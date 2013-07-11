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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/**
 * We use Guice for wiring up dependices in the
 * onebusaway-gtfs-realtime-exporter library. For most clients, that means
 * injecting instances of {@link GtfsRealtimeSink}, {@link GtfsRealtimeSource},
 * and {@link GtfsRealtimeExporter}. Typically, clients may produce different
 * combinations of GTFS-realtime feeds: alerts, trip-updates, and
 * vehicles-positions, each modeled with a separate feed. Each feed type needs
 * shared instances of {@link GtfsRealtimeSink} or {@link GtfsRealtimeSource}
 * specific to that feed type.
 * 
 * To achieve this, we use Guice Binding Annotations to indicate which type of
 * feed we'd like. For example, for a {@link GtfsRealtimeSink} for trip-updates,
 * we'd use the following:
 * 
 * <pre>
 * {@literal @}Inject
 * void setTripUpdatesSink({@literal @}TripUpdates GtfsRealtimeSink tripUpdatesSink) {
 *   ...
 * }
 * </pre>
 * 
 * Note the use of the {@link TripUpdates} annotation on the injection
 * parameter. Any instance of {@link GtfsRealtimeSink},
 * {@link GtfsRealtimeSource}, or {@link GtfsRealtimeExporter} that is annotated
 * with the {@link TripUpdates} annotation will receive the same underlying
 * instance. We provide annotations for {@link Alerts}, {@link VehiclePositions}
 * , and finally a {@link MixedFeed} if you wish to mix different GTFS-realtime
 * feed entity types in the same feed.
 * 
 * Note: one limitation of this approach is that it's tricky to exporter
 * multiple different feeds of the same type using the library (eg. two
 * trip-updates feeds).
 * 
 * @author bdferris
 */
public class GtfsRealtimeGuiceBindingTypes {

  /**
   * Annotation to indicate that a wired instance of {@link GtfsRealtimeSink},
   * {@link GtfsRealtimeSource}, or {@link GtfsRealtimeExporter} must support
   * trip updates data.
   * 
   * @author bdferris
   * @see GtfsRealtimeGuiceBindingTypes
   */
  @BindingAnnotation
  @Target({FIELD, PARAMETER, METHOD})
  @Retention(RUNTIME)
  public @interface TripUpdates {
  }

  /**
   * Annotation to indicate that a wired instance of {@link GtfsRealtimeSink},
   * {@link GtfsRealtimeSource}, or {@link GtfsRealtimeExporter} must support
   * vehicle positions data.
   * 
   * @author bdferris
   * @see GtfsRealtimeGuiceBindingTypes
   */
  @BindingAnnotation
  @Target({FIELD, PARAMETER, METHOD})
  @Retention(RUNTIME)
  public @interface VehiclePositions {
  }

  /**
   * Annotation to indicate that a wired instance of {@link GtfsRealtimeSink},
   * {@link GtfsRealtimeSource}, or {@link GtfsRealtimeExporter} must support
   * alerts data.
   * 
   * @author bdferris
   * @see GtfsRealtimeGuiceBindingTypes
   */
  @BindingAnnotation
  @Target({FIELD, PARAMETER, METHOD})
  @Retention(RUNTIME)
  public @interface Alerts {
  }

  /**
   * Annotation to indicate that a wired instance of {@link GtfsRealtimeSink},
   * {@link GtfsRealtimeSource}, or {@link GtfsRealtimeExporter} must support a
   * mixture of trip updates, vehicle positions, and alerts data.
   * 
   * @author bdferris
   * @see GtfsRealtimeGuiceBindingTypes
   */
  @BindingAnnotation
  @Target({FIELD, PARAMETER, METHOD})
  @Retention(RUNTIME)
  public @interface MixedFeed {
  }
}
