package nl.ulso.sprox;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method parameter as a value to be extracted from an attribute.
 * <p>
 * The value of the annotation is optional. If omitted, Sprox will use the name of the parameter as the name of the
 * attribute. <strong>Note that this works only if your controller classes are compiled with the {@code -parameters}
 * option!</strong>
 * </p>
 * <p>
 * A method parameter marked with this annotation can have any type you want. Sprox will automatically convert it from
 * the String value in the XML to that type. If you have custom types, you need to provide your own {@link Parser}s.
 * </p>
 * <p>
 * If namespaces are used and the namespace is not defined, the namespace defaults to the method default.
 * </p>
 *
 * @see Parser
 * @see Namespace
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Attribute {
    /**
     * @return Name of the attribute to trigger on.
     */
    String value() default "";
}
