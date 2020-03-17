package org.bm.operations;

import java.util.function.Function;

/**
 * Holds the standard operations as per the original requirements.
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
