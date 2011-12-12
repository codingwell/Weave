package net.codingwell.weave

import scopt._

class WeaveConfig
{
   var toplevel : String = null
   var forcecolor : Boolean = false
}

object CommandLineParser
{
   def parse( args : Array[String], config : WeaveConfig ) : Boolean =
   {
      val parser = new OptionParser("weave") {
         arg("<toplevel>", "Top level module", {v: String => config.toplevel = v})
         opt( "color", "Force color output even on non-tty's", {config.forcecolor = true} )
      }

      parser.parse(args)
   }
}
