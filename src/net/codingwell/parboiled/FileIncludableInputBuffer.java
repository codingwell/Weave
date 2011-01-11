package net.codingwell.parboiled;

import java.io.File;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;

import org.parboiled.buffers.InputBuffer;
import org.parboiled.common.StringUtils;
import org.parboiled.support.Chars;

import net.codingwell.util.FileUtils;

public class FileIncludableInputBuffer implements InputBuffer
{
	StringBuffer buffer = new StringBuffer();
	TreeMap<Integer,File> fileIndexes = new TreeMap<Integer,File>();
    protected int[] newlines;

	/**
	 * 
	 * @param file
	 * @throws Exception FIXME Throw specialized exception
	 * 
	 */
	public FileIncludableInputBuffer(String file) throws Exception
	{
		include(0,file,0);
	}

	@Override
	public char charAt(int index)
	{
		return 0 <= index && index < buffer.length() ? buffer.charAt(index) : Chars.EOI;
	}
	
	public File fileAt(int index)
	{
		return 0 <= index && index < buffer.length() ? fileIndexes.floorEntry(index).getValue() : null;
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
		//TODO: Update when parboiled updated
        if(!(0 < lineNumber && lineNumber <= newlines.length + 1))
        {
        	throw new IllegalArgumentException();
        }
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
	
	/**
	 * 
	 * @param index
	 *
	 * @param file
	 * @param replace
	 * @throws IOException FIXME specialize exception
	 */
	public void include(int index, String file, int replace) throws IOException
	{
		String dat = FileUtils.readFileAsString(file);
		buffer.replace( index-replace, index, StringUtils.repeat(' ', replace));//We already handled it.
		buffer.insert( index, dat );
		
		Integer keysToFix = index;
		
		//TODO: Use ceilingEntry to get a pair.
		//Iterate over the indexes and adjust them for what we added (and removed)
		TreeMap<Integer,File> newFileIndexes = new TreeMap<Integer,File>();
		for(Iterator<Integer> iter = fileIndexes.keySet().iterator(); iter.hasNext();)
		{
			Integer mykey = iter.next();
			if( mykey >= keysToFix )
			{/*
				if( mykey < index + replace )
				{
					//We have to cut off the beginning of this (Or remove it completely)
					Integer nextkey = fileIndexes.ceilingKey(mykey+1);
					if( nextkey >= index + replace )
					{
						//Just cut off the beginning
						newFileIndexes.put(mykey+dat.length(),fileIndexes.get(mykey));
					}
				}
				else*/
				{
					//Just move it over
					newFileIndexes.put(mykey/*-replace*/+dat.length(),fileIndexes.get(mykey));
				}
			}
			else
			{   //Before what we included, no change.
				newFileIndexes.put(mykey,fileIndexes.get(mykey));
			}
		}
		
		fileIndexes = newFileIndexes;
		
		//Split the current file
		Integer sourcekey = fileIndexes.floorKey(index);
		if( sourcekey != null )
		{
			fileIndexes.put(index + dat.length(), fileIndexes.get(sourcekey));
		}
		
		//Finally, add the file we just included.
		fileIndexes.put(index,new File(file));
		
		newlines = null;//Invalidate
	}
	
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
}
