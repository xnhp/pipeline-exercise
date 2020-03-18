package org.bm.operations;

import com.sun.javaws.exceptions.InvalidArgumentException;
import org.bm.Main;
import org.bm.cli.CLIOptions;
import org.bm.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.bm.Main.setupCommandLine;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Benjamin Moser.
 */
class OperationsManagerTest {

    static class OriginalOperations {
        @Operation(keyword = "foo")
        public final static Function<String,String> foo = (String s) -> "bar";
    }

    static class AdditionalOperations {
        @Operation(keyword = "foo")
        public final static Function<String, String> foo = (String s) -> "baz";
    }

    @Test
    void operationOverwrite() {
        try {
            OperationsManager.registerOperations(OriginalOperations.class);
            OperationsManager.registerOperations(AdditionalOperations.class);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            fail();
        }

        CommandLine cl = setupCommandLine(CLIOptions.instance);
        String[] args = new String[] {
                "--input", "foo.txt",
                "--inputtype", "string",
                "--threads", "1",
                "--chunksize", "1",
                "--output", "bar.txt",
                "--operations", "foo"
        };
        cl.parseArgs(args);

        Stream<List<String>> stream = IOUtils.getChunkedStream(Stream.of("a", "b", "c", "d"));
        try {
            Main.processChunks(stream, (List<Object> chunkResult) -> {
                assertEquals(chunkResult.get(0), "baz");
            });
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
            fail();
        }
    }

}