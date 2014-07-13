package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import task1.HopLimitedCrawlerQueue;

import common.Constants;

public class CrawlerMain {

	private static final int	THREAD_NUMBER	= 2;
	private static final int	MAX_HOPS		= 1;

	public static void main(String[] args) {
		/** Folder containing idFiles */
		File inputFolder = (args.length > 0) ? new File(args[0]) : Constants.RES_INPUT_FOLDER;
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
		HopLimitedCrawlerQueue<Long> idToCrawl = getInitialIdToCrawl();
		/** ThreadPood for crawler thread */
		ExecutorService eService = Executors.newFixedThreadPool(THREAD_NUMBER);
		/** Twitter Connection TODO: create some class for limit reached or handle into thread logic */
		
		Runnable task = new SpidyRunnable(idSet, crawledIDs, idToCrawl);
		for(int i=0; i<6; i++) eService.execute(task);			
		eService.shutdown();
		try {
			while(!eService.awaitTermination(10, TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(255);
		}

	}

	private static Set<Long> createIDSet(File inputFolder) throws IOException {
		Set<Long> idset = new HashSet<>();
		if (inputFolder.exists() && inputFolder.isDirectory())
			for (File f : inputFolder.listFiles()) {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)))) {
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

	private static HopLimitedCrawlerQueue<Long> getInitialIdToCrawl() {
		HopLimitedCrawlerQueue<Long> idTocrawl = new HopLimitedCrawlerQueue<Long>(MAX_HOPS);
		for (long id : Constants.STARTING_IDS)
			idTocrawl.addElement(0, id);
		return idTocrawl;
	}
}
