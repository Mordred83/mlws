package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertyLoader {

	public static final String AUTHENTICATE = "/properties/authenticate.properties";
	
	public static Properties load(String path) throws IOException{
		InputStream is = PropertyLoader.class.getResourceAsStream(path);
		Properties p = new Properties();
		p.load(is);
		return p;
	}
	
	public static String getProperty(String filePath, String key) throws IOException{
		return load(filePath).getProperty(key);
	}
	
}
