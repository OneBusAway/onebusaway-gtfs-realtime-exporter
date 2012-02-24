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

import com.google.transit.realtime.GtfsRealtime.TranslatedString;
import com.google.transit.realtime.GtfsRealtime.TranslatedString.Translation;
import com.google.transit.realtime.GtfsRealtimeConstants;
import com.google.transit.realtime.GtfsRealtime.FeedHeader;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.FeedHeader.Incrementality;

/**
 * Provides a number of convenience methods for working with GTFS-realtime data.
 * 
 * @author bdferris
 * 
 */
public class GtfsRealtimeLibrary {

  /**
   * Constructs a new {@link FeedMessage.Builder} that can be used to build a
   * new GTFS-realtime feed message. The {@link FeedHeader} will already be
   * filled in as a {@link Incrementality#FULL_DATASET} and the timestamp of the
   * feed will be set to NOW. This is the minimal requirement for an empty feed
   * so the feed could be returned 'as-is' at this point.
   * 
   * 
   * @return a new feed message builder with a header already populated
   */
  public static FeedMessage.Builder createFeedMessageBuilder() {
    long now = System.currentTimeMillis();
    FeedHeader.Builder header = FeedHeader.newBuilder();
    header.setTimestamp(now);
    header.setIncrementality(Incrementality.FULL_DATASET);
    header.setGtfsRealtimeVersion(GtfsRealtimeConstants.VERSION);
    FeedMessage.Builder feedMessageBuilder = FeedMessage.newBuilder();
    feedMessageBuilder.setHeader(header);
    return feedMessageBuilder;
  }

  /**
   * @param text the text to include in the translated string
   * @return a new {@link TranslatedString} with just a single translation in
   *         the default language of the feed using the specified text.
   */
  public static TranslatedString getTextAsTranslatedString(String text) {
    TranslatedString.Builder builder = TranslatedString.newBuilder();
    Translation.Builder translation = Translation.newBuilder();
    translation.setText(text);
    builder.addTranslation(translation);
    return builder.build();
  }
}
