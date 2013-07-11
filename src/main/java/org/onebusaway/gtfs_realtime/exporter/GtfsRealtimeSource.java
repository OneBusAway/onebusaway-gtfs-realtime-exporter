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

import com.google.transit.realtime.GtfsRealtime.FeedMessage;

/**
 * Provides a source of GTFS-realtime data, providing methods to receive a full
 * data set view of a feed, along with incremental updates as well.
 * 
 * @author bdferris
 * @see GtfsRealtimeIncrementalListener
 */
public interface GtfsRealtimeSource {

  /**
   * @return the full-dataset view of a GTFS-realtime feed
   */
  public FeedMessage getFeed();

  /**
   * Register a new incremental GTFS-realtime listener.
   * 
   * @param listener
   */
  public void addIncrementalListener(GtfsRealtimeIncrementalListener listener);

  /**
   * Un-register a new incremental GTFS-realtime listener.
   * 
   * @param listener
   */
  public void removeIncrementalListener(GtfsRealtimeIncrementalListener listener);
}
