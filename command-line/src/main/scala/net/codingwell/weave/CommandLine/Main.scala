// Copyright (c) 2011 Thomas Suckow
// All rights reserved. This program and the accompanying materials 
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.CommandLine

import com.google.inject._
import uk.me.lings.scalaguice._

import net.codingwell.jansi.AnsiConsole
import net.codingwell.weave._
import net.codingwell.weave.languages.silk._
import net.codingwell.weave.languages.verilog._
import java.io.File
import akka.actor._

object Main {

   def main(args: Array[String]):Unit = {

      print("Patience")

      //Load Config (Command Line)
      val config:WeaveConfig = new WeaveConfig()
      if( !CommandLineParser.parse( args, config ) ) return

      //Ansi console color proxy
      AnsiConsole.systemInstall( config.forcecolor )
      
      print(".")

      //Prepare Dependancy Injection
      val injector:Injector = Guice.createInjector(
         LocalExecutorModule(),
         SilkCompilerModule(),
         WeaveModule(),
         new AbstractModule with ScalaModule {
           def configure = {
             bind[ActorSystem].toInstance( ActorSystem("WeaveSystem") )
             bind[GeneratorVisitor].to[VerilogGeneratorVisitor]
             bind[Profiler].to[LoggingProfiler]
           }
         }
      )
      
      print(".")

      //Get the compiler
      var app:Application = injector.getInstance(classOf[Application])
      app.run
   }
}


class Application @Inject() ( compiler:WeaveCompiler, profiler:Profiler, system:ActorSystem ) {
  def run() = {
      println(".")

      val file = new NativeWeaveFile( new File("samples/newtest.silk"), "silk" )

      val files = List( file )

      profiler.time("Total Compilation")( compiler.compile( files ) )

      system shutdown

      println( profiler.get )
  }
}
