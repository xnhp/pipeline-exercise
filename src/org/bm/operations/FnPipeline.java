package org.bm.operations;

import javafx.util.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * An implementation of the Pipeline interface that uses java.util.Function`s composition methods.
 * Note: As opposed to a Java Stream, this acts on a single value
 * @author Benjamin Moser.
 */
public class FnPipeline<A,O> implements Pipeline<A, O> {

    /**
     * The output type of the function. Have to keep this updated when attaching
     */
    private Type plOutT;

    /**
     * The composed <code>Function</code> representing the pipeline operations
     */
    private Function<A, O> pl;

    /**
     * Constructor constructing a new pipeline from an initial argument and a first function to be applied to it
     * @param newFn first function in the pipeline
     */
    private FnPipeline(Function<A, O> newFn, Type newFnOutT) {
        this.pl = newFn;
        this.plOutT = newFnOutT;
    }

    // for initialising with a starting value
    // TODO: move this to a utility function to avoid having a public Constructor not specified by the Interface
    public FnPipeline(Type initialType) {
        this.pl = (Function<A, O>) Function.identity();
        this.plOutT = initialType;
    }

    /**
     * Extend the current pipeline by using function composition provided by java.util.Function
     * @param f The function to attach to the end of this pipeline
     * @param <V> The out-type of the pipeline
     * @return A new pipeline object representing the extended pipeline
     */
    @Override
    public <V> FnPipeline<A,V> attach(
            Field f
    ) {

        try {
            // f.get gets the value of the field in the specified object
            // since we assume f to be a static field, we can "omit" this argument
            // since we assume f to be properly declared, we cast it to the Function type
            Function fn = (Function) f.get(null);
            return new FnPipeline<A,V>(
                    // input value remains unchanged
                    this.pl.andThen(fn), // returns a composed function
                    getFnOutT(f)
            );
        } catch (IllegalAccessException e) {
            e.printStackTrace(); // todo
            return null;
        }
    }

    /**
     * See interface for documentation
     */
    @Override
    public O eval(A in) {
        return this.pl.apply(in);
    }

    /**
     * See interface for documentation.
     *
     * We define a function to be attachable iff its in-type is cast-compatible to the out-type of the pipeline.
     * This is facilitated by the type variables on <code>attach</code>.
     */
    @Override
    // note: when calling this with e.g. a Function<String,String> we are actually performing an up-cast
    public boolean checkAttachable(Field f) {
        Type fInT = getFnInT(f);

        // if both are parameterised types, additionally check for equality of their parameters
        if (fInT instanceof ParameterizedType && plOutT instanceof ParameterizedType) {
            ArrayList<Type> inTypeArgs = new ArrayList<>(Arrays.asList(
                    ((ParameterizedType) fInT).getActualTypeArguments()
            ));
            ArrayList<Type> plTypeArgs = new ArrayList<>(Arrays.asList(
                    ((ParameterizedType) plOutT).getActualTypeArguments()
            ));
            return IntStream
                    // obtain stream over ints ranging to smaller size
                    .range(0, Math.min(inTypeArgs.size(), plTypeArgs.size()))
                    // "zip" both lists, i.e. create pairs
                    .mapToObj(i -> new Pair<>(inTypeArgs.get(i), plTypeArgs.get(i)))
                    // check if the type parameters match
                    .allMatch((Pair<Type,Type> p) -> p.getKey() == p.getValue());
        }

        return fInT == plOutT;
        // todo: we only check for exact matches, not subtyping relationships
        //  this is probably possible by obtaining the Class<?> object from the Type and using isInstance
    }

    private Type getFnInT(Field f) {
        return ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
    }

    private Type getFnOutT(Field f) {
        return ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[1];
    }
}
