package org.bm;

import org.bm.cli.CLIOptions;
import java.io.IOException;

/**
 * Main class.
 * 
 * @author KNIME GmbH
 */
public class Main {

	public static void main(String[] args) throws IOException {


		CLIOptions cliOptions = new CLIOptions();
		new picocli.CommandLine(cliOptions).parseArgs(args);

		System.out.printf("b is %b%n", cliOptions.myBool);
		
		// DO NOT CHANGE THE FOLLOWING LINES OF CODE
		System.out.println(String.format("Processed %d lines (%d of which were unique)", //
				Statistics.getInstance().getNoOfLinesRead(), //
				Statistics.getInstance().getNoOfUniqueLines()));
	}

}
