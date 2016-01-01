package nl.ulso.sprox;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method to be triggered on a specific node, or an argument to be injected with a node's body content.
 * <p>
 * A method parameter marked with this annotation can have any type you want. Sprox will automatically convert it from
 * the String value in the XML to that type. If you have custom types, you need to provide your own {@link Parser}s.
 * </p>
 * <p>
 * The value of the annotation is optional. If omitted, Sprox will use the name of the method or parameter (depending on
 * where you put the annotation) as the name of the node. <strong>Note that this works only if your controller classes
 * are compiled with the {@code -parameters} option!</strong>
 * </p>
 * <p>
 * If namespaces are used and the namespace is not defined on an annotated method, the namespace defaults to the class
 * default. If namespaces are used and the namespace is not defined on an annotated parameter, the namespace defaults
 * to the method default. This is transitive.
 * </p>
 *
 * @see Parser
 * @see Namespace
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface Node {
    /**
     * @return Name of the node to trigger on or to inject the value of.
     */
    String value() default "";
}
