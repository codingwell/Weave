// Copyright (c) 2010 Thomas Suckow
// All rights reserved. This program and the accompanying materials 
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.silk;

public class Preprocessor
{
	/**
	 * Strips out comments replacing them with simple spaces.
	 * This means that the resulting string will be the same
	 * length as the input.
	 * 
	 * @param input Input file
	 * @return The processed string
	 */
	static public String StripComments(String input)
	{
		if( input == null ) return "";
		boolean eol = false;
		boolean block = false;
		StringBuilder output = new StringBuilder(input);
		for( int i = 0; i < output.length(); ++i )
		{
			if(eol)
			{
				char here = output.charAt(i);
				if( here == '\r' || here == '\n' )
				{
					eol = false;
				}
				else
				{
					output.setCharAt(i, ' ');
				}
			}
			else if(block)
			{
				char here = output.charAt(i);
				if( here == '\r' || here == '\n' )
				{
					//Do Nothing
				}
				else if( output.charAt(i) == '*' )
				{
					output.setCharAt(i, ' ');
					if( i+1 < output.length() )
					{
						if( output.charAt(i+1) == '/' )
						{
							block = false;
							output.setCharAt(i+1, ' ');
						}
					}
				}
				else
				{
					output.setCharAt(i, ' ');
				}
			}
			else
			{
				if( output.charAt(i) == '/' )
				{
					if( i+1 < output.length() )
					{
						if( output.charAt(i+1) == '/' )
						{
							eol = true;
							output.setCharAt(i, ' ');
						}
						else if( output.charAt(i+1) == '*' )
						{
							block = true;
							output.setCharAt(i, ' ');
						}
					}
				}
			}
		}
		
		return output.toString();
	}
}
