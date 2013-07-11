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

import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeGuiceBindingTypes.TripUpdates;

/**
 * Convenience interface that combines {@link GtfsRealtimeSource} and
 * {@link GtfsRealtimeSink} into one interface.
 * 
 * @author bdferris
 */
public interface GtfsRealtimeExporter extends GtfsRealtimeSource,
    GtfsRealtimeSink {

  /**
   * A note on {@link AlertsExporter}, {@link TripUpdatesExporter},
   * {@link VehiclePositionsExporter} and {@link MixedFeedExporter}:
   * 
   * In order to support the use of {@link GtfsRealtimeGuiceBindingTypes}
   * annotations, where each annotation of the same time is backed by the same
   * singleton instance (eg. {@link TripUpdates}), it was necessary to create
   * sub-interfaces of {@link GtfsRealtimeExporter} for each of the
   * {@link GtfsRealtimeGuiceBindingTypes} such that singleton-scoped instances
   * could be bound to each of the sub-interfaces. Maybe someday Guice will
   * support this directly?
   * 
   * See {@link GtfsRealtimeExporterModule} for more details.
   */

  interface AlertsExporter extends GtfsRealtimeExporter {

  }

  interface TripUpdatesExporter extends GtfsRealtimeExporter {

  }

  interface VehiclePositionsExporter extends GtfsRealtimeExporter {

  }

  interface MixedFeedExporter extends GtfsRealtimeExporter {

  }
}
