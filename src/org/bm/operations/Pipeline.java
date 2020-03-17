package org.bm.operations;

import java.util.function.Function;

/**
 * @author Benjamin Moser.
 */
public interface Pipeline<A, O> {
    // this is a ""polymorphic"" method parametrised by V
    // returns a new FnPipeline parametrised by V, i.e. with out-type V
    <V> FnPipeline<A,V> attach (
            // f needs to be able to process geq than T (out-type of previous pipeline)
            // f needs to to not return more than the new out-parameter specifiies
            // f needs to not return less than the new out-param (no "? extends V")
            Function<? super O, V> f
    );

    O eval();

    // for a given unparametrised function value, check whether it can be attached to the pipeline.
    // note we have to do `eval` here because Function compositions are evaluated lazily at runtime, thus
    // a potential cast exception occurs only then.
    boolean checkAttachable(Function f);
}
