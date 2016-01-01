package nl.ulso.sprox;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an object parameter with the source of which the result must be injected. This is useful if there are
 * several controller methods that generate objects of the same type, which must then be injected into separate
 * parameters.
 * <p>
 * The value of the annotation is optional. If omitted, Sprox will use the name of the parameter as the name of the
 * source. <strong>Note that this works only if your controller classes are compiled with the {@code -parameters}
 * option!</strong>
 * </p>
 * <p>
 * If namespaces are used and the namespace is not defined, the namespace defaults to the method default.
 * </p>
 *
 * @see Namespace
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Source {
    /**
     * @return Name of the node for which the annotated result must be injected.
     */
    String value() default "";
}
