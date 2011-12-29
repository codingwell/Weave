// Copyright (c) 2011 Thomas Suckow
// All rights reserved. This program and the accompanying materials 
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave;

import net.codingwell.jansi.AnsiConsole;
import net.codingwell.labs.CalcRunner;
import net.codingwell.labs.LoggedA;
import net.codingwell.labs.logging.ConsoleLogger;
import net.codingwell.labs.logging.Logger;

import org.atmosphere.container.Jetty7CometSupport;
import org.atmosphere.container.JettyCometSupport;
import org.atmosphere.container.JettyCometSupportWithWebSocket;
import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
public class WeaveCL
{
	private static final String ATMOSPHERE_RESOURCES = "com.enteks.atmosphere.samples.resources.atmosphere";
    private static final String DEFAULT_MAX_INACTIVITY_LIMIT = "120000";
    

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
      WeaveConfig config = new WeaveConfig();
      if( !CommandLineParser.parse( args, config ) ) return;

      AnsiConsole.systemInstall( config.forcecolor() );

		Logger logr = new ConsoleLogger();
		LoggedA la = new LoggedA(logr);
		la.doStuff();

try
{
	/*
Handler handler=new AbstractHandler()
{
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch)
        throws IOException, ServletException
    {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1>Hello</h1>");
        ((Request)request).setHandled(true);
    }
};
*/

//AtmosphereServlet atmosphereServer = new AtmosphereServlet();

Server server = new Server(8080);
//server.setHandler(handler);

ServletHolder atmosphereServletHolder = initAtmosphereServlet(DEFAULT_MAX_INACTIVITY_LIMIT);

ServletContextHandler servletContextHandler;
servletContextHandler = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
servletContextHandler.addServlet(atmosphereServletHolder, "/atmosphere/*");

server.start();
Thread.sleep(120000);
server.stop();
}
catch(Exception e)
{
System.err.println(e.getMessage());
   e.printStackTrace();
}

		CalcRunner cr = new CalcRunner();
		cr.Run();
	}
	
	 private static ServletHolder initAtmosphereServlet(String maxInactivityLimit) {
		 
		 AtmosphereServlet atmosphereServlet = new AtmosphereServlet();
	        atmosphereServlet.addInitParameter("com.sun.jersey.config.property.packages", ATMOSPHERE_RESOURCES);
	        atmosphereServlet.addInitParameter("com.sun.jersey.spi.container.ResourceFilter", "org.atmosphere.core.AtmosphereFilter");
	        atmosphereServlet.addInitParameter(ApplicationConfig.PROPERTY_COMET_SUPPORT, JettyCometSupportWithWebSocket.class.getName());
	        atmosphereServlet.addInitParameter(ApplicationConfig.WEBSOCKET_SUPPORT, "true");
	        atmosphereServlet.addInitParameter(ApplicationConfig.PROPERTY_NATIVE_COMETSUPPORT, "true");
	        atmosphereServlet.addInitParameter(ApplicationConfig.MAX_INACTIVE, (maxInactivityLimit.isEmpty()) ? DEFAULT_MAX_INACTIVITY_LIMIT : maxInactivityLimit);
		 //atmosphereServlet.setCometSupport(new JettyCometSupport(atmosphereServlet.getAtmosphereConfig()));
		 ServletHolder atmosphereServletHolder = new ServletHolder(atmosphereServlet);
		 
       /*
	        ServletHolder atmosphereServletHolder = new ServletHolder(AtmosphereServlet.class);
	        atmosphereServletHolder.setInitParameter("com.sun.jersey.config.property.packages", ATMOSPHERE_RESOURCES);
	        atmosphereServletHolder.setInitParameter("com.sun.jersey.spi.container.ResourceFilter", "org.atmosphere.core.AtmosphereFilter");
	        atmosphereServletHolder.setInitParameter(ApplicationConfig.PROPERTY_COMET_SUPPORT, JettyCometSupportWithWebSocket.class.getName());
	        atmosphereServletHolder.setInitParameter(ApplicationConfig.WEBSOCKET_SUPPORT, "true");
	        atmosphereServletHolder.setInitParameter(ApplicationConfig.PROPERTY_NATIVE_COMETSUPPORT, "true");
	        atmosphereServletHolder.setInitParameter(ApplicationConfig.MAX_INACTIVE, (maxInactivityLimit.isEmpty()) ? DEFAULT_MAX_INACTIVITY_LIMIT : maxInactivityLimit);
      */
	        return atmosphereServletHolder;
	    }
}
