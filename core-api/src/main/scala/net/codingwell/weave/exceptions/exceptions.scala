// Copyright (c) 2011 Thomas Suckow
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.weave.exceptions

import net.codingwell.weave._

case class MultipleDriverException( connection:Connection, newsignal:ConnectionSignal, oldsignal:ConnectionSignal ) extends Exception
