/**
 * Copyright (C) 2012 Google, Inc.
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

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeExporter.AlertsExporter;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeExporter.MixedFeedExporter;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeExporter.TripUpdatesExporter;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeExporter.VehiclePositionsExporter;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeGuiceBindingTypes.Alerts;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeGuiceBindingTypes.MixedFeed;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeGuiceBindingTypes.TripUpdates;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeGuiceBindingTypes.VehiclePositions;
import org.onebusaway.guice.jetty_exporter.JettyExporterModule;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

/**
 * Provides Guice support for wiring up the services provided by the
 * onebusaway-gtfs-realtime-exporter library, along with all its dependencies.
 * 
 * Here's a quick example of the library in use:
 * 
 * <pre>
 * {@code
 * Set<Module> modules = new HashSet<Module>();
 * GtfsRealtimeExporterModule.addModuleAndDependencies(modules);
 * Injector injector = Guice.createInjector(modules);
 * }
 * </pre>
 * 
 * @author bdferris
 */
public class GtfsRealtimeExporterModule extends AbstractModule {

  public static final String NAME_EXECUTOR = "org.onebusway.gtfs_realtime.exporter.GtfsRealtimeExporterModule.executor";

  /**
   * Adds a {@link GtfsRealtimeExporterModule} instance to the specified set of
   * modules, along with all its dependencies.
   * 
   * @param modules the resulting set of Guice modules
   */
  public static void addModuleAndDependencies(Set<Module> modules) {
    modules.add(new GtfsRealtimeExporterModule());
    JettyExporterModule.addModuleAndDependencies(modules);
  }

  /**
   * See {@link GtfsRealtimeExporter} for a discussion of the somewhat
   * convoluted binding scheme used here.
   */
  @Override
  protected void configure() {

    bind(GtfsRealtimeSink.class).annotatedWith(Alerts.class).to(
        AlertsExporter.class);
    bind(GtfsRealtimeSource.class).annotatedWith(Alerts.class).to(
        AlertsExporter.class);
    bind(GtfsRealtimeExporter.class).annotatedWith(Alerts.class).to(
        AlertsExporter.class);
    bind(AlertsExporter.class).to(GtfsRealtimeExporterImpl.class).in(
        Singleton.class);

    bind(GtfsRealtimeSink.class).annotatedWith(TripUpdates.class).to(
        TripUpdatesExporter.class);
    bind(GtfsRealtimeSource.class).annotatedWith(TripUpdates.class).to(
        TripUpdatesExporter.class);
    bind(GtfsRealtimeExporter.class).annotatedWith(TripUpdates.class).to(
        TripUpdatesExporter.class);
    bind(TripUpdatesExporter.class).to(GtfsRealtimeExporterImpl.class).in(
        Singleton.class);

    bind(GtfsRealtimeSink.class).annotatedWith(VehiclePositions.class).to(
        VehiclePositionsExporter.class);
    bind(GtfsRealtimeSource.class).annotatedWith(VehiclePositions.class).to(
        VehiclePositionsExporter.class);
    bind(GtfsRealtimeExporter.class).annotatedWith(VehiclePositions.class).to(
        VehiclePositionsExporter.class);
    bind(VehiclePositionsExporter.class).to(GtfsRealtimeExporterImpl.class).in(
        Singleton.class);

    bind(GtfsRealtimeSink.class).annotatedWith(MixedFeed.class).to(
        MixedFeedExporter.class);
    bind(GtfsRealtimeSource.class).annotatedWith(MixedFeed.class).to(
        MixedFeedExporter.class);
    bind(MixedFeedExporter.class).to(GtfsRealtimeExporterImpl.class).in(
        Singleton.class);

    bind(ScheduledExecutorService.class).annotatedWith(
        Names.named(NAME_EXECUTOR)).toInstance(
        Executors.newSingleThreadScheduledExecutor());
  }

  /**
   * Implement hashCode() and equals() such that two instances of the module
   * will be equal.
   */
  @Override
  public int hashCode() {
    return this.getClass().hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null)
      return false;
    return this.getClass().equals(o.getClass());
  }
}
