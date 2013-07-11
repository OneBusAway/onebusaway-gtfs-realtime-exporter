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

import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.FeedHeader.Incrementality;

/**
 * Defines an interface for receiving incremental GTFS-realtime updates.
 * 
 * @author bdferris
 * 
 */
public interface GtfsRealtimeIncrementalListener {

  /**
   * Handle a feed update. Updates can either be full datasets (as indicated by
   * a {@link Incrementality#FULL_DATASET} value in the header) or incremental
   * updates to the most-recent full dataset (as indicated by a
   * {@link Incrementality#DIFFERENTIAL} value in the header).
   * 
   * @param feed the feed update
   */
  public void handleFeed(FeedMessage feed);
}
