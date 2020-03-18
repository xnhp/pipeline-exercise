package org.bm.operations;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Benjamin Moser.
 */
class FnPipelineTest {

    Function<String,Integer> length = (String s) -> (Integer) s.length();
    Function<Integer,Double> weirdNegate = (Integer i) -> new Double(i*(-1));
    Function<String,String> uppercase = String::toUpperCase;

    Function<Double,Double> testNegateDouble = (Double n) -> n*(-1);
    Function<Integer,Integer> testNegateInt = (Integer n) -> n*(-1);

    @Test
    void eval() {
        /*FnPipeline<String, String> myPipeline = new FnPipeline<>("hello world");
        Object res = myPipeline
                .attach(uppercase)
                .attach(length)
                .attach(weirdNegate)
                .eval();

        assertEquals(new Double(-11), (Double) res);*/
        // todo
    }


    @Test
    void checkAttachable() {
        try {
            Field negateInt = this.getClass().getDeclaredField("testNegateInt");
            Field negateDouble = this.getClass().getDeclaredField("testNegateDouble");

            Pipeline<Integer, Integer> pip = new FnPipeline<Integer,Integer>(Integer.class);
            assert(!pip.checkAttachable(negateDouble));
            assert( pip.checkAttachable(negateInt));

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            fail();
        }
    }

}