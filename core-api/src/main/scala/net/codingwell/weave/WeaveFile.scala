package net.codingwell.weave

import java.io.File
import org.apache.commons.io._

trait WeaveFile {

   def name():String

   def contents():String

   def filetype():String
}

class NativeWeaveFile( var file:File, val _filetype:String ) extends WeaveFile {
   def name() = file.getName()

   def contents() = FileUtils.readFileToString( file )

   def filetype() = _filetype
}
