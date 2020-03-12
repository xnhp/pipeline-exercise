package org.bm.cli;

import picocli.CommandLine.Option;

/**
 * @author Benjamin Moser.
 */
public class CLIOptions {

    @Option(names = "-b", description = "foo")
    public boolean myBool;
}
