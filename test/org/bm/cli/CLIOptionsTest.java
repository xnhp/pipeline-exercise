package org.bm.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static org.bm.Main.setupCommandLine;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Note: We assume correctness of the library and thus only test for the mapping to enum values.
 * @author Benjamin Moser.
 */
class CLIOptionsTest {

    // todo: test input file

    @Test
    void testInputTypeParseCorrect() {
        CommandLine cl = setupCommandLine(CLIOptions.instance);
        String[] args = new String[] {
                "--input", "foo.txt",
                "--inputtype", "string",
                "--threads", "1",
                "--output", "bar.txt"
        };
        cl.parseArgs(args);
        assert CLIOptions.instance.inputType == InputType.STRING;
    }

    @Test
    void testInputTypeParseDifferent() {
        CommandLine cl = setupCommandLine(CLIOptions.instance);
        String[] args = new String[] {
                "--input", "foo.txt",
                "--inputtype", "double",
                "--threads", "1",
                "--output", "bar.txt"
        };
        cl.parseArgs(args);
        assert CLIOptions.instance.inputType != InputType.STRING;
    }

    @Test
    void testInputTypeParseUnknown() {
        assertThrows(picocli.CommandLine.ParameterException.class, () -> {
            CommandLine cl = setupCommandLine(CLIOptions.instance);
            String[] args = new String[] {
                    "--input", "foo.txt",
                    "--inputtype", "baz",
                    "--threads", "1",
                    "--output", "bar.txt"
            };
            cl.parseArgs(args);
        });
    }


}