package nl.ulso.sprox;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Collects a list of {@link Namespace}s used in a single controller. Note that on JDK 8 and up, you don't need to use
 * this annotation in your code; just use as many {@link Namespace} annotations as necessary.
 *
 * @see Namespace
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Namespaces {

    /**
     * @return The list of namespaces used in the controller.
     */
    Namespace[] value();
}
