package org.bm;

import com.sun.javaws.exceptions.InvalidArgumentException;
import org.bm.cli.CLIOptions;
import org.bm.operations.*;
import picocli.CommandLine;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Main class.
 * 
 * @author KNIME GmbH
 */
public class Main {

	public static void main(String[] args) throws IOException {

		// parse arguments and store values in CLIOptions.instance
		setupCommandLine(CLIOptions.instance).parseArgs(args);
		// todo: need no reference to created commandline object?

		OperationsManager.registerOperations(StandardOperations.class);
		OperationsManager.registerOperations(AdditionalOperations.class);

		try {
			Pipeline pip = OperationsManager.assembleIntermediate(
					new FnPipeline<>("hello world"),
					CLIOptions.instance.operations
					);
			System.out.println(pip.eval());
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
		}

		// DO NOT CHANGE THE FOLLOWING LINES OF CODE
		System.out.println(String.format("Processed %d lines (%d of which were unique)", //
				Statistics.getInstance().getNoOfLinesRead(), //
				Statistics.getInstance().getNoOfUniqueLines()));
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
