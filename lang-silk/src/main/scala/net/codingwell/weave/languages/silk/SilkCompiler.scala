// Copyright (c) 2011 Thomas Suckow
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.languages.silk

import com.google.inject._
import java.util.Arrays
import org.parboiled.scala.ParsingResult.unwrap
import org.parboiled.scala.parserunners.RecoveringParseRunner
import org.parboiled.scala.Input
import net.codingwell.parboiled.ErrorUtils
import net.codingwell.parboiled.IncludableInputBuffer
import net.codingwell.weave._
import akka.actor._
import akka.dispatch._
import akka.pattern.{ ask, pipe }
import akka.util.Timeout
import akka.util.duration._

import scala.collection.{ mutable => mu, immutable => im }

class SilkCompiler @Inject() (profiler:Profiler) extends Actor {
  def receive = {
    case WeaveCompiler.NotifyWork(actor, source,target) =>
      implicit val timeout = Timeout(5 seconds)
      val future = actor.ask( WeaveCompiler.RequestWork( source, target ) ).mapTo[WeaveCompiler.Work[WeaveFile]]
      val work = Await.result( future, timeout.duration )
      if(work != null)
      {
        val modules = profiler.time("Compiling")( compile( work.value ) )
        modules match {
          case Some( vector ) =>
            sender ! WeaveCompiler.WorkCompleted( work.value, vector )
          case None =>
        }
      }
  }

  def compile( file:WeaveFile ):Option[im.Vector[ModuleSymbol]] = {
    profiler.time("Silk Phase") {
    val start = System.currentTimeMillis

    val buffer = new IncludableInputBuffer[String]
    profiler.time("Stripping")( buffer.include(0, Preprocessor.StripComments( file.contents ), file.name, 0) )//Load the first file
    println( ":1: " + (System.currentTimeMillis - start) )

    val parser = new net.codingwell.weave.languages.silk.SilkParser(buffer)
    println( ":2: " + (System.currentTimeMillis - start) )
    val parserunner = RecoveringParseRunner(parser.File)//TODO: Cache this, takes 200ms to construct.
    println( ":3: " + (System.currentTimeMillis - start) )

    val input = new Input( null, (A:Array[Char]) => buffer ) //Forces the use of our buffer
    println( ":4: " + (System.currentTimeMillis - start) )

    val result = profiler.time("Parsing")( parserunner.run( input ) )
    println( ":5: " + (System.currentTimeMillis - start) )

    if( result.hasErrors() )
    {
      System.out.println("Parse Errors:\n"
        + ErrorUtils.printParseErrors(Arrays.asList( result.parseErrors.toArray : _* ),buffer));
      return None
    }
    else
    {
      System.out.println("Parse OK. :D");

      result.result match {
        case Some(file:ast.File) =>
          val symboltable = new SymbolTable
          val visitor = new ASTRTLVisitor( symboltable )
          profiler.time("Visiting")( visitor visit file )
          val semantic = new Semantic( symboltable )
          profiler.time("Semantic")( semantic.process )
          return Some( symboltable.getImmutableModules )
        case _ => throw new Error("Slik AST missing")
      }
    }
    println( ":: " + (System.currentTimeMillis - start) )
    return None
    }
  }

  def supportedLanguages() = Set("Silk")
}

// vim: tabstop=2 shiftwidth=2
