package twitter.auth;

import java.io.File;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class TwitterAppCredentialManager {
	
	public static List<TwitterAppCredentials> getCredentials(File file) {
		SAXParser parser = null;
		List<TwitterAppCredentials> credentials = null;
		try {
			if (file.exists() && file.canRead()) {
				parser = SAXParserFactory.newInstance().newSAXParser();
				TwitterAuthSAXHandler handler = new TwitterAuthSAXHandler();
				parser.parse(file, handler);
				credentials = handler.getCredentials();
			} else {
				String msg = file.getAbsolutePath()
						+ (!file.exists() ? " doesn't exists" : " not enough permission");
				throw new IllegalArgumentException(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return credentials;
	}
}
