package org.bm.operations;

import java.util.function.Function;

/**
 * Holds the standard operations as per the original requirements.
 * Note that these do not have to be in a specific class, only a reference to
 * the `Function` object is needed.
 *
 * Note that we make the big assumption that all of these transformations are pure.
 *
 * @author Benjamin Moser.
 */
public class StandardOperations {

    @Operation(keyword = "capitalize")
    public final static Function<String, String> capitalize = String::toUpperCase;

    @Operation(keyword = "reverse")
    public final static Function<String, String> reverseString = (String s)
            -> new StringBuffer(s).reverse().toString();

    @Operation(keyword = "neg")
    public final static Function<Integer, Integer> negateInt = (Integer n) -> n * (-1);

    @Operation(keyword = "neg")
    public final static Function<Double, Double> negateDouble = (Double n) -> n * (-1);

}
