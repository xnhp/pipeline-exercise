package org.bm.operations;

import com.sun.javaws.exceptions.InvalidArgumentException;
import org.bm.Statistics;
import org.bm.cli.CLIOptions;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Benjamin Moser.
 */
public class OperationsManager {

    /**
     * Contains the mapping between commands (operations as specified by CLI arguments)
     * and corresponding functions (more than one if several functions fit the command).
     */
    public static Map<String, List<Function>> opsMap = new HashMap<>();

    /**
     * Make operations accessible for being selected by command-line arguments
     * of the provided class, all fields of type java.util.Function with an
     * @Operation annotation will be handled.
     * Note that we assume these fields to be properly expressed: static, Function type, and with annotation
     * TODO: modify attachByCandidates so that operations registered later will be used preferredly
     *  to allow "overwriting"
     * @param clazz A class containg fields of the type <code>Function</code> annotated by <code>@Operation</code>
     */
    public static void registerOperations(Class clazz) throws IllegalAccessException {
        // we use "Reflection" methods to obtain the fields and their annotations
        for (Field f : clazz.getDeclaredFields()) {
            Operation opAnnot = f.getDeclaredAnnotation(Operation.class);
            // only consider annotated fields
            if (opAnnot == null || opAnnot.keyword() == null) continue;
            String keyword = opAnnot.keyword();
            // upsert map with candidate function
            if (!opsMap.containsKey(keyword)) opsMap.put(keyword, new LinkedList<>());
            opsMap.get(keyword).add(
                    // f.get gets the value of the field in the specified object
                    // since we assume f to be a static field, we can "omit" this argument
                    // since we assume f to be properly declared, we cast it to the Function type
                    (Function) f.get(null)
            );
        }
    }

    /**
     * Given an initial value and a list of commands, for each command identify a suitable function and construct
     * a pipeline object representing the Composition of these functions based on the initial value (argument).
     *
     * Candidate functions will be these matching the command keyword.
     *
     * @param init Initial value that the constructed function composition will be applied to.
     * @param cmds Commands that define the function composition
     * @return A <code>Pipeline</code> object representing the function composition on the initial argument. Note that
     * at this point, the actual computations are not necessarily carried out yet.
     */
    public static Pipeline assemblePipeline(String init, List<String> cmds) throws InvalidArgumentException {
        // todo: can express this with stream reduce instead of loop?
        // todo: make use of CLIOptions.instance.inputType to cast input values
        Pipeline pip = new FnPipeline<>(init); // pipeline with init value and identity operation
        for (String cmd : cmds) {
            List<Function> candidates = opsMap.get(cmd);
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
     * TODO: currently, this is the first one that is "attachable" in the sense of Pipeline.checkAttachable
     *
     * @param initPip Pipeline to be extended
     * @param candidates Candidate functions from which to pick one to be attached
     * @return A new Pipeline object, representing the extended pipeline
     * @throws InvalidArgumentException If no candidates or no "attachable" candidates are present.
     */
    private static Pipeline attachByCandidates(Pipeline initPip, List<Function> candidates) throws InvalidArgumentException {
        List<Function> matches = candidates.stream()
                .filter(initPip::checkAttachable)
                .collect(Collectors.toList());
        Function f = matches.get(matches.size()-1);

        // we leave this call unchecked w.r.t type parameters because `checkAttachable` ensures this for us.
        return initPip.attach(f);
    }

    /**
     * The procedure to be applied to each line.
     * @throws InvalidArgumentException In case no operations can be found for a given command
     * @return
     */
    public static String evalLine (String line) {
        Statistics.getInstance().updateStatisticsWithLine(line);
        try {
            return OperationsManager
                    .assemblePipeline(line, CLIOptions.instance.operations)
                    .eval()
                    .toString(); // todo
        } catch (InvalidArgumentException e) {
            System.out.println("No operation found for given command");
            e.printStackTrace();
            return null;
        }
    };
}
