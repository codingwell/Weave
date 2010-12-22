/*
Copyright 2010 Coding Well
All rights reserved. This program and the accompanying materials 
are made available under the terms of the Eclipse Public License v1.0 
which accompanies this distribution, and is available at 
http://www.eclipse.org/legal/epl-v10.html
*/

package net.codingwell.weave;

import org.parboiled.buffers.InputBuffer.Position;

public interface ASTAttr
{
	Position GetStartPosition();
	Position GetEndPosition();
	String GetCode();
}
