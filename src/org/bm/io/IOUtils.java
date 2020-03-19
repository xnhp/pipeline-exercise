package org.bm.io;

import org.bm.cli.CLIOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Benjamin Moser.
 */
public class IOUtils {

    /**
     * Divide the stream into chunks of size as defined via CLI options.
     * @param stream A stream to be chunked.
     * @param <T> The type of the elements in the stream
     * @return A new stream with elements of type List<T> where each lists represents a chunk of the elements of the
     * input stream.
     */
    public static <T> Stream<List<T>> getChunkedStream(Stream<T> stream) {
        return chunkStream(stream, CLIOptions.instance.chunkSize);
    }

    private static <T> Stream<List<T>> chunkStream(Stream<T> stream, int chunkSize) {
        // this needs to be atomic since the stream is potentially processed in asynchroneously
        AtomicInteger index = new AtomicInteger(0);

        return stream
                // collect the elements of the stream into a map defined by some grouping attribute
                .collect(Collectors.groupingBy(
                        // for each element of the stream, we obtain floor(index/chunkSize)
                        // and use it to group the stream into chunks
                        x -> index.getAndIncrement() / chunkSize)
                )
                // obtain only the elements of the map (i.e. not the indices)
                .entrySet()
                // obtain a stream of individual `Map.Entry`s (i.e. key-value pairs)
                .stream()
                // obtain this stream, sorted by the natural ordering on its keys (indices)
                .sorted(Map.Entry.comparingByKey())
                // obtain a stream containing only the values of these entries
                .map(Map.Entry::getValue);
    }


    private static List<String> chunkToString(List<Object> chunk) {
        return chunk.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }


    public static void writeToStdOut(List<Object> resultChunk) {
        List<String> strings = chunkToString(resultChunk);
        strings.stream().forEachOrdered((String s) -> {System.out.println(s);});
    }

    /**
     * Write a processed chunk to the output file
     * @param resultChunk
     */
    public static void writeToOutFile(List<Object> resultChunk) {

        // convert results to strings
        List<String> strings = chunkToString(resultChunk);

        try {
            // use NIO Files over FileWriter because it handles writing seperate lines with
            // platform-specific newlines
            // todo: this opens/closes the file on every call of this method
            //  rather use some static file writer instance
            // todo: appends to file if already exists
            Files.write(
                    CLIOptions.instance.getOutputFilePath(),
                    strings,
                    StandardOpenOption.APPEND,
                    StandardOpenOption.CREATE
            );
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
