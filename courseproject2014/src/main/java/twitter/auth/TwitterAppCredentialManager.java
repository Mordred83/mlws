package twitter.auth;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class TwitterAppCredentialManager {
	
	/**
	 * Legge le credenziali dal file in input
	 * @param file file contenente le credenziali di autenticazione
	 * @return lista delle credenziali
	 */
	public static List<TwitterAppCredentials> getCredentials(InputStream file) {
		SAXParser parser = null;
		List<TwitterAppCredentials> credentials = null;
		try {
				parser = SAXParserFactory.newInstance().newSAXParser();
				TwitterAuthSAXHandler handler = new TwitterAuthSAXHandler();
				parser.parse(file, handler);
				credentials = handler.getCredentials();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return credentials;
	}
}
