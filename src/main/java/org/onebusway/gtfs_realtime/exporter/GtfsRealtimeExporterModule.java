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
package org.onebusway.gtfs_realtime.exporter;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.onebusaway.guice.jetty_exporter.JettyExporterModule;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.name.Names;

public class GtfsRealtimeExporterModule extends AbstractModule {

  public static final String NAME_EXECUTOR = "org.onebusway.gtfs_realtime.exporter.GtfsRealtimeExporterModule.executor";

  public static void addModuleAndDependencies(Set<Module> modules) {
    modules.add(new GtfsRealtimeExporterModule());
    JettyExporterModule.addModuleAndDependencies(modules);
  }

  @Override
  protected void configure() {
    bind(GtfsRealtimeProvider.class).to(GtfsRealtimeProviderImpl.class);
    bind(GtfsRealtimeMutableProvider.class).to(GtfsRealtimeProviderImpl.class);
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
