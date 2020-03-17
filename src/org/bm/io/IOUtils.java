package org.bm.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Benjamin Moser.
 */
public class IOUtils {

    public static final int chunkSize = 16;

    public static <T> Stream<List<T>> getChunkedStream(Stream<T> stream) {
        return chunkStream(stream, chunkSize);
    }

    public static <T> Stream<List<T>> chunkStream(Stream<T> stream, int chunkSize) {
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

}
