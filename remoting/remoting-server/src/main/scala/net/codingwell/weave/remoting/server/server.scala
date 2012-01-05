// Copyright (c) 2011 Thomas Suckow
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.remoting.server
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHolder
import org.atmosphere.cpr.AtmosphereServlet
import org.atmosphere.cpr.ApplicationConfig
import org.atmosphere.container.JettyCometSupportWithWebSocket
import org.eclipse.jetty.servlet.ServletContextHandler

object AtmosphereWebsocketServer
{
    val DEFAULT_MAX_INACTIVITY_LIMIT:String = "120000";
}

class AtmosphereWebsocketServer( val resourcePackage:String, val port:Int )
{
   val server:Server = new Server(port)
   val atmosphereServletHolder:ServletHolder = new ServletHolder( classOf[AtmosphereServlet] )

       atmosphereServletHolder.setInitParameter("com.sun.jersey.config.property.packages", resourcePackage);
	        atmosphereServletHolder.setInitParameter("com.sun.jersey.spi.container.ResourceFilter", "org.atmosphere.core.AtmosphereFilter");
	        atmosphereServletHolder.setInitParameter(ApplicationConfig.PROPERTY_COMET_SUPPORT, classOf[JettyCometSupportWithWebSocket].getName());
	        atmosphereServletHolder.setInitParameter(ApplicationConfig.WEBSOCKET_SUPPORT, "true");
	        atmosphereServletHolder.setInitParameter(ApplicationConfig.PROPERTY_NATIVE_COMETSUPPORT, "true");
	        atmosphereServletHolder.setInitParameter(ApplicationConfig.MAX_INACTIVE, AtmosphereWebsocketServer.DEFAULT_MAX_INACTIVITY_LIMIT );

val servletContextHandler:ServletContextHandler = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
servletContextHandler.addServlet(atmosphereServletHolder, "/atmosphere/*");

   def start():Unit =
   {
      server.start()
   }

   def stop():Unit =
   {
      server.stop()
   }
}
