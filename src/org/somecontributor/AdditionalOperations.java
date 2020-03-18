package org.somecontributor;

import org.bm.operations.Operation;

import java.util.function.Function;

/**
 * Additional operations like they would be supplied by a contributor.
 *
 * TODO: create an example with non-trivial datatypes in the pipeline
 *
 * @author Benjamin Moser.
 */
public class AdditionalOperations {
    @Operation(keyword = "length")
    public final static Function<String,Integer> length = (String s) -> (Integer) s.length();

    @Operation(keyword = "reverse")
    public final static Function<String, String> reverse = (String s) -> new StringBuilder(s).reverse().toString();

}
