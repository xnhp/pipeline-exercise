package org.bm;

import com.sun.javaws.exceptions.InvalidArgumentException;
import org.bm.cli.CLIOptions;
import org.bm.io.IOUtils;
import org.bm.operations.AdditionalOperations;
import org.bm.operations.OperationsManager;
import org.bm.operations.StandardOperations;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Main class.
 * 
 * @author KNIME GmbH
 */
public class Main {

	public static void main(String[] args) {

		// parse arguments and store values in CLIOptions.instance
		setupCommandLine(CLIOptions.instance).parseArgs(args);

        try {
            OperationsManager.registerOperations(StandardOperations.class);
            OperationsManager.registerOperations(AdditionalOperations.class);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }


		try {

			// obtain a stream of input lines, divided into chunks (lists of some max. size)
			Stream<List<String>> chunkedInput = IOUtils.getChunkedStream(
					Files.lines(CLIOptions.instance.getInputFilePath())
			);
			// process chunks, potentially multithreaded, and call the provided callback on each chunk
			processChunks(
					chunkedInput,
					// this will carry out an implicit .toString conversion
					System.out::println
			);

		} catch (IOException | InvalidArgumentException e) {
		    e.printStackTrace();
			return;
		}


        // access statistics about read data
		// DO NOT CHANGE THE FOLLOWING LINES OF CODE
		System.out.println(String.format("Processed %d lines (%d of which were unique)", //
				Statistics.getInstance().getNoOfLinesRead(), //
				Statistics.getInstance().getNoOfUniqueLines()));
	}


	/**
	 * Given a stream of chunked input, this method uses a fixed-size thread pool to run the operations
	 * as defined via the command-line on each line in each chunk. The result is passed to a callback function.
	 * @param chunkedInput A stream consisting of the input lines, chunked.
	 * @param chunkCallback A <code>Consumer</code> that further processes the resulting data.
	 *                      TODO: since the output type of the pipeline is (more or less) known, we could
	 *                        probably also constrain the type of the value passed to the Consumer
	 */
	public static void processChunks(Stream<List<String>> chunkedInput, Consumer<List<Object>> chunkCallback) throws InvalidArgumentException {
		// java.util.concurrent.Executor is a nice framework to handle the management of a thread pool for us.
		// the the key ingredient is es.submit(•) which submits a task to the thread pool and returns
		// a "Future" object f that repreents the eventual outcome of the computation.
		// This is very useful for collecting the result chunks in order: We immediately receive return objects
		// from the invocations of tasks which we can then process sequentially.
		// On f, we can call .get(), which is blocking. Thus, if our `Future` objects are in order, and we
		// sequentially call .get() on each of them, we will collect results in order.
		ExecutorService es = Executors.newFixedThreadPool(CLIOptions.instance.nThreads);

		// declaring the procedure to be run on a batch of lines
		Function<List<String>, Future<List<Object>>> evalBatch = (List<String> lines) -> {
			// submits the processing of a batch/chunk to the ExecutorService
			// which returns a Future object containing eventual results
			return es.submit(() -> {
				return lines.stream()
						.peek((String l) -> Statistics.getInstance().updateStatisticsWithLine(l))
						.map(OperationsManager::evalLine) // map does not break order within chunk
						.collect(Collectors.toList());
			});
		};

		if (chunkedInput.isParallel()) {
			throw new InvalidArgumentException(new String[]{"stream must be sequential"});
		}

		// executing the procedures on the chunked input
		chunkedInput // note that this stream is ordered
			// process each batch
			.map(evalBatch)
			// access results in order of input
			.forEachOrdered((Future<List<Object>> f) -> {
				try {
					chunkCallback.accept(
						// Future.get is blocking
						f.get()
					);
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			});
	}


	/**
	 * Create and configure a CLI parser.
	 * @param optsObj the object to hold the information parsed from the input args
	 * @return the CLI parser object. Supports further method chaining.
	 */
	public static CommandLine setupCommandLine(CLIOptions optsObj) {
		return new picocli.CommandLine(optsObj)
				.setCaseInsensitiveEnumValuesAllowed(true);
	}

}
