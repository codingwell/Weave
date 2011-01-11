package net.codingwell.parboiled;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;

import org.parboiled.buffers.InputBuffer;
import net.codingwell.util.FileUtils;

public class FileIncludableInputBuffer implements InputBuffer
{
	StringBuffer buffer = new StringBuffer();
	TreeMap<Integer,File> fileIndexes = new TreeMap<Integer,File>();
	//floorKey
	
	/**
	 * 
	 * @param file
	 * @throws Exception FIXME Throw specialized exception
	 * 
	 */
	public void FileIncludeableInputBuffer(String file) throws Exception
	{
		include(0,file,0);
	}

	@Override
	public char charAt(int arg0)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String extract(int start, int end)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String extractLine(int lineNumber)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLineCount()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Position getPosition(int index)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean test(int index, char[] characters)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void include(int index, String relativeFile) throws IOException
	{
		include(index,relativeFile,0);
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
		buffer.delete( index, index + replace);
		buffer.insert( index, dat );
		Integer keysToFix = fileIndexes.ceilingKey(index);
		
		//Iterate over the indexes and adjust them for what we added (and removed)
		TreeMap<Integer,File> newFileIndexes = new TreeMap<Integer,File>();
		for(Iterator<Integer> iter = fileIndexes.keySet().iterator(); iter.hasNext();)
		{
			Integer mykey = iter.next();
			if( mykey >= keysToFix )
			{
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
				else
				{
					//Just move it over
					newFileIndexes.put(mykey-replace+dat.length(),fileIndexes.get(mykey));
				}
			}
			else
			{   //Before what we included, no change.
				newFileIndexes.put(mykey,fileIndexes.get(mykey));
			}
		}
		
		fileIndexes = newFileIndexes;
		//Finally, add the file we just included.
		fileIndexes.put(index,new File(file));
	}
}
