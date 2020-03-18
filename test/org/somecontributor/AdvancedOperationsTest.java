package org.somecontributor;

import org.bm.InvalidArgumentException;
import org.bm.Main;
import org.bm.cli.CLIOptions;
import org.bm.io.IOUtils;
import org.bm.operations.OperationsManager;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.bm.Main.setupCommandLine;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Benjamin Moser.
 */
class AdvancedOperationsTest {

    @Test
    void drop2() {
        ArrayList<Character> l = new ArrayList<>();
        l.add('a'); l.add('b'); l.add('c'); l.add('d'); l.add('e');
        assertArrayEquals(
                new Character[] {'c', 'd', 'e'},
                AdvancedOperations.drop2.apply(l).toArray()
        );
    }

    @Test
    void composition() {

        // not tested if these arent overwritten
        OperationsManager.registerOperations(AdvancedOperations.class);

        CommandLine cl = setupCommandLine(CLIOptions.instance);
        String[] args = new String[] {
                "--input", "foo.txt",
                "--inputtype", "string",
                "--threads", "1",
                "--chunksize", "1",
                "--output", "bar.txt",
                "--operations", "split,drop2,concat"
        };
        cl.parseArgs(args);

        Stream<List<String>> stream = IOUtils.getChunkedStream(Stream.of("hello world"));
        try {
            Main.processChunks(stream, (List<Object> chunkResult) -> {
                assertEquals(chunkResult.get(0), "llo world");
            });
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
            fail();
        }
    }
}