package org.onebusway.gtfs_realtime.exporter;

import javax.inject.Singleton;

import com.google.protobuf.Message;

@Singleton
public class TripUpdatesFileWriter extends AbstractGtfsRealtimeFileWriter {

  @Override
  protected Message getMessage() {
    return _provider.getTripUpdates();
  }
}
