package net.codingwell.weave.languages.silk

import net.codingwell.weave.Compiler

import com.google.inject._
import com.google.inject.multibindings._

case class SilkCompilerModule() extends AbstractModule {

  def configure() = {
    val compilerbinder = Multibinder.newSetBinder( binder(), classOf[Compiler] )

    compilerbinder.addBinding().to(classOf[SilkCompiler])
  }

}

// vim: tabstop=2 shiftwidth=2
