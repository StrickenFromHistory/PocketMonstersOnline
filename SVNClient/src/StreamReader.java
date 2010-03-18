

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JTextArea;

public class StreamReader implements Runnable {
	private InputStream m_stream;
	private String m_streamName;
	private JTextArea m_textOut = null;
	
	public StreamReader(InputStream r, String stream) {
		m_stream = r;
		m_streamName = stream;
	}

	public StreamReader(InputStream r, String stream, JTextArea o) {
		m_stream = r;
		m_streamName = stream;
		m_textOut = o;
		}

	@Override
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(m_stream);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null) {
            	if(m_textOut == null) System.out.println(m_streamName + ">" + line); 
            	else m_textOut.append(line + "\n");
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
