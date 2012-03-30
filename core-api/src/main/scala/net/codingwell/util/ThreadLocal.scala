package net.codingwell.util

class ThreadLocal[T](init: => T) extends java.lang.ThreadLocal[T] {
  override def initialValue:T = init
  def withValue[R](value:T)( thunk: =>R ):R = {
    val oldvalue:T = get
    set( value )

    try {
      thunk
    } finally {
      set( oldvalue )
    }
  }
}
