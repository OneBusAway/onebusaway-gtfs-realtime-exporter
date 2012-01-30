package org.onebusway.gtfs_realtime.exporter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class GtfsRealtimeExporterModule extends AbstractModule {

  public static final String NAME_EXECUTOR = "org.onebusway.gtfs_realtime.exporter.GtfsRealtimeExporterModule.executor";

  @Override
  protected void configure() {
    bind(AlertsFileWriter.class);
    bind(TripUpdatesFileWriter.class);
    bind(VehiclePositionsFileWriter.class);
    bind(AlertsServlet.class);
    bind(TripUpdatesServlet.class);
    bind(VehiclePositionsServlet.class);
    bind(ScheduledExecutorService.class).annotatedWith(
        Names.named(NAME_EXECUTOR)).toInstance(
        Executors.newSingleThreadScheduledExecutor());
  }
}
