// Copyright (c) 2012 Thomas Suckow
// All rights reserved. This program and the accompanying materials 
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave

import com.google.inject._
import net.codingwell.util.ThreadLocal

import scala.collection.{ mutable => mu, immutable => im }
import scala.xml._

trait Profiler {
  def time[R](blockName:String)(block: =>R):R

  def get():Node //TODO: Remove this, shouldn't know about xml
}

@Singleton
class NullProfiler() extends Profiler {
  def time[R](blockName:String)(block: =>R) = {
    block
  }

  def get():Node = {
    <nullprofiler/>
  }
}

@Singleton
class LoggingProfiler() extends Profiler {
  val tops = new mu.ArrayBuffer[mu.ListBuffer[Node]] with mu.SynchronizedBuffer[mu.ListBuffer[Node]]
  val stack = new ThreadLocal[mu.ListBuffer[Node]]( newThread )

  def newThread():mu.ListBuffer[Node] = {
    val buffer = new mu.ListBuffer[Node]
    tops += buffer
    buffer
  }

  def time[R](blockName:String)(thunk: =>R) = {
    val start = System.currentTimeMillis

    val childbuffer = new mu.ListBuffer[Node]

    val result:R = try {
      stack.withValue( childbuffer ) {
        thunk
      }
    } finally {
      val dt = (System.currentTimeMillis - start)
      val node = <timing name={ blockName } time={ dt.toString }>{ childbuffer }</timing>
      stack.get += node
    }

    result
  }

  def get():Node = {
    val children = tops.map( stacks => <thread>{ stacks }</thread> )

    <profiler>
      { children }
    </profiler>
  }
}
