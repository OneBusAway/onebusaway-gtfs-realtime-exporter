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

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedHeader;

public interface GtfsRealtimeSink {

  /**
   * The exporter library will automatically fill in necessary
   * {@link FeedHeader} fields when exporting a feed. However, you may wish to
   * specify some additional fields to be included by default (eg. extensions).
   * You may specify a partially constructed {@link FeedHeader} object here that
   * will be used as a basis for constructing the actual headers sent to
   * clients.
   * 
   * @param header a partially constructed header
   */
  public void setFeedHeaderDefaults(FeedHeader header);

  /**
   * Send a full dataset update. The feed entities in the update will replace
   * all previous updates received so far.
   * 
   * @param update
   */
  public void handleFullUpdate(GtfsRealtimeFullUpdate update);

  /**
   * Send an incremental update. The feed entities will selectively replace any
   * previously updated feed entities. It is important to use stable
   * {@link FeedEntity#getId()} id values across updates in order for
   * incremental updates to function properly.
   * 
   * @param update
   */
  public void handleIncrementalUpdate(GtfsRealtimeIncrementalUpdate update);
}
