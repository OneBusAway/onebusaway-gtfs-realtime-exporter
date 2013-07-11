/**
 * Copyright (C) 2013 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onebusaway.gtfs_realtime.exporter;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.transit.realtime.GtfsRealtimeOneBusAway;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.FeedHeader.Incrementality;
import com.google.transit.realtime.GtfsRealtimeOneBusAway.OneBusAwayFeedHeader;

public class GtfsRealtimeExporterImplTest {

  private GtfsRealtimeExporterImpl _exporter;

  private ListenerImpl _listener;

  @Before
  public void setup() {
    _exporter = new GtfsRealtimeExporterImpl();
    _listener = new ListenerImpl();
  }

  @Test
  public void testFullUpdate() {
    GtfsRealtimeFullUpdate update = new GtfsRealtimeFullUpdate();
    FeedEntity.Builder feedEntity = FeedEntity.newBuilder();
    feedEntity.setId("v123");
    update.addEntity(feedEntity.build());

    _exporter.handleFullUpdate(update);

    FeedMessage feed = _exporter.getFeed();
    assertEquals(Incrementality.FULL_DATASET,
        feed.getHeader().getIncrementality());
    OneBusAwayFeedHeader obaHeader = feed.getHeader().getExtension(
        GtfsRealtimeOneBusAway.obaFeedHeader);
    assertEquals(1, obaHeader.getIncrementalIndex());
    assertEquals(1, feed.getEntityCount());
    assertEquals("v123", feed.getEntity(0).getId());

    _exporter.addIncrementalListener(_listener);
    assertSame(feed, _listener.getFeed());

    update = new GtfsRealtimeFullUpdate();
    feedEntity = FeedEntity.newBuilder();
    feedEntity.setId("v456");
    update.addEntity(feedEntity.build());

    _exporter.handleFullUpdate(update);

    feed = _exporter.getFeed();
    obaHeader = feed.getHeader().getExtension(
        GtfsRealtimeOneBusAway.obaFeedHeader);
    assertEquals(2, obaHeader.getIncrementalIndex());
    assertEquals(1, feed.getEntityCount());
    assertEquals("v456", feed.getEntity(0).getId());
    assertSame(feed, _listener.getFeed());
  }

  @Test
  public void testIncrementalUpdate() {
    {
      GtfsRealtimeIncrementalUpdate update = new GtfsRealtimeIncrementalUpdate();
      FeedEntity.Builder feedEntity = FeedEntity.newBuilder();
      feedEntity.setId("v123");
      update.addUpdatedEntity(feedEntity.build());

      _exporter.handleIncrementalUpdate(update);
    }

    {
      FeedMessage feed = _exporter.getFeed();
      assertEquals(Incrementality.FULL_DATASET,
          feed.getHeader().getIncrementality());
      OneBusAwayFeedHeader obaHeader = feed.getHeader().getExtension(
          GtfsRealtimeOneBusAway.obaFeedHeader);
      assertEquals(1, obaHeader.getIncrementalIndex());
      assertEquals(1, feed.getEntityCount());
      assertEquals("v123", feed.getEntity(0).getId());
    }

    {
      GtfsRealtimeIncrementalUpdate update = new GtfsRealtimeIncrementalUpdate();
      FeedEntity.Builder feedEntity = FeedEntity.newBuilder();
      feedEntity.setId("v456");
      update.addUpdatedEntity(feedEntity.build());
      update.addDeletedEntity("v123");

      _exporter.handleIncrementalUpdate(update);
    }

    {
      FeedMessage feed = _exporter.getFeed();
      OneBusAwayFeedHeader obaHeader = feed.getHeader().getExtension(
          GtfsRealtimeOneBusAway.obaFeedHeader);
      assertEquals(2, obaHeader.getIncrementalIndex());
      assertEquals(1, feed.getEntityCount());
      assertEquals("v456", feed.getEntity(0).getId());
    }

    _exporter.addIncrementalListener(_listener);

    assertSame(_exporter.getFeed(), _listener.getFeed());

    {
      GtfsRealtimeIncrementalUpdate update = new GtfsRealtimeIncrementalUpdate();
      FeedEntity.Builder feedEntity = FeedEntity.newBuilder();
      feedEntity.setId("v789");
      update.addUpdatedEntity(feedEntity.build());
      update.addDeletedEntity("v456");

      _exporter.handleIncrementalUpdate(update);
    }

    {
      FeedMessage feed = _listener.getFeed();
      assertEquals(Incrementality.DIFFERENTIAL,
          feed.getHeader().getIncrementality());
      OneBusAwayFeedHeader obaHeader = feed.getHeader().getExtension(
          GtfsRealtimeOneBusAway.obaFeedHeader);
      assertEquals(3, obaHeader.getIncrementalIndex());
      assertEquals(2, feed.getEntityCount());
      assertEquals("v789", feed.getEntity(0).getId());
      assertEquals("v456", feed.getEntity(1).getId());
      assertTrue(feed.getEntity(1).getIsDeleted());
    }
  }

  private static class ListenerImpl implements GtfsRealtimeIncrementalListener {

    private FeedMessage _feed;

    public FeedMessage getFeed() {
      return _feed;
    }

    @Override
    public void handleFeed(FeedMessage feed) {
      _feed = feed;
    }
  }
}
