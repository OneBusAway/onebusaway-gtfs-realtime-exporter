/**
 * Copyright (C) 2011 Google, Inc.
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;
import org.onebusaway.guice.jetty_exporter.ServletSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Message;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;

/**
 * Provides functionality to export a GTFS-realtime feed via HTTP, with support
 * for both traditional HTTP GET requests and also incremental requests via
 * WebSockets.
 * 
 * @author bdferris
 */
public class GtfsRealtimeServlet extends WebSocketServlet implements
    ServletSource {

  private static final long serialVersionUID = 1L;

  private static final String CONTENT_TYPE = "application/x-google-protobuf";

  private static final Logger _log = LoggerFactory.getLogger(GtfsRealtimeServlet.class);

  protected GtfsRealtimeSource _source;

  private URL _url;

  public void setSource(GtfsRealtimeSource source) {
    _source = source;
  }

  public void setUrl(URL url) {
    _url = url;
  }

  /****
   * {@link HttpServlet} Interface
   ****/

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    boolean debug = req.getParameter("debug") != null;
    Message message = _source.getFeed();
    if (debug) {
      resp.getWriter().print(message);
    } else {
      resp.setContentType(CONTENT_TYPE);
      message.writeTo(resp.getOutputStream());
    }
  }

  @Override
  public WebSocket doWebSocketConnect(HttpServletRequest arg0, String arg1) {
    return new DataWebSocket();
  }

  /****
   * {@link ServletSource} Interface
   ****/

  @Override
  public URL getUrl() {
    return _url;
  }

  @Override
  public Servlet getServlet() {
    return this;
  }

  /****
   * Protected Methods
   ****/

  class DataWebSocket implements WebSocket, GtfsRealtimeIncrementalListener {

    private Connection _connection;

    @Override
    public void onOpen(Connection connection) {
      _log.info("client connect");
      _connection = connection;
      _source.addIncrementalListener(this);
    }

    @Override
    public void onClose(int closeCode, String message) {
      _log.info("client close");
      _source.removeIncrementalListener(this);
    }

    /****
     * {@link GtfsRealtimeIncrementalListener} Interface
     ****/

    @Override
    public void handleFeed(FeedMessage feed) {
      byte[] buffer = null;
      try {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        feed.writeTo(out);
        buffer = out.toByteArray();
      } catch (IOException ex) {
        throw new IllegalStateException(ex);
      }

      try {
        _connection.sendMessage(buffer, 0, buffer.length);
      } catch (IOException ex) {

      }
    }
  }
}
