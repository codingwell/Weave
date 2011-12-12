// Copyright (c) 2011 Thomas Suckow
// All rights reserved. This program and the accompanying materials 
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave;

import net.codingwell.labs.CalcRunner;
import net.codingwell.labs.LoggedA;
import net.codingwell.labs.logging.ConsoleLogger;
import net.codingwell.labs.logging.Logger;

import net.codingwell.jansi.AnsiConsole;

public class WeaveCL
{

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

		CalcRunner cr = new CalcRunner();
		cr.Run();
	}
}
