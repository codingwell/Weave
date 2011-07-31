// Copyright (c) 2010 Thomas Suckow
// All rights reserved. This program and the accompanying materials 
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.labs

import net.codingwell.parboiled.IncludableInputBuffer
import org.parboiled.scala.Input
import net.codingwell.weave.silk.Preprocessor
import org.parboiled.common.FileUtils
import org.parboiled.scala.parserunners.RecoveringParseRunner
import net.codingwell.parboiled.ErrorUtils
import java.util.Arrays
import org.parboiled.errors.ParseError

class CalcRunner {
	def Run =
	{
		val buffer = new IncludableInputBuffer[String]
		buffer.include(0, Preprocessor.StripComments( FileUtils.readAllText("samples\\test.silk") ), "test.silk", 0);//Load up first file
		val parser = new net.codingwell.weave.silk.SilkParser(buffer) { override val buildParseTree = true }
		val runner = RecoveringParseRunner(parser.File)
		val result = runner.run( new Input( null, (A:Array[Char]) => buffer ) ) //This is kinda hackish, ideally we would pass filename to buffer here
		val parseTreePrintOut = org.parboiled.support.ParseTreeUtils.printNodeTree(result)
		//println(parseTreePrintOut)
		val file = result.result.get
		println( file.members.size )
		buffer.debugOutput()
		
		//TODO: Need to think more about interaction between errors, includableinputbuffer, defaultbuffer, recoveringparserunner
		if (result.hasErrors())
		{
			System.out.println("Parse Errors:\n"
					+ ErrorUtils.printParseErrors(Arrays.asList( result.parseErrors.toArray : _* ),buffer));
		}
		else
		{
			System.out.println("Parse OK. :D");
		}
}
}