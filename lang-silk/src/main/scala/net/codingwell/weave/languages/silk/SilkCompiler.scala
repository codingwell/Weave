// Copyright (c) 2011 Thomas Suckow
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.languages.silk

import java.util.Arrays
import org.parboiled.scala.ParsingResult.unwrap
import org.parboiled.scala.parserunners.RecoveringParseRunner
import org.parboiled.scala.Input
import net.codingwell.parboiled.ErrorUtils
import net.codingwell.parboiled.IncludableInputBuffer
import net.codingwell.weave._
import akka.actor._

import scala.collection.mutable.{Map => MutableMap}

class SilkCompiler extends Actor {

//  val map = new MutableMap[UntypedChannel,String]

  def receive = {
    case WeaveCompiler.NotifyWork(actor, source,target) =>
      val work = actor ? WeaveCompiler.RequestWork( source, target )
      work.as[WeaveCompiler.Work[WeaveFile]] match {
        case Some(WeaveCompiler.Work(file)) =>
          compile( file )
        case None =>
        case _ =>
      }
  }

  def compile( file:WeaveFile ):Unit = {
          println("File")

    val buffer = new IncludableInputBuffer[String]
    buffer.include(0, Preprocessor.StripComments( file.contents ), file.name, 0);//Load the first file

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
        case Some(file:ast.File) =>
          val symboltable = new SymbolTable
          val visitor = new ASTRTLVisitor( symboltable )
          visitor visit file
        case _ => throw new Error("Slik AST missing")
      }
    }
  }

  def supportedLanguages() = Set("Silk")
}

// vim: tabstop=2 shiftwidth=2
