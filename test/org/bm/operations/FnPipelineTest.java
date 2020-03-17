package org.bm.operations;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Benjamin Moser.
 */
class FnPipelineTest {

    Function<String,Integer> length = (String s) -> (Integer) s.length();
    Function<Integer,Double> weirdNegate = (Integer i) -> new Double(i*(-1));
    Function<String,String> uppercase = String::toUpperCase;

    @Test
    void eval() {
        FnPipeline<String, String> myPipeline = new FnPipeline<>("hello world");
        Object res = myPipeline
                .attach(uppercase)
                .attach(length)
                .attach(weirdNegate)
                .eval();

        assertEquals(new Double(-11), (Double) res);
    }


    @Test
    void checkAttachable() {
        Pipeline<Integer, Integer> pip = new FnPipeline<>(new Integer(3));
        assert (!pip.checkAttachable(StandardOperations.negateDouble));
        assert ( pip.checkAttachable(StandardOperations.negateInt));
    }
}