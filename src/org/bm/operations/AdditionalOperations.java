package org.bm.operations;

import java.util.function.Function;

/**
 * @author Benjamin Moser.
 */
public class AdditionalOperations {
    @Operation(keyword = "length")
    public final static Function<String,Integer> length = (String s) -> (Integer) s.length();
}
