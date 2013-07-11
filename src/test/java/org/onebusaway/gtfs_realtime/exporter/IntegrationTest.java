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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocket.Connection;
import org.eclipse.jetty.websocket.WebSocket.OnBinaryMessage;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onebusaway.gtfs_realtime.exporter.GtfsRealtimeGuiceBindingTypes.VehiclePositions;
import org.onebusaway.guice.jsr250.JSR250Module;
import org.onebusaway.guice.jsr250.LifecycleService;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedHeader;
import com.google.transit.realtime.GtfsRealtime.FeedHeader.Incrementality;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.VehiclePosition;
import com.google.transit.realtime.GtfsRealtimeExtensions;
import com.google.transit.realtime.GtfsRealtimeOneBusAway;
import com.google.transit.realtime.GtfsRealtimeOneBusAway.OneBusAwayFeedHeader;

public class IntegrationTest {

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
  public void testFullFeed() throws IOException, InterruptedException {
    URL url = getUrl();

    File path = File.createTempFile(getClass().getName() + "-", ".pb");
    path.delete();
    path.deleteOnExit();

    Set<Module> modules = new HashSet<Module>();
    GtfsRealtimeExporterModule.addModuleAndDependencies(modules);
    JSR250Module.addModuleAndDependencies(modules);
    Injector injector = Guice.createInjector(modules);
    injector.injectMembers(this);

    GtfsRealtimeServlet servlet = injector.getInstance(GtfsRealtimeServlet.class);
    servlet.setSource(_exporter);
    servlet.setUrl(url);

    GtfsRealtimeFileWriter fileWriter = injector.getInstance(GtfsRealtimeFileWriter.class);
    fileWriter.setSource(_exporter);
    fileWriter.setPath(path);

    _lifecycleService.start();

    {
      GtfsRealtimeIncrementalUpdate update = new GtfsRealtimeIncrementalUpdate();
      FeedEntity.Builder entity = FeedEntity.newBuilder();
      entity.setId("tacos");
      VehiclePosition.Builder position = VehiclePosition.newBuilder();
      entity.setVehicle(position);
      update.addUpdatedEntity(entity.build());
      _exporter.handleIncrementalUpdate(update);
    }

    Thread.sleep((fileWriter.getPeriod() + 1) * 1000);

    FeedMessage feed = FeedMessage.parseFrom(new FileInputStream(path));
    assertEquals(1, feed.getEntityCount());
    assertEquals("tacos", feed.getEntity(0).getId());

    feed = FeedMessage.parseFrom(url.openStream());
    assertEquals(1, feed.getEntityCount());
    assertEquals("tacos", feed.getEntity(0).getId());

    {
      GtfsRealtimeIncrementalUpdate update = new GtfsRealtimeIncrementalUpdate();
      FeedEntity.Builder entity = FeedEntity.newBuilder();
      entity.setId("nachos");
      VehiclePosition.Builder position = VehiclePosition.newBuilder();
      entity.setVehicle(position);
      update.addUpdatedEntity(entity.build());
      _exporter.handleIncrementalUpdate(update);
    }

    Thread.sleep((fileWriter.getPeriod() + 1) * 1000);

    feed = FeedMessage.parseFrom(new FileInputStream(path));
    assertEquals(2, feed.getEntityCount());

    feed = FeedMessage.parseFrom(url.openStream());
    assertEquals(2, feed.getEntityCount());
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

    {
      GtfsRealtimeIncrementalUpdate update = new GtfsRealtimeIncrementalUpdate();
      FeedEntity.Builder entity = FeedEntity.newBuilder();
      entity.setId("tacos");
      VehiclePosition.Builder position = VehiclePosition.newBuilder();
      entity.setVehicle(position);
      update.addUpdatedEntity(entity.build());
      _exporter.handleIncrementalUpdate(update);
    }

    WebSocketClientFactory factory = new WebSocketClientFactory();
    factory.start();
    WebSocketClient client = factory.newWebSocketClient();
    GtfsRealtimeSocket socket = new GtfsRealtimeSocket();
    Future<Connection> future = client.open(getWebsocketUri(), socket);
    future.get(10, TimeUnit.SECONDS);

    CountDownLatch latch = socket.setLatch(1);
    latch.await(2, TimeUnit.SECONDS);

    List<FeedMessage> feeds = socket.getFeeds();
    assertEquals(1, feeds.size());
    FeedMessage feed = feeds.get(0);
    FeedHeader header = feed.getHeader();
    assertEquals(Incrementality.FULL_DATASET, header.getIncrementality());
    OneBusAwayFeedHeader obaHeader = header.getExtension(GtfsRealtimeOneBusAway.obaFeedHeader);
    long index = obaHeader.getIncrementalIndex();
    assertEquals(1, index);
    assertEquals(1, feed.getEntityCount());

    latch = socket.setLatch(1);

    {
      GtfsRealtimeIncrementalUpdate update = new GtfsRealtimeIncrementalUpdate();
      FeedEntity.Builder entity = FeedEntity.newBuilder();
      entity.setId("nachos");
      VehiclePosition.Builder position = VehiclePosition.newBuilder();
      entity.setVehicle(position);
      update.addUpdatedEntity(entity.build());
      _exporter.handleIncrementalUpdate(update);
    }

    latch.await();

    assertEquals(2, feeds.size());
    feed = feeds.get(1);
    header = feed.getHeader();
    assertEquals(Incrementality.DIFFERENTIAL, header.getIncrementality());
    obaHeader = header.getExtension(GtfsRealtimeOneBusAway.obaFeedHeader);
    assertEquals(2, obaHeader.getIncrementalIndex());
    assertEquals(1, feed.getEntityCount());
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

  private static class GtfsRealtimeSocket implements WebSocket, OnBinaryMessage {

    private CountDownLatch _latch = null;

    private List<FeedMessage> _feeds = new ArrayList<FeedMessage>();

    public List<FeedMessage> getFeeds() {
      return _feeds;
    }

    public CountDownLatch setLatch(int count) {
      _latch = new CountDownLatch(count);
      return _latch;
    }

    @Override
    public void onOpen(Connection connection) {

    }

    @Override
    public void onClose(int closeCode, String message) {

    }

    @Override
    public void onMessage(byte[] data, int offset, int length) {
      try {
        FeedMessage feed = FeedMessage.parseFrom(
            ByteString.copyFrom(data, offset, length), _extensionRegistry);
        _feeds.add(feed);
        if (_latch != null) {
          _latch.countDown();
        }
      } catch (InvalidProtocolBufferException ex) {
        throw new IllegalStateException(ex);
      }
    }
  }
}
