package org.bm.cli;

import picocli.CommandLine.Option;

import java.io.File;
import java.util.List;

/**
 * Holds the initial configuration of the application as supplied via the commandline.
 * This is a singleton object because it will be read-accessed during execution by
 * different procedures and threads.
 *
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

    // a field referencing an instance of this class (lazily initiated)
    instance;

    @Option(names = "--input",
            required = true,   // default value is false
            description = "UTF8-encoded file containing data entries separated by newline.")
    public File inputFile;

    @Option(names = "--inputtype",
            required = true,
            description = "Interpretation of supplied data.")
    public InputType inputType;


    // use custom parser here and map directly to transformer instances?
    // cf https://picocli.info/#_custom_type_converters
    @Option(names = "--operations",
            required = true,
            split = ",",
            description = "Sequence of operations to apply to each line")
    public List<String> operations;
    // todo: using an enum here is not very extensible? would have to modify enum itself
    // to add new operation

    @Option(names = "--threads",
            required = true,
            description = "Number of threads used for reading and operating on the input.")
    public int nThreads;

    @Option(names = "--output",
            description = "Path of output file")
    public File outputFile;
}
