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

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Singleton;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;

@Singleton
public class GtfsRealtimeProviderImpl implements GtfsRealtimeMutableProvider {

  private volatile FeedMessage _tripUpdates = GtfsRealtimeLibrary.createFeedMessageBuilder().build();

  private volatile FeedMessage _vehiclePositions = GtfsRealtimeLibrary.createFeedMessageBuilder().build();

  private volatile FeedMessage _alerts = GtfsRealtimeLibrary.createFeedMessageBuilder().build();

  private List<GtfsRealtimeListener> _listeners = new ArrayList<GtfsRealtimeListener>();

  public void setTripUpdates(FeedMessage tripUpdates) {
    setTripUpdates(tripUpdates, true);
  }

  public void setTripUpdates(FeedMessage tripUpdates, boolean fireUpdate) {
    _tripUpdates = tripUpdates;
    if (fireUpdate) {
      fireUpdate();
    }
  }

  public void setVehiclePositions(FeedMessage vehiclePositions) {
    setVehiclePositions(vehiclePositions, true);
  }

  public void setVehiclePositions(FeedMessage vehiclePositions,
      boolean fireUpdate) {
    _vehiclePositions = vehiclePositions;
    if (fireUpdate) {
      fireUpdate();
    }
  }

  public void setAlerts(FeedMessage alerts) {
    setAlerts(alerts, true);
  }

  public void setAlerts(FeedMessage alerts, boolean fireUpdate) {
    _alerts = alerts;
    if (fireUpdate) {
      fireUpdate();
    }
  }

  public void fireUpdate() {
    for (GtfsRealtimeListener listener : _listeners) {
      listener.hadUpdate(this);
    }
  }

  /****
   * {@link GtfsRealtimeProvider} Interface
   ****/

  @Override
  public FeedMessage getTripUpdates() {
    return _tripUpdates;
  }

  @Override
  public FeedMessage getVehiclePositions() {
    return _vehiclePositions;
  }

  @Override
  public FeedMessage getAlerts() {
    return _alerts;
  }

  @Override
  public void addGtfsRealtimeListener(GtfsRealtimeListener listener) {
    _listeners.add(listener);
  }

  @Override
  public void removeGtfsRealtimeListener(GtfsRealtimeListener listener) {
    _listeners.remove(listener);
  }
}
