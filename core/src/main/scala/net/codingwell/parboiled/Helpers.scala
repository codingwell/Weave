// Copyright (c) 2011 Thomas Suckow
// All rights reserved. This program and the accompanying materials 
// are made available under the terms of the Eclipse Public License v1.0 
// which accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html

package net.codingwell.parboiled

import org.parboiled.Context
import org.parboiled.support.IndexRange

object Helpers
{
	def OriginalIndexRange(s:IndexRange, c:Context[Any]):IndexRange =
	{
	    val start = c.getInputBuffer().getOriginalIndex(s.start)
		val end = c.getInputBuffer().getOriginalIndex(s.end)
		return new IndexRange(start,end)
	}
}