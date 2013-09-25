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

import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeGuiceBindingTypes.VehiclePositions;
import org.onebusaway.guice.jsr250.JSR250Module;
import org.onebusaway.guice.jsr250.LifecycleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.protobuf.ExtensionRegistry;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.VehiclePosition;
import com.google.transit.realtime.GtfsRealtimeExtensions;

public class DeadlockTest {

  private static final Logger _log = LoggerFactory.getLogger(DeadlockTest.class);

  private static ExtensionRegistry _extensionRegistry = ExtensionRegistry.newInstance();

  private GtfsRealtimeExporter _exporter;

  private LifecycleService _lifecycleService = null;

  @BeforeClass
  public static void beforeClass() {
    GtfsRealtimeExtensions.registerExtensions(_extensionRegistry);
  }

  @After
  public void after() {
    if (_lifecycleService != null) {
      _lifecycleService.stop();
    }
  }

  @Inject
  public void setExporter(@VehiclePositions
  GtfsRealtimeExporter exporter) {
    _exporter = exporter;
  }

  @Inject
  public void setLifecycleService(LifecycleService lifecycleService) {
    _lifecycleService = lifecycleService;
  }

  @Test
  public void testIncrementalFeed() throws Exception {
    Set<Module> modules = new HashSet<Module>();
    GtfsRealtimeExporterModule.addModuleAndDependencies(modules);
    JSR250Module.addModuleAndDependencies(modules);
    Injector injector = Guice.createInjector(modules);
    injector.injectMembers(this);

    GtfsRealtimeServlet servlet = injector.getInstance(GtfsRealtimeServlet.class);
    servlet.setSource(_exporter);
    servlet.setUrl(getUrl());

    _lifecycleService.start();

    Thread dataSourceThread = new Thread(new DataSource(_exporter));
    dataSourceThread.start();

    WebSocketClient client = new WebSocketClient();
    DataSink sink = new DataSink();

    for (int i = 0; i < 20; ++i) {
      _log.info("connect");
      client.start();
      client.connect(sink, getWebsocketUri());
      for (int j = 0; j < 2; ++j) {
        long beforeCount = sink.getMessageCount();
        Thread.sleep(1000);
        long afterCount = sink.getMessageCount();
        _log.info("message count=" + (afterCount - beforeCount));
        assertTrue(afterCount - beforeCount > 5);
      }
      _log.info("disconnect");
      client.stop();
    }
  }

  private static URL getUrl() {
    try {
      String port = System.getProperty("org_onebusaway_test_port", "8080");
      return new URL("http://localhost:" + port + "/my-servlet");
    } catch (MalformedURLException ex) {
      throw new IllegalStateException(ex);
    }
  }

  private static URI getWebsocketUri() {
    try {
      String port = System.getProperty("org_onebusaway_test_port", "8080");
      return new URI("ws://localhost:" + port + "/my-servlet");
    } catch (URISyntaxException ex) {
      throw new IllegalStateException(ex);
    }
  }

  /**
   * Sends incremental feed messages as fast as it can.
   */
  private static class DataSource implements Runnable {

    private GtfsRealtimeSink _sink;

    public DataSource(GtfsRealtimeSink sink) {
      _sink = sink;
    }

    @Override
    public void run() {
      FeedEntity.Builder entity = FeedEntity.newBuilder();
      entity.setId("tacos");
      VehiclePosition.Builder position = VehiclePosition.newBuilder();
      entity.setVehicle(position);
      final FeedEntity feedEntity = entity.build();

      while (!Thread.interrupted()) {
        GtfsRealtimeIncrementalUpdate update = new GtfsRealtimeIncrementalUpdate();
        update.addUpdatedEntity(feedEntity);
        _sink.handleIncrementalUpdate(update);
      }
    }
  }

  /**
   * Counts messages received from a GTFS-realtime incremental feed WebSocket.
   */
  @WebSocket
  public static class DataSink {

    private AtomicLong _messageCount = new AtomicLong();

    public long getMessageCount() {
      return _messageCount.get();
    }

    @OnWebSocketMessage
    public void onMessage(byte[] data, int offset, int length) {
      _messageCount.incrementAndGet();
    }
  }
}
