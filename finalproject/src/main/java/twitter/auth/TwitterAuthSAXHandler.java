package twitter.auth;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TwitterAuthSAXHandler extends DefaultHandler {

	private static final String	AUTHXML_ROOT	= "twitter-auth-data";
	private static final String	XMLEL_APIKEY	= "apikey";
	private static final String	XMLEL_APISEC	= "apisecret";
	private static final String	XMLEL_CONKEY	= "consumerkey";
	private static final String	XMLEL_CONSEC	= "consumersecret";
	
	private List<TwitterAppCredentials> credentials = null;

	private String				apiKey			= null;
	private String				apiSecret		= null;
	private String				consKey			= null;
	private String				consSecret		= null;

	boolean						isAK			= false;
	boolean						isAS			= false;
	boolean						isCK			= false;
	boolean						isCS			= false;
	
	@Override
	public void startDocument() throws SAXException {
		credentials = new ArrayList<TwitterAppCredentials>();
	}
	
	@Override
	public void endDocument() throws SAXException {
		credentials = null;
		this.notify();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		System.out.println("Start Element :" + qName);
		if (qName.equalsIgnoreCase(AUTHXML_ROOT)){
			apiKey = null;
			apiSecret = null;
			consKey = null;
			consSecret = null;
		}

		if (qName.equalsIgnoreCase(XMLEL_APIKEY)) {
			isAK = true;
		}

		if (qName.equalsIgnoreCase(XMLEL_APISEC)) {
			isAS = true;
		}

		if (qName.equalsIgnoreCase(XMLEL_CONKEY)) {
			isCK = true;
		}

		if (qName.equalsIgnoreCase(XMLEL_CONSEC)) {
			isCS = true;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		System.out.println("End Element :" + qName);
		if (qName.equalsIgnoreCase(AUTHXML_ROOT)){
			credentials.add(new TwitterAppCredentials(apiKey, apiSecret, consKey, consSecret));
		}
	}

	public void characters(char ch[], int start, int length) throws SAXException {

		if (isAK) {
			apiKey = new String(ch, start, length);
			System.out.println(XMLEL_APIKEY.toUpperCase() + " : " + apiKey);
			isAK = false;
		}

		if (isAS) {
			apiSecret = new String(ch, start, length);
			System.out.println(XMLEL_APISEC.toUpperCase() + " : " + apiSecret);
			isAS = false;
		}

		if (isCK) {
			consKey = new String(ch, start, length);
			System.out.println(XMLEL_CONKEY.toUpperCase() + " : " + consKey);
			isCK = false;
		}

		if (isCS) {
			consSecret = new String(ch, start, length);
			System.out.println(XMLEL_CONSEC.toUpperCase() + " : " + consSecret);
			isCS = false;
		}

	}
	
	public synchronized List<TwitterAppCredentials> getCredentials(){
		return credentials;
	}
}
