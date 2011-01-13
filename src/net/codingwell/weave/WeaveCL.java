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
import org.parboiled.common.FileUtils;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.Chars;
import org.parboiled.support.ParsingResult;

import net.codingwell.parboiled.IncludableInputBuffer;
import net.codingwell.weave.silk.SilkParser;

import static net.codingwell.util.FileUtils.readFileAsString;

public class WeaveCL
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		SilkParser parserMaster = Parboiled.createParser(SilkParser.class);
		SilkParser parser = parserMaster.newInstance();

		// File
		String input = null;
		try
		{
			input = readFileAsString("test.silk");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		if (input != null)
		{
			
			
			try
			{
				IncludableInputBuffer<String> buffer = new IncludableInputBuffer<String>();
				buffer.include(0, FileUtils.readAllText("test.silk"), "test.silk", 0);//Load up first file
				parser.act.buffer = buffer;
				ParseRunner<SilkParser> runner = new RecoveringParseRunner<SilkParser>(parser.File());
			
				ParsingResult<SilkParser> result = runner.run(buffer);
	
				if (result.hasErrors())
				{
					System.out.println("Parse Errors:\n"
							+ ErrorUtils.printParseErrors(result));
				}
				else
				{
					System.out.println("Parse OK. :D");
				}
				
				System.out.println( "File Sources:\n" );
				
				char c = buffer.charAt(0);
				for(int i = 0; c != Chars.EOI; c = buffer.charAt(++i))
				{
					System.out.print(c);
					System.out.print(' ');
					System.out.println( buffer.handleAt(i) );
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
