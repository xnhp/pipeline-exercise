package org.bm.operations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Benjamin Moser.
 */
// keep annotations to runtime
@Retention(RetentionPolicy.RUNTIME)
// these annotations may only apply to fields
@Target(ElementType.FIELD)
public @interface Operation {
    String keyword();
}
