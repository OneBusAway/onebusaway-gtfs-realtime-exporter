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
package org.onebusway.gtfs_realtime.exporter;

import java.io.IOException;
import java.net.URL;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onebusaway.guice.jetty_exporter.ServletSource;

import com.google.protobuf.Message;

abstract class AbstractGtfsRealtimeServlet extends HttpServlet implements
    ServletSource {

  private static final long serialVersionUID = 1L;

  private static final String CONTENT_TYPE = "application/x-google-protobuf";

  protected GtfsRealtimeProvider _provider;

  private URL _url;

  public void setProvider(GtfsRealtimeProvider provider) {
    _provider = provider;
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
    Message message = getMessage();
    if (debug) {
      resp.getWriter().print(message);
    } else {
      resp.setContentType(CONTENT_TYPE);
      message.writeTo(resp.getOutputStream());
    }
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

  /**
   * Override this method to return the protocol buffer
   * 
   * @return
   */
  protected abstract Message getMessage();
}
