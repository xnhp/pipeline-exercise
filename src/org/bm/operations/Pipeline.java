package org.bm.operations;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.function.Function;

/**
 *
 * TODO: implementation like FnPipeline but without laziness (i.e. evaluation at attach)?
 * TODO: implement a version for this based on java.util.stream with map?
 *
 * @param <A> representing the type of the initial argument of the pipeline
 * @param <O> representing the type of the resulting value of the pipeline
 *
 * @author Benjamin Moser
 */
public interface Pipeline<A, O> {

    /**
     * Creates a new Pipeline object based on this one with <code>f</code> attached.
     * @param f The function to attach to the end of this pipeline
     * @param <V> The output type of <code>f</code> and the output type of the new pipeline.
     * @return a new pipeline parameterised by V, i.e. with out-type V
     * todo update this
     */
    <V> Pipeline<A,V> attach(
            // todo: comment on Function<? super O, V> f,
            Field f
    );

    /**
     * todo update doc
     * Actually compute and return the result value of this pipeline.
     * This is particularly needed in case of lazy implementations.
     * @return The result value of this pipeline based on ints initial argument.
     */
    O eval(A in);

    /**
     * For a given unparameterised function value, check whether it can be attached to the pipeline.
     * @param f The function to be checked
     * @return true iff the function could be attached to the pipeline.
     */
    boolean checkAttachable(Field f);
}
