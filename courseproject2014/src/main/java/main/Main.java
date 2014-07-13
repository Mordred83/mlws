package main;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import octojus.demo.Sysout;
import twitter4j.Status;
import twitter4j.TwitterException;
import common.Constants;
import data.FileParser;

public class Main {

	private static final String	PREFIX				= "output";
	private static final int	ID_LIMIT_X_FILE		= 1024 * 1024;
	private static final Date	endFirstWeekDate	= new Date(114, 4, 19); //2014-->3914 !!

	public static void main(String[] args) throws FileNotFoundException, IOException, TwitterException {
		System.out.println("[START]");
		File inputFolder = (args.length > 0) ? new File(args[0]) : Constants.RES_INPUT_FOLDER;
		File outputFolder = (args.length > 1) ? new File(args[1]) : Constants.RES_DEST_DIR;
		if(!inputFolder.exists() && inputFolder.isDirectory()){
			System.err.println("NO input folder: "+inputFolder.getAbsolutePath());
			System.exit(1);
		}
		if(!(outputFolder.mkdirs() || outputFolder.isDirectory()) ){
			System.err.println("NO output folder: "+outputFolder.getAbsolutePath());
			System.exit(1);
		}
		// Create list of gz files
		List<File> sortedFileList = getSortedFileList(inputFolder);
		System.out.println("File to parse: "+sortedFileList.size());
		Set<Long> idSet= new HashSet<Long>();
		boolean hasTerminated = false;
		/*Parse every file in the list until you get a status after the limit.
		* Add every id to a set*/ 
		for(int i=0; i<sortedFileList.size() && !hasTerminated; i++){
				System.out.println("Parsing file: "+(i+1)+" of: "+sortedFileList.size());
				FileParser parser = new FileParser(sortedFileList.get(i));
				for(Status status : parser.getStatus()){
					long id = status.getUser().getId();
					//If is the the first tweet of the second week break
					if(hasTerminated=isStatusCreatedAfter(status, endFirstWeekDate)){
						System.out.println("Found first tweet after date in file: "+sortedFileList.get(i).getAbsolutePath());
						break;
					}
					idSet.add(id);
				}
		}
		System.out.println("[DONE]");
		System.out.println("Total: "+idSet.size());
		//Convert the set to an arraylist and sort it
		ArrayList<Long> sortedIDList = new ArrayList<Long>(idSet);
		Collections.sort(sortedIDList);
		/*for every element in the sortedIdList save the id to a file:
		 * Every IDFile contains at most ID_LIMIT_X_FILE 
		 */
		int oFileID=0;
		File currentFile = new File(outputFolder, PREFIX+oFileID);
		PrintWriter writer = new PrintWriter(currentFile);
		try{
		for(int idCount=0; idCount < sortedIDList.size(); idCount++){
			if(idCount%1024 == 0)System.out.println("["+idCount/1024+"K]");
			long id = sortedIDList.get(idCount);
			//IF id count exceeds the limit switch file
			if(idCount % ID_LIMIT_X_FILE == 0){
				writer.flush();
				writer.close();
				File nextFile = new File(outputFolder, PREFIX+(++oFileID));
				System.out.println("Changing output file from: "+currentFile.getName()+"to: "+nextFile.getName());
				currentFile = nextFile;
				writer = new PrintWriter(currentFile);
			}
			writer.println(id);
		}
		
		}finally{
			if(writer != null){
				writer.flush();
				writer.close();
			}
		}	
		System.out.println("[STOP]");
	}

	/**
	 * Controlla se uno status(tweet) è stato scritto dopo della data specificata in input
	 * 
	 * @param status
	 *            status che si vuole controllare
	 * @param timeLimit
	 *            Date massimo
	 * @return true se lo status è stato scritto dopo il timeLimit, false altrimenti
	 */
	private static boolean isStatusCreatedAfter(Status status, Date timeLimit) {
		Date date = status.getCreatedAt();
		return date.after(timeLimit);
	}

	/**
	 * Ritorna i file di una directory in ordine alfabetico
	 * 
	 * @param inputFolder
	 *            directory nella quale si vogliono ordinare i file
	 * @return lista ordinata di file
	 */
	private static List<File> getSortedFileList(File inputFolder) {
		ArrayList<File> sortedFileList = new ArrayList<File>();
		for (File dir : inputFolder.listFiles(folderFilter)){
			if(dir == null || !dir.exists()){
				System.out.println("no files in input folder: "+inputFolder.getAbsolutePath());
				System.exit(1);
			}
			for (File fileGZ : dir.listFiles(fileFilter)){
				if(fileGZ == null){
					System.out.println("no files in input folder: "+inputFolder.getAbsolutePath());
					System.exit(1);
				}
				sortedFileList.add(fileGZ);
			}
		}
		Collections.sort(sortedFileList, fileComparator);
		return sortedFileList;
	}

	private static final FileFilter			folderFilter	= new FileFilter() {

																@Override
																public boolean accept(File pathname) {
																	return pathname.isDirectory();
																}
															};
	private static final FileFilter			fileFilter		= new FileFilter() {

																@Override
																public boolean accept(File pathname) {
																	String name = pathname.getName();
																	return name.substring(	name.lastIndexOf('.')).equalsIgnoreCase(".gz");
																}

															};
	private static final Comparator<File>	fileComparator	= new Comparator<File>() {

																@Override
																public int compare(File o1, File o2) {
																	return o1.getName().compareTo(	o2.getName());
																}
															};
}
