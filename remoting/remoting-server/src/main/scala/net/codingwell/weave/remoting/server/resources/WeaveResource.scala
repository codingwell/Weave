// Copyright (c) 2011 Thomas Suckow
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.remoting.server.resources

import org.atmosphere.annotation.Broadcast
import org.atmosphere.annotation.Suspend
import org.atmosphere.cpr.Broadcaster
import org.atmosphere.jersey.Broadcastable
import javax.ws.rs._
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType

import com.google.inject._

@Path("/")
class WeaveResource @Inject() ( val injectParam:String )
{
    /*
     * Subscribes listener to the broadcast of clicker responses
     * @PARAM {channel} is the path parameter representing the unique ID generated when the
     * listener registered with the service.
     */
    @GET
    @Path("/{channel}")
    @Suspend(outputComments = true)
    def subscribe( @PathParam("channel") channel:Broadcaster ):Broadcastable =
    {
      println("Subscribe" + channel.toString())
      channel.broadcast(injectParam)
        return new Broadcastable(channel)
    }

}
