package org.pokenet.thin.libs;

/*
 * simple class to provide functionality similar to Wget.
 *
 * Note: Could also strip out all of the html w/ jtidy.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class JGet
{

	public static void getFile(String input, String output) throws MalformedURLException, IOException
	{
		URL u = new URL(input);
		InputStream is = u.openStream();
		File f = new File(output);

		OutputStream out = new FileOutputStream(f);
		byte buf[] = new byte[1024];
		int len;
		while((len = is.read(buf)) >0)
			out.write(buf,0,len);
		out.close();
		is.close();

	}

}
