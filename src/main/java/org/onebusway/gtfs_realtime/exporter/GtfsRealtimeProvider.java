package org.onebusway.gtfs_realtime.exporter;

import com.google.transit.realtime.GtfsRealtime.FeedMessage;

public interface GtfsRealtimeProvider {
  
  public FeedMessage getTripUpdates();

  public FeedMessage getVehiclePositions();

  public FeedMessage getAlerts();
}
