package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import task1.data.ValueDistancePair;
import common.Constants;

public class CrawlerMain {
	
	private static final int	THREAD_NUMBER	= 5;
	private static final int	MAX_HOPS		= 1;
	private static final int	MAX_NODES	= 250;
	private static final String OUTPUT_FILE_NAME = "GRAPPH!!!!";

	public static void main(String[] args) {
		int corePoolSize = 0;
		int maximumPoolSize = 5;
		long keepAliveTime = 5L;
		TimeUnit unit = TimeUnit.SECONDS;
		BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
		Map<Long, Collection<Long>> graphMap = new HashMap<Long, Collection<Long>>();
		/** Folder containing idFiles */
		File inputFolder = (args.length > 0) ? new File(args[0]) : Constants.RES_INPUT_FOLDER;
		File outputFolder = (args.length > 1) ? new File(args[1]) : Constants.RES_DEST_DIR;
		/** Set with the IDs */
		Set<Long> idSet;
		try {
			idSet = createIDSet(inputFolder);
		} catch (IOException e) {
			System.out.println("Errore nella lettura del file di input contenente gli user ID");
			e.printStackTrace();
			return;
		}
		/** Set of already crawled ids */
		Set<Long> crawledIDs = new HashSet<Long>();
		/** Queue of ids to crawl */
		BlockingQueue<ValueDistancePair> idToCrawl = getInitialIdToCrawl();
		/** ThreadPood for crawler thread */
		ThreadPoolExecutor eService = new ThreadPoolExecutor(corePoolSize , maximumPoolSize , keepAliveTime, unit , workQueue);
		int count = MAX_NODES;
		while((!idToCrawl.isEmpty() || eService.getActiveCount() > 0) && count > 0){
			
			ValueDistancePair value = null;
			try {
				value = idToCrawl.poll(1L, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(value != null){
				//System.out.println("getted value: "+value.value);
				count--;
				eService.submit(new SpideyRunnable(idSet, crawledIDs, idToCrawl, value, graphMap));
			}
			
		}
		while(eService.getActiveCount() > 0){
			try {
				Thread.sleep(500L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		saveMapToFile(graphMap, new File(outputFolder, OUTPUT_FILE_NAME));

	}

	private static void saveMapToFile(Map<Long, Collection<Long>> graphMap, File graphFile) {
		if(!graphFile.isFile() || !graphFile.exists()) try {
			graphFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(Long key : graphMap.keySet()){
			try(PrintWriter writer = new PrintWriter(new FileOutputStream(graphFile,true))){
				writer.print(key+";");
				for(Long value : graphMap.get(key)){
					writer.print(value+";");
				}
				writer.println();
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			}
		}
	}

	private static Set<Long> createIDSet(File inputFolder) throws IOException {
		Set<Long> idset = new HashSet<Long>();
		if (inputFolder.exists() && inputFolder.isDirectory())
			for (File f : inputFolder.listFiles()) {
				try (BufferedReader reader = new BufferedReader(
																new InputStreamReader(
																						new FileInputStream(
																											f)))) {
					String line = null;
					while ((line = reader.readLine()) != null) {
						Long id = Long.valueOf(line);
						idset.add(id);
					}
				}
			}
		System.out.println("IDs totali: " + idset.size());
		return idset;
	}

	private static BlockingQueue<ValueDistancePair> getInitialIdToCrawl() {
		BlockingQueue<ValueDistancePair> idTocrawl = new LinkedBlockingQueue<ValueDistancePair>();
		for (long id : Constants.STARTING_IDS)
			idTocrawl.add(new ValueDistancePair(id, 0));
		return idTocrawl;
	}
}
