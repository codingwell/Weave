/*
 * Copyright (C) 2011 Thomas Suckow, Mathias Doenitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.codingwell.parboiled;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.parboiled.buffers.InputBuffer;
import org.parboiled.common.StringUtils;
import org.parboiled.support.Chars;
import org.parboiled.support.IndexRange;
import org.parboiled.support.Position;

import static org.parboiled.common.Preconditions.*;

/**
 * @author tsuckow
 *
 * Allows for files to be included inside of each other yet still be able to
 * retrieve the characters origin. The file origin is specified by a handle
 * provided when the file is added. 
 *
 * @param <Handle> Handle type for files.
 */
public class IncludableInputBuffer<Handle> implements InputBuffer
{
	StringBuilder buffer = new StringBuilder();
	private class IndexEntry
	{
		Handle handle;
		Position localStart;
		
		IndexEntry( Handle handle, Position pos )
		{
			this.handle = handle;
			this.localStart = pos;
		}
	}
	TreeMap<Integer,IndexEntry> fileIndexes = new TreeMap<Integer,IndexEntry>();
    protected int[] newlines;

    /**
     * Constructs an empty buffer.
     */
	public IncludableInputBuffer()
	{
	}

	@Override
	public char charAt(int index)
	{
		return 0 <= index && index < buffer.length() ? buffer.charAt(index) : Chars.EOI;
	}
	
	public Handle handleAt(int index)
	{
		return 0 <= index && index < buffer.length() ? fileIndexes.floorEntry(index).getValue().handle : null;
	}

	@Override
	public String extract(int start, int end)
	{
		if (start < 0) start = 0;
        if (end >= buffer.length()) end = buffer.length();
        if (end <= start) return "";
        return buffer.substring(start, end);
	}

	@Override
	public String extractLine(int lineNumber)
	{
		buildNewlines();
		checkArgument(0 < lineNumber && lineNumber <= newlines.length + 1);
        int start = lineNumber > 1 ? newlines[lineNumber - 2] + 1 : 0;
        int end = lineNumber <= newlines.length ? newlines[lineNumber - 1] : buffer.length();
        if (charAt(end - 1) == '\r') end--;
        return extract(start, end);
	}

	@Override
	public int getLineCount()
	{
		buildNewlines();
        return newlines.length + 1;
	}

	@Override
	public Position getPosition(int index)
	{
		buildNewlines();
        int line = getLine0(newlines, index);
        int column = index - (line > 0 ? newlines[line - 1] : -1);
        return new Position(line + 1, column);
	}
	
	/**
	 * Returns the line and column number of the character with the given
	 * index relative to the included file.
	 * 
	 * @param index Index of the character to get the position of
	 * @return The position
	 * 
	 * @see getPosition
	 */
	public Position getLocalPosition(int index)
	{
		Position ch = getPosition(index);
		Map.Entry<Integer, IndexEntry> entry = fileIndexes.floorEntry(index); 
		Integer fileindex = entry.getKey();
		Position file = getPosition(fileindex);
		Position filechunk = entry.getValue().localStart;
		int localline = ch.line - file.line + filechunk.line;
		int localcol  = (localline==0)?(ch.column-file.column + filechunk.column):ch.column;
		return new Position(localline,localcol);
	}
	
	// returns the zero based input line number the character with the given index is found in
    protected static int getLine0(int[] newlines, int index) {
        int j = Arrays.binarySearch(newlines, index);
        return j >= 0 ? j : -(j + 1);
    }

	@Override
	public boolean test(int index, char[] characters)
	{
		int len = characters.length;
        if (index < 0 || index > buffer.length() - len) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            if (buffer.charAt(index + i) != characters[i]) return false;
        }
        return true;
	}
	
	public void include(int index, String dat, Handle handle, int replace)
	{
		include(index, dat, handle, replace, ' ');
	}
	
	/**
	 * 
	 * @param index
	 *
	 * @param file
	 * @param replace
	 */
	public void include(int index, String dat, Handle handle, int replace, char replacement)
	{
		buffer.replace( index-replace, index, StringUtils.repeat(' ', replace));//We already handled it.
		buffer.insert( index, dat );
		
		//Iterate over the indexes and adjust them for what we added
		TreeMap<Integer,IndexEntry> newFileIndexes = new TreeMap<Integer,IndexEntry>();
		for(Map.Entry<Integer,IndexEntry> entry : fileIndexes.entrySet())
		{
			if( entry.getKey() >= index )
			{
				newFileIndexes.put( entry.getKey()+dat.length(), entry.getValue() );
			}
			else
			{   //Before what we included, no change.
				newFileIndexes.put( entry.getKey(), entry.getValue() );
			}
		}
		
		fileIndexes = newFileIndexes;
		
		//Split the current file
		Map.Entry<Integer, IndexEntry> includer = fileIndexes.floorEntry(index);
		if( includer != null )
		{
			IndexEntry entry = includer.getValue();
			entry = new IndexEntry(entry.handle, getLocalPosition(index));
			fileIndexes.put(index + dat.length(), entry );
		}
		
		//Finally, add the file we just included.
		fileIndexes.put( index, new IndexEntry( handle, new Position(1,1) ) );
		
		newlines = null;//Invalidate
	}
	
	/**
	 * Constructs newline data for the buffer
	 */
	protected void buildNewlines() {
        if (newlines == null) {
            int count = 0, length = buffer.length();
            for (int i = 0; i < length; i++) {
                if (buffer.charAt(i) == '\n') {
                    count++;
                }
            }
            newlines = new int[count];
            count = 0;
            for (int i = 0; i < length; i++) {
                if (buffer.charAt(i) == '\n') {
                    newlines[count++] = i;
                }
            }
        }
    }

	@Override
	public String extract(IndexRange range)
	{
		return extract(range.start,range.end);
	}

	@Override
	public int getOriginalIndex(int index)
	{
		return index;
	}
}
