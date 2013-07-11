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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.transit.realtime.GtfsRealtime.FeedHeader;
import com.google.transit.realtime.GtfsRealtime.FeedHeader.Incrementality;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtimeConstants;

public class GtfsRealtimeFileWriterTest {

  private GtfsRealtimeFileWriter _writer;

  private GtfsRealtimeSource _source;

  private ScheduledExecutorService _executor;

  private File _path;

  @Before
  public void setup() throws IOException {
    _writer = new GtfsRealtimeFileWriter();

    _source = Mockito.mock(GtfsRealtimeSource.class);
    _writer.setSource(_source);

    _executor = Mockito.mock(ScheduledExecutorService.class);
    _writer.setExecutor(_executor);

    _path = File.createTempFile(GtfsRealtimeFileWriterTest.class.getName(),
        "-FeedMessage.pb");
    _path.delete();
    _path.deleteOnExit();
    _writer.setPath(_path);
  }

  @Test
  public void test() throws IOException {
    ArgumentCaptor<Runnable> captureRunnable = ArgumentCaptor.forClass(Runnable.class);
    Mockito.when(
        _executor.scheduleAtFixedRate(captureRunnable.capture(),
            Mockito.eq(0L), Mockito.eq(5L), Mockito.eq(TimeUnit.SECONDS))).thenReturn(
        null);
    _writer.start();

    FeedMessage.Builder feed = FeedMessage.newBuilder();
    FeedHeader.Builder header = feed.getHeaderBuilder();
    header.setIncrementality(Incrementality.FULL_DATASET);
    header.setTimestamp(1234L);
    header.setGtfsRealtimeVersion(GtfsRealtimeConstants.VERSION);

    Mockito.when(_source.getFeed()).thenReturn(feed.build());

    Runnable writerTask = captureRunnable.getValue();
    writerTask.run();

    assertTrue(_path.exists());

    InputStream in = new FileInputStream(_path);
    FeedMessage actualFeed = FeedMessage.parseFrom(in);
    assertEquals(header.getTimestamp(), actualFeed.getHeader().getTimestamp());
  }
}
