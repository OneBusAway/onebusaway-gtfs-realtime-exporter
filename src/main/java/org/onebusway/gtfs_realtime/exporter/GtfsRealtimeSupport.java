package org.onebusway.gtfs_realtime.exporter;

import com.google.transit.realtime.GtfsRealtimeConstants;
import com.google.transit.realtime.GtfsRealtime.FeedHeader;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.FeedHeader.Incrementality;

public class GtfsRealtimeSupport {

  public static FeedMessage.Builder createFeedMessageBuilder() {
    long now = System.currentTimeMillis();
    FeedHeader.Builder header = FeedHeader.newBuilder();
    header.setTimestamp(now);
    header.setIncrementality(Incrementality.FULL_DATASET);
    header.setGtfsRealtimeVersion(GtfsRealtimeConstants.VERSION);
    FeedMessage.Builder feedMessageBuilder = FeedMessage.newBuilder();
    feedMessageBuilder.setHeader(header);
    return feedMessageBuilder;
  }
}
