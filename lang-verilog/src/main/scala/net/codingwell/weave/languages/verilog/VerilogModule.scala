// Copyright (c) 2012 Thomas Suckow
// All rights reserved. This program and the accompanying materials 
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.languages.silk

import net.codingwell.weave._

import com.google.inject._
import com.google.inject.name._
import uk.me.lings.scalaguice._

import scala.collection.{ mutable => mu, immutable => im }

import akka.actor._

class VerilogCompiler extends Actor {
  def receive = {
    case unknown =>
      println("Verilog not implemented")
  }

  def compile( file:WeaveFile ):Option[im.Vector[ModuleSymbol]] = {
    return None
  }

  def supportedLanguages() = Set("Verilog")
}

case class VerilogModule() extends AbstractModule {

  def configure() = {
    val compilerbinder = ScalaMultibinder.newSetBinder( binder(), classOf[ActorRef], Names.named("LangCompilers") )

    compilerbinder.addBinding().toProvider( new TypeLiteral[ActorProvider[VerilogCompiler]]() {} )
  }

}

// vim: tabstop=2 shiftwidth=2

