package org.somecontributor;

import org.bm.operations.Operation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Benjamin Moser.
 */
public class AdvancedOperations {
    @Operation(keyword = "split")
    public final static Function<String, List<Character>> splitStringToChars = (String s) -> {
        ArrayList<Character> r = new ArrayList<Character>();
        for (char c : s.toCharArray()) {
            r.add(c);
        }
        return r;
    };

    @Operation(keyword = "drop2")
    public final static Function<List<Character>, List<Character>> drop2 = (List<Character> l) -> {
        ArrayList<Character> r = new ArrayList<>();
        for (int i = 2; i < l.size(); i++) {
            r.add(l.get(i));
        }
        return r;
    };

    @Operation(keyword = "concat")
    public final static Function<List<Character>, String> concat = (List<Character> l) -> {
        String s = "";
        for (Character c : l) {
            s = s+c;
        }
        return s;
    };
}
