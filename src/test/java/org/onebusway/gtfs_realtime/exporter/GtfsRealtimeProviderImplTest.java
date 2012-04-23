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

import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.transit.realtime.GtfsRealtime.FeedMessage;

public class GtfsRealtimeProviderImplTest {

  private GtfsRealtimeProviderImpl _provider;

  private GtfsRealtimeListener _listener;

  @Before
  public void before() {
    _provider = new GtfsRealtimeProviderImpl();

    _listener = Mockito.mock(GtfsRealtimeListener.class);
    _provider.addGtfsRealtimeListener(_listener);
  }

  @Test
  public void testSetAlerts() {
    FeedMessage alertsA = GtfsRealtimeLibrary.createFeedMessageBuilder().build();
    _provider.setAlerts(alertsA);
    assertSame(alertsA, _provider.getAlerts());

    FeedMessage alertsB = GtfsRealtimeLibrary.createFeedMessageBuilder().build();
    _provider.setAlerts(alertsB, true);
    assertSame(alertsB, _provider.getAlerts());

    Mockito.verify(_listener, Mockito.times(2)).hadUpdate(_provider);

    FeedMessage alertsC = GtfsRealtimeLibrary.createFeedMessageBuilder().build();
    _provider.setAlerts(alertsC, false);
    assertSame(alertsC, _provider.getAlerts());

    Mockito.verifyNoMoreInteractions(_listener);
  }

  @Test
  public void testFireUpdate() {

    _provider.fireUpdate();

    Mockito.verify(_listener).hadUpdate(_provider);
    Mockito.verifyNoMoreInteractions(_listener);
  }

}
