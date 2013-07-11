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

import java.util.ArrayList;
import java.util.List;

import com.google.transit.realtime.GtfsRealtime.FeedEntity;

/**
 * Specifies an incremental update to a GTFS-realtime feed, such that new feed
 * entities will selectively replace any previously updated feed entities. It is
 * important to use stable {@link FeedEntity#getId()} id values across updates
 * in order for incremental updates to function properly.
 * 
 * @author bdferris
 * @see GtfsRealtimeFullUpdate
 * @see GtfsRealtimeSink
 */
public class GtfsRealtimeIncrementalUpdate {

  private List<FeedEntity> updatedEntities = new ArrayList<FeedEntity>();

  private List<String> deletedEntities = new ArrayList<String>();

  private long expirationTime = -1;

  public void addUpdatedEntity(FeedEntity entity) {
    updatedEntities.add(entity);
  }

  public List<FeedEntity> getUpdatedEntities() {
    return updatedEntities;
  }

  public void addDeletedEntity(String entityId) {
    deletedEntities.add(entityId);
  }

  public List<String> getDeletedEntities() {
    return deletedEntities;
  }

  public long getExpirationTime() {
    return expirationTime;
  }

  public void setExpirationTime(long expirationTimeInMilliseconds) {
    this.expirationTime = expirationTimeInMilliseconds;
  }

}
