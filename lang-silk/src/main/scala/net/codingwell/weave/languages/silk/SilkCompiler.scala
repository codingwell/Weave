package net.codingwell.weave.languages.silk
import java.util.Arrays

import org.parboiled.scala.ParsingResult.unwrap
import org.parboiled.scala.parserunners.RecoveringParseRunner
import org.parboiled.scala.Input

import net.codingwell.parboiled.ErrorUtils
import net.codingwell.parboiled.IncludableInputBuffer
import net.codingwell.weave.Compiler
import net.codingwell.weave.WeaveFile

class RTLPlaceholder {}

class SilkCompiler extends Compiler {

  def compile( file:WeaveFile ): RTLPlaceholder = {

    val buffer = new IncludableInputBuffer[String]
    buffer.include(0, Preprocessor.StripComments( file.contents ), file.filename, 0);//Load the first file

    val parser = new net.codingwell.weave.languages.silk.SilkParser(buffer)
    val parserunner = RecoveringParseRunner(parser.File)

    val input = new Input( null, (A:Array[Char]) => buffer ) //Forces the use of our buffer

    val result = parserunner.run( input )

    if( result.hasErrors() )
    {
      System.out.println("Parse Errors:\n"
        + ErrorUtils.printParseErrors(Arrays.asList( result.parseErrors.toArray : _* ),buffer));
    }
    else
    {
      System.out.println("Parse OK. :D");

      result.result match {
        case Some(f:ast.File) => ""//TODO
        case _ => throw new Error("Slik AST missing")
      }
    }

    new RTLPlaceholder
  }

}

// vim: tabstop=2 shiftwidth=2
