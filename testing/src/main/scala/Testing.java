import net.codingwell.weave.remoting.server.*;

class Testing
{

	public static void main(String[] args)
	{
		System.out.println("Hello World");
      AtmosphereWebsocketServer server = new AtmosphereWebsocketServer( "net.codingwell.weave.remoting.server.resources", 8080 );
      server.start();
	}

}
