package prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Lookup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


/**
 * Very simple URL downloader.
 * Tested only on URLs with protocol http://
 */
public class SimpleDownloader {
	private int maxLength = 1 << 20;
	
	/**
	 * Download content as string - blocking operation.
	 * @param url URL of content to download.
	 * @return Returns downloaded content as string of maximal length maxLength.
	 * @throws IOException Generic IOException (network problem, ...)
	 */
	public String download(URL url) throws IOException {
		// Create connection object (next line do not connect immediately)
		URLConnection conn = url.openConnection();
		
		// Create reader
		BufferedReader reader = 
			new BufferedReader(new InputStreamReader(conn.getInputStream()));
		
		// Read content line after line
		StringBuffer buff = new StringBuffer();
		String inputLine;
		while (				
					buff.length()<maxLength &&
					null!= (inputLine = reader.readLine()))
			buff.append(inputLine);
		
		// close reader
        reader.close();
        
        // truncate unwanted content
        if (buff.length()>maxLength)
        	buff.setLength(maxLength);
        
        // return content as string
		return buff.toString();
	}
	/**
	 * Returns max content length.
	 */
	public int getMaxLength() {
		return maxLength;
	}
	/**
	 * Set max content length.
	 */
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
}
