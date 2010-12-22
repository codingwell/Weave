package net.codingwell.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileUtils
{
	public static String readFileAsString(String filePath) throws java.io.IOException
	{
	    byte[] buffer = new byte[(int) new File(filePath).length()];
	    BufferedInputStream f = null;
	    try 
	    {
	        f = new BufferedInputStream(new FileInputStream(filePath));
	        f.read(buffer);
	    }
	    finally
	    {
	        if (f != null)
	        {
	        	try
	        	{
	        		f.close();
	        	}
	        	catch (IOException ignored)
	        	{ }
	        }
	    }
	    return new String(buffer);
	}
}
