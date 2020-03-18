package org.bm;

import org.bm.cli.CLIOptions;
import org.bm.io.IOUtils;
import org.bm.operations.AdditionalOperations;
import org.bm.operations.OperationsManager;
import org.bm.operations.StandardOperations;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.util.List;
import java.util.stream.Stream;

import static org.bm.Main.setupCommandLine;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Benjamin Moser.
 */
class StatisticsTest {

    @Test
    void test() {

        OperationsManager.registerOperations(StandardOperations.class);
        OperationsManager.registerOperations(AdditionalOperations.class);

        CommandLine cl = setupCommandLine(CLIOptions.instance);
        String[] args = new String[] {
                "--input", "foo.txt",
                "--inputtype", "string",
                "--threads", "1",
                "--output", "bar.txt",
                "--operations", "length"
        };
        cl.parseArgs(args);

        Statistics.resetInstance();
        Statistics s = Statistics.getInstance();
        // we assume File.lines to be correct
        Stream<List<String>> stream = IOUtils.getChunkedStream(Stream.of("foo", "bar", "baz", "foo"));
        try {
            Main.processChunks(stream, (l) -> {}); // do nothing with results
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
            fail();
        }
        assertEquals(s.getNoOfLinesRead(), 4);
        assertEquals(s.getNoOfUniqueLines(), 3);
    }
}