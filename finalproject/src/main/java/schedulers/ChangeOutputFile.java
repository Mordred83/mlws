package schedulers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import listener.PoliticalTweets;

import twitter4j.TwitterException;
import bean.Tweet;
import bean.TweetList;

public class ChangeOutputFile {
	
	private final String path = "src/main/resources/tweets_";
	private final String txtFormat = ".txt";
	private final String zipFormat = ".zip";

	public void changeFile() {
		
		System.out.println("Starting Scheduler ChangeOutputFile");
		
		try {
			GregorianCalendar calendar = new GregorianCalendar();
			String today = formatDate(calendar);
			
			File newFile = new File(path+today+txtFormat);
			newFile.createNewFile();
			PoliticalTweets.changeOutFile(newFile);
			
			calendar.add(calendar.DAY_OF_MONTH, -1);
			String yesterday = formatDate(calendar);
			
			zipYesterdayFile(yesterday);
			
			File oldFile = new File(path+yesterday+txtFormat);
			oldFile.delete();
			
		}catch(IOException ex){
			System.out.println("Errore nella creazione del file ZIP");
			ex.printStackTrace();
		}
	}
	
	private void zipYesterdayFile(String yesterday) throws IOException {
		byte[] buffer = new byte[4096];
		
		FileOutputStream fos = new FileOutputStream(path+yesterday+zipFormat);
		ZipOutputStream zos = new ZipOutputStream(fos);
		ZipEntry ze= new ZipEntry(yesterday+txtFormat);
		zos.putNextEntry(ze);
		FileInputStream in = new FileInputStream(path+yesterday+txtFormat);
		
		int len;
		while ((len = in.read(buffer)) > 0) {
			zos.write(buffer, 0, len);
		}

		in.close();
		zos.closeEntry();
		zos.close();

		System.out.println("File "+path+yesterday+zipFormat+" completato con successo");
		
	}

	private String formatDate (GregorianCalendar calendar) {
		SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
		String date = format.format(calendar.getTime());
		return date;
	}

}
