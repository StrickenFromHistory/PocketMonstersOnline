package org.pokenet.thin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamReader implements Runnable {
	private InputStream m_stream;
	private String m_streamName;
	
	public StreamReader(InputStream r, String stream) {
		m_stream = r;
		m_streamName = stream;
	}

	@Override
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(m_stream);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null) {
            	System.out.println(m_streamName + ">" + line); 
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
