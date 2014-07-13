package common;

import java.io.File;


public class Constants {
	public static final File BASE_FOLDER = new File("./");
	public static final File DATA_FOLDER = new File(BASE_FOLDER, "data");
	
	// Cartelle in resources
	public static final File RES_CONF_FOLDER = new File("conf");	
	public static final File RES_INPUT_FOLDER = new File("src/main/resources/input");	
	public static final File RES_DEST_DIR = new File("src/main/resources/output");
	
	// Files di credenziali per l'autenticazione a Twitter
	public static final File CRED_FILE_M = new File("/mauthenticate.xml");
	public static final File CRED_FILE_F = new File("/fauthenticate.xml");
	
	// Array iniziale di utenti da cui effettuare il crawl
	public static final Long[] STARTING_IDS = new Long[]{19067940L, 
		615597661L, 337171830L, 379256309L};
//	public static final String[] STARTING_IDS = new String[]{"Beppe Grillo", 
//		"AlessandroDiBattista", "Angelo Tofalo", "Giulia Di Vita"};
	
}
