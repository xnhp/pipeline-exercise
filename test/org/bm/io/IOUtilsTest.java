package org.bm.io;

import org.bm.cli.CLIOptions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.bm.io.IOUtils.getChunkedStream;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * @author Benjamin Moser.
 */
class IOUtilsTest {

    /**
     * Check whether the stream is chunked correctly.
     */
    @Test
    void chunkStreamTest() {
        Stream<Integer> s = IntStream.range(0, 10).boxed();
        CLIOptions.instance.chunkSize = 3;
        Stream<List<Integer>> chunked = getChunkedStream(s);
        List<List<Integer>> ll = chunked.collect(Collectors.toList());
        assertArrayEquals(ll.get(0).toArray(), new Integer[] {0,1,2});
        assertArrayEquals(ll.get(1).toArray(), new Integer[] {3,4,5});
        assertArrayEquals(ll.get(2).toArray(), new Integer[] {6,7,8});
        assertArrayEquals(ll.get(3).toArray(), new Integer[] {9});
    }

    /**
     * Check whether `chunkStream` keeps the <i>ordered</i> property of the stream.
     */
    @Test
    void isOrdered() {
        Stream<Integer> s = IntStream.range(0, 10).boxed();
        CLIOptions.instance.chunkSize = 3;
        Stream<List<Integer>> chunked = getChunkedStream(s);
        assert !chunked.isParallel();
    }
}