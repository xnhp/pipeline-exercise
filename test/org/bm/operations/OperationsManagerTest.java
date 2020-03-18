package org.bm.operations;

import org.bm.InvalidArgumentException;
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

    @BeforeAll
    static void setUp() {
        OperationsManager.registerOperations(OriginalOperations.class);
        OperationsManager.registerOperations(AdditionalOperations.class);
    }

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

    @Test
    void inputTypeString() {

        OperationsManager.registerOperations(StandardOperations.class);

        CommandLine cl = setupCommandLine(CLIOptions.instance);
        String[] args = new String[] {
                "--input", "foo.txt",
                "--inputtype", "string",
                "--threads", "1",
                "--chunksize", "1",
                "--output", "bar.txt",
                "--operations", "capitalize"
        };
        cl.parseArgs(args);

        try {
            OperationsManager
                    .assemblePipeline(CLIOptions.instance)
                    .eval("42");
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void inputTypeFail() {
        OperationsManager.registerOperations(StandardOperations.class);

        CommandLine cl = setupCommandLine(CLIOptions.instance);
        String[] args = new String[] {
                "--input", "foo.txt",
                "--inputtype", "int",
                "--threads", "1",
                "--chunksize", "1",
                "--output", "bar.txt",
                "--operations", "neg"
        };
        cl.parseArgs(args);

        assertThrows(ClassCastException.class, () -> {
            OperationsManager
                    .assemblePipeline(CLIOptions.instance)
                    .eval("notanumber");
        });

    }

}