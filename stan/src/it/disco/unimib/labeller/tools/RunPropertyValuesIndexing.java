package it.disco.unimib.labeller.tools;

import it.disco.unimib.labeller.index.AcceptAll;
import it.disco.unimib.labeller.index.EntityValues;
import it.disco.unimib.labeller.index.Evidence;
import it.disco.unimib.labeller.index.IndexFields;
import it.disco.unimib.labeller.index.InputFile;
import it.disco.unimib.labeller.index.Triples;
import it.disco.unimib.labeller.index.TypeHierarchy;
import it.disco.unimib.stan.core.EvaluationPaths;
import it.disco.unimib.stan.core.LogEvents;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.lucene.store.NIOFSDirectory;

public class RunPropertyValuesIndexing {

	public static void main(String[] args) throws Exception {
		String source = args[0];
		String propertiesDirectory = args[1];
		String typesDirectory = args[2];
		String labelsDirectory = args[3];
		int concurrentThreads = Integer.parseInt(args[4]);
		String hierarchy = args[5];
		String knowledgeBase = propertiesDirectory.split("/")[0];
		
		EntityValues types = new EntityValues(new NIOFSDirectory(new File(new EvaluationPaths().indexes().path() + "/" + typesDirectory).toPath()));
		EntityValues labels = new EntityValues(new NIOFSDirectory(new File(new EvaluationPaths().indexes().path() + "/" + labelsDirectory).toPath()));
		
		final Evidence properties = new Evidence(new NIOFSDirectory(new File(new EvaluationPaths().indexes().path() + "/" + propertiesDirectory).toPath()),
															new TypeHierarchy(new InputFile(new File(new EvaluationPaths().path() + "/" + hierarchy))),
															types, 
															labels,
															new IndexFields(knowledgeBase));
		ExecutorService executor = Executors.newFixedThreadPool(concurrentThreads);
		for(final File file : new File(new EvaluationPaths().path() + "/" + source).listFiles()){
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						new Triples(new InputFile(file)).fill(properties, new AcceptAll());
					} catch (Exception e) {
						LogEvents.labelling().error("processing file: " + file, e);
					}
				}
			});
		}
		executor.shutdown();
	    while(!executor.isTerminated()){}
		
		properties.closeWriter();
		types.closeReader();
		labels.closeReader();
	}
}
