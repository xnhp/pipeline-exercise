package org.bm.cli;

import picocli.CommandLine.Option;

/**
 * Holds the initial configuration of the application as supplied via the commandline.
 * Powered by the PicoCLI library.
 *
 * Making this an enum is a way to express the Singleton pattern. Here,
 * `instance` is just a private, static, final field of type CLIOptions.
 * This is more concise and is lazily initialised.
 *
 * https://stackoverflow.com/q/26285520/156884
 * https://stackoverflow.com/q/20826712/156884
 * https://stackoverflow.com/q/70689/156884
 * https://14b1424d-a-62cb3a1a-s-sites.googlegroups.com/site/io/effective-java-reloaded/effective_java_reloaded.pdf
 *
 * @see picocli.CommandLine
 * @author Benjamin Moser.
 */
public enum CLIOptions {

    instance;

    @Option(names = "-b", description = "foo")
    public boolean myBool;
}
