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
     * @param f The field representing the operation to attach
     * @param <V> The output type of <code>f</code> and the output type of the new pipeline.
     * @return a new pipeline parameterised by V, i.e. with out-type V
     */
    <V> Pipeline<A,V> attach(
            // todo: comment on Function<? super O, V> f,
            Field f
    );

    /**
     * Actually compute and return the result value of this pipeline.
     * @param in The value to apply the pipeline to
     * @return The result value of this pipeline based on the given argument
     */
    O eval(A in);

    /**
     * For a given unparameterised function value, check whether it can be attached to the pipeline.
     * @param f The function to be checked
     * @return true iff the function could be attached to the pipeline.
     */
    boolean checkAttachable(Field f);
}
