package org.bm;

import java.util.HashSet;
import java.util.Set;

/**
 * Captures statistics about the lines being read from the input file.
 * 
 * @author KNIME GmbH
 */
public class Statistics {

	private static Statistics instance;

	private final Set<String> linesRead = new HashSet<>();

	private int lineCounter;

	/**
	 * Updates statistics with respect to the given line. This method is supposed to
	 * be called when a new line has been read from the input file.
	 * 
	 * @param line
	 *            A new line that has been read from the input file.
	 */
	public void updateStatisticsWithLine(final String line) {
		lineCounter++;
		linesRead.add(line);
	}

	/**
	 * 
	 * @return the total number of lines read.
	 */
	public int getNoOfLinesRead() {
		return lineCounter;
	}

	/**
	 * 
	 * @return the number of unique lines read.
	 */
	public int getNoOfUniqueLines() {
		return linesRead.size();
	}

	/**
	 * 
	 * @return the shared {@link Statistics} instance to use.
	 */
	public static Statistics getInstance() {
		if (instance == null) instance = new Statistics();
		return instance;
	}

	/**
	 * Reset the singleton instance to a fresh one.
	 * This is needed for writing tests since running several tests in succession will share the same singleton
	 * @return the fresh instance
	 */
	public static Statistics resetInstance() {
		instance = new Statistics();
		return instance;
	}
}
