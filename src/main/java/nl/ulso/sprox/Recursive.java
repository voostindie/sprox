package nl.ulso.sprox;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method annotated with {@link Node} to be recursive, meaning that the node may appear in the XML input
 * in a hierarchy of repeating nodes.
 * <p>
 * Recursive nodes are a bit harder to process than non-recursive nodes. Don't annotate nodes with {@link Recursive}
 * unless absolutely necessary.
 * </p>
 *
 * @see Node
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Recursive {
}
