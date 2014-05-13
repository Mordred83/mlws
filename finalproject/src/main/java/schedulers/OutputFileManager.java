package schedulers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import listener.PoliticalTweets;

public class OutputFileManager {

	private static final String	txtFormat			= ".txt";
	private static final String	zipFormat			= ".zip";

	private static final int	BUFFER_SIZE			= 4096;
	private static final String	OUTPUTFILE_BASENAME	= "tweets_";
	private static final String	OUTPUTDIR_NAME		= "outputs";

	public void changeFile() {

		System.out.println("Starting Scheduler ChangeOutputFile");

		try {
			File outputDir = getOutputDirectory();
			GregorianCalendar calendar = new GregorianCalendar();
			String today = formatDate(calendar);

			File newFile = new File(outputDir, OUTPUTFILE_BASENAME + today + txtFormat);
			newFile.createNewFile();
			PoliticalTweets.changeOutFile(newFile);

			calendar.add(GregorianCalendar.DAY_OF_MONTH, -1);
			String yesterday = formatDate(calendar);

			File oldFile = new File(outputDir, OUTPUTFILE_BASENAME + yesterday + txtFormat);
			if (oldFile.exists()) {
				zipYesterdayFile(outputDir, yesterday);
				oldFile.delete();
			}

		} catch (IOException ex) {
			System.out.println("Errore nella creazione del file ZIP");
			ex.printStackTrace();
		}
	}

	private void zipYesterdayFile(File outputDir, String yesterday) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		
		File inputFile = new File(outputDir, OUTPUTFILE_BASENAME
		          				+ yesterday + txtFormat);
		File outputFile = new File(outputDir, OUTPUTFILE_BASENAME
		           				+ yesterday + zipFormat);
		
		FileInputStream in = new FileInputStream(inputFile);
		FileOutputStream fos = new FileOutputStream(outputFile);
		ZipOutputStream zos = new ZipOutputStream(fos);
		ZipEntry ze = new ZipEntry(yesterday + txtFormat);
		zos.putNextEntry(ze);
		
		int len;
		while ((len = in.read(buffer)) > 0) {
			zos.write(buffer, 0, len);
		}

		in.close();
		zos.closeEntry();
		zos.close();

		System.out.println("File " + outputFile.getName() + " completato con successo");
	}

	private static String formatDate(GregorianCalendar calendar) {
		SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
		String date = format.format(calendar.getTime());
		return date;
	}

	private static File getOutputDirectory() {
		String workingDir = System.getProperty("user.dir");
		File wd = new File(workingDir);
		File outputDir = new File(wd, OUTPUTDIR_NAME);
		if (!outputDir.exists())
			if (!outputDir.mkdirs() || !outputDir.canWrite())
				throw new IllegalArgumentException(outputDir.getAbsolutePath()
						+ " not enough permissions");
		return outputDir;
	}
}
