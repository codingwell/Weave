/*
Copyright 2010 Coding Well
All rights reserved. This program and the accompanying materials 
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at 
http://www.eclipse.org/legal/epl-v10.html
*/

package net.codingwell.weave;

import java.io.IOException;
import org.parboiled.Parboiled;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.parserunners.TracingParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

import net.codingwell.weave.silk.SilkParser;

import static net.codingwell.util.FileUtils.readFileAsString;

public class WeaveCL 
{

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		SilkParser parser = Parboiled.createParser(SilkParser.class);

		//File
        String input = null;
		try
		{
			input = readFileAsString("test.silk");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		if( input != null )
		{
			ParsingResult<SilkParser> result = RecoveringParseRunner.run(parser.File(), input);
			//ParsingResult<SilkParser> result = TracingParseRunner.run(parser.File(), input);
			
        	if (result.hasErrors()) 
        	{
            	System.out.println("Parse Errors:\n" + ErrorUtils.printParseErrors(result));
        	}
        	else
        	{
        		System.out.println("Parse OK. :D");
        	}
		}
	}

}
