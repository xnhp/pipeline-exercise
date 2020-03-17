package org.bm.operations;

import com.sun.javaws.exceptions.InvalidArgumentException;
import org.bm.cli.CLIOptions;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

/**
 * @author Benjamin Moser.
 */
public class OperationsManager {
    public static Map<String, List<Function>> opsMap = new HashMap<>();

    public static void registerOperations(Class clazz) {
        for (Field f : clazz.getDeclaredFields()) {
            Operation opAnnot = f.getDeclaredAnnotation(Operation.class);
            if (opAnnot == null || opAnnot.keyword() == null) continue;
            String keyword = opAnnot.keyword();
            try {
                if (!opsMap.containsKey(keyword)) opsMap.put(keyword, new LinkedList<>());
                opsMap.get(keyword).add((Function) f.get(null)); // assume that annotations are correct
            } catch (ClassCastException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static Pipeline assemblePipeline(String init, List<String> cmds) throws InvalidArgumentException {
        // todo: can do this with stream reduce instead of loop?
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

    private static Pipeline attachByCandidates(Pipeline initPip, List<Function> candidates) throws InvalidArgumentException {
        Function f =  candidates.stream()
                .filter(initPip::checkAttachable)
                .findFirst()
                .orElseThrow(() -> new InvalidArgumentException(new String[]{"no operation with this keyword fits into specified pipeline"}));
        return initPip.attach(f);
    }


    public static Function<String,String> evalLine = (String line) -> {
        try {
            return OperationsManager
                    .assemblePipeline(line, CLIOptions.instance.operations)
                    .eval()
                    .toString(); // todo
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
        return null;
    };
}
