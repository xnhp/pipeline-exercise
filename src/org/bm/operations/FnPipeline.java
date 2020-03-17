package org.bm.operations;

import java.util.function.Function;

/**
 * As opposed to a Java Stream, this acts on a single value
 * @author Benjamin Moser.
 */
public class FnPipeline<A,O> implements Pipeline<A, O> {

    A input;

    // in-type does not matter anymore (?)
    private Function<A, O> pl;

    // for composing with new functions
    private FnPipeline(A in, Function<A, O> newFn) {
        this.input = in;
        this.pl = newFn;
    }

    // for initialising with a starting value
    public FnPipeline(A in) {
        this.input = in;
        this.pl = (Function<A, O>) Function.identity();
    }

    // this is a ""polymorphic"" method parametrised by V
    // returns a new FnPipeline parametrised by V, i.e. with out-type V
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

    @Override
    public O eval() {
        return this.pl.apply(input);
    }

    // for a given unparametrised function value, check whether it can be attached to the pipeline.
    // note we have to do `eval` here because Function compositions are evaluated lazily at runtime, thus
    // a potential cast exception occurs only then.
    @Override
    public boolean checkAttachable(Function f) {
        try {
            // todo: describe what is happening here and why this gives us type compatibility
            Pipeline<A, Object> res = this.attach(f);
            // todo: avoid laziness by `apply`ing always after attach?
            res.eval();
        } catch (ClassCastException e) {
            return false;
        }
        return true;
    }
}
