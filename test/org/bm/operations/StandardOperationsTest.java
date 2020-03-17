package org.bm.operations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Benjamin Moser.
 */
class StandardOperationsTest {

    @Test
    void capitalise() {
        String s = "foo Bar baz";
        assertEquals(
                StandardOperations.capitalize.apply(s),
                "FOO BAR BAZ"
        );
    }

    @Test
    void reverse() {
        String s = "foo";
        assertEquals(StandardOperations.reverseString.apply(s),"oof");
    }

    @Test
    void negInteger() {
        Integer n = new Integer(123);
        assertEquals(
                StandardOperations.negateInt.apply(n).intValue(),
                // take care not to compare with primitive number
                new Integer(-123).intValue()
        );
    }

    @Test
    void negDouble() {
        Double n = new Double(123);
        assertEquals(StandardOperations.negateDouble.apply(n).doubleValue(), new Double(-123).doubleValue());
    }


}