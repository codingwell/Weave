// Copyright (c) 2011 Thomas Suckow
// All rights reserved. This program and the accompanying materials 
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.CommandLine

import com.google.inject._
import net.codingwell.jansi.AnsiConsole
import net.codingwell.weave._
import net.codingwell.weave.languages.silk._
import java.io.File
import scala.actors.Actor
import scala.actors.Actor._

object Main {

   def main(args: Array[String]):Unit = {

      //Load Config (Command Line)
      val config:WeaveConfig = new WeaveConfig()
      if( !CommandLineParser.parse( args, config ) ) return

      //Ansi console color proxy
      AnsiConsole.systemInstall( config.forcecolor )

      //Prepare Dependancy Injection
      val injector:Injector = Guice.createInjector(
         LocalExecutorModule(),
         SilkCompilerModule(),
         new AbstractModule() {
            @Override
            def configure() {
                //bind(classOf[Config]).toInstance(someInstance)
//                bind(classOf[CodeSource]).to(classOf[FileCodeSource])
//                bind(classOf[OutputDestination]).to(classOf[FileOutputDestination])
            }
        }
      )

      //Get the compiler
      var compiler:WeaveCompiler = injector.getInstance(classOf[WeaveCompiler])

      val file = new NativeWeaveFile( new File("samples/newtest.silk"), "silk" )

      val files = List( file )

      compiler.compile( files )
   }

   val testActor = actor {
      
   }
}
