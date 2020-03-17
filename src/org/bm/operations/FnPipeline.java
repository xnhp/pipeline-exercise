package org.bm.operations;

import java.util.function.Function;

/**
 * An implementation of the Pipeline interface that uses java.util.Function`s composition methods.
 * Note: As opposed to a Java Stream, this acts on a single value
 * @author Benjamin Moser.
 */
public class FnPipeline<A,O> implements Pipeline<A, O> {

    /** we have to keep the base input value because we are actually using
     * eval (i.e. apply) to check for consistency
     */
    A input;

    /**
     * The composed <code>Function</code> representing the pipeline operations
     */
    private Function<A, O> pl;

    /**
     * Constructor constructing a new pipeline from an initial argument and a first function to be applied to it
     * @param in initial argument
     * @param newFn first function in the pipeline
     */
    private FnPipeline(A in, Function<A, O> newFn) {
        this.input = in;
        this.pl = newFn;
    }

    // for initialising with a starting value
    // TODO: move this to a utility function to avoid having a public Constructor not specified by the Interface
    public FnPipeline(A in) {
        this.input = in;
        this.pl = (Function<A, O>) Function.identity();
    }

    /**
     * Extend the current pipeline by using function compoisition provided by java.util.Function
     * @param f The function to attach to the end of this pipeline
     * @param <V> The out-type of the pipeline
     * @return A new pipeline object representing the extended pipeline
     */
    @Override
    public <V> FnPipeline<A,V> attach(
            // f needs to be able to process geq than T (out-type of previous pipeline)
            // f needs to to not return more than the new out-parameter specifiies
            // f needs to not return less than the new out-param (no "? extends V")
            Function<? super O, V> f
    ) {
        return new FnPipeline<A,V>(
                this.input, // input value remains unchanged
                this.pl.andThen(f) // returns a composed function
        );
    }

    /**
     * See interface for documentation
     */
    @Override
    public O eval() {
        return this.pl.apply(input);
    }

    /**
     * See interface for documentation.
     *
     * We define a function to be attachable iff its in-type is cast-compatible to the out-type of the pipeline.
     * This is facilitated by the type variables on <code>attach</code>.
     */
    @Override
    public boolean checkAttachable(Function f) {
        try {
            Pipeline<A, Object> res = this.attach(f);
            // note we have to do `eval` here because Function compositions are evaluated lazily at runtime, thus
            // a potential cast exception occurs only then.
            // todo: avoid laziness by `apply`ing always after attach?
            res.eval();
        } catch (ClassCastException e) {
            return false;
        }
        return true;
    }
}
