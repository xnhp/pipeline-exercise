package org.bm.operations;

import com.sun.javaws.exceptions.InvalidArgumentException;
import org.bm.cli.CLIOptions;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Benjamin Moser.
 */
public class OperationsManager {

    /**
     * Contains the mapping between commands (operations as specified by CLI arguments)
     * and corresponding functions (more than one if several functions fit the command).
     */
    public static Map<String, List<Field>> opsMap = new HashMap<>();

    /**
     * Make operations accessible for being selected by command-line arguments
     * of the provided class, all fields of type java.util.Function with an
     * @Operation annotation will be handled.
     * Note that we assume these fields to be properly expressed: static, Function type, and with annotation
     * @param clazz A class containg fields of the type <code>Function</code> annotated by <code>@Operation</code>
     */
    public static void registerOperations(Class clazz) {
        // we use "Reflection" methods to obtain the fields and their annotations
        for (Field field : clazz.getDeclaredFields()) {
            Operation opAnnot = field.getDeclaredAnnotation(Operation.class);
            // only consider annotated fields
            if (opAnnot == null || opAnnot.keyword() == null) continue;
            String keyword = opAnnot.keyword();
            // todo: filter if f.getDeclaredType is instance of ParameterizedTypeImpl
            //   and has two type arguments
            //   and maybe also check if is a Function type

            // upsert map with candidate function
            if (!opsMap.containsKey(keyword)) opsMap.put(keyword, new LinkedList<>());
            opsMap.get(keyword).add(
                    field
            );
        }
    }

    /**
     * Given an initial value and a list of commands, for each command identify a suitable function and construct
     * a pipeline object representing the Composition of these functions based on the initial value (argument).
     *
     * Candidate functions will be these matching the command keyword.
     *
     * @return A <code>Pipeline</code> object representing the function composition on the initial argument. Note that
     * at this point, the actual computations are not necessarily carried out yet.
     */
    public static Pipeline assemblePipeline(CLIOptions opts) throws InvalidArgumentException {
        // construct an initial pipeline with the correct out type
        Pipeline pip;
        try {
            switch (opts.inputType) {
                case INT:    pip = new FnPipeline<Integer,Integer>(Integer.class); break;
                case DOUBLE: pip = new FnPipeline<Double,Double>(Double.class); break;
                // STRING and default
                default:     pip = new FnPipeline(String.class); break;
            }
        } catch (NumberFormatException e) {
            // NumberFormatException is a subclass of InvalidArgumentException
            // thus a catch in a parent method would have to make an explicit instanceof check
            // to avoid this, we construct and throw a new exception here that holds information about the error cause
            throw new InvalidArgumentException(new String[]{"Could not parse number from input"});
        }

        // identify and attach given operations
        for (String cmd : opts.operations) {
            List<Field> candidates = opsMap.get(cmd);
            if (candidates == null) {
                throw new InvalidArgumentException(new String[]{"unrecognised command"});
            }
            pip = attachByCandidates(pip, candidates);
        }
        return pip;
    }

    /**
     * From a list of candidate functions, determine a "best suited" one to be attached to the pipeline.
     *
     * @param initPip Pipeline to be extended
     * @param candidates Candidate functions from which to pick one to be attached
     * @return A new Pipeline object, representing the extended pipeline
     * @throws InvalidArgumentException If no candidates or no "attachable" candidates are present.
     */
    private static Pipeline attachByCandidates(Pipeline initPip, List<Field> candidates) throws InvalidArgumentException {
        List<Field> matches = candidates.stream()
                .filter(initPip::checkAttachable)
                .collect(Collectors.toList());

        if (matches.size() == 0) {
            throw new InvalidArgumentException(new String[]{"No matching operation"});
        }

        Field f = matches.get(matches.size()-1);

        // we leave this call unchecked w.r.t type parameters because `checkAttachable` ensures this for us.
        return initPip.attach(f);
    }
}
