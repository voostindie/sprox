/*
 * Copyright 2013 Vincent Oostindië
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package nl.ulso.sprox;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method to be triggered on a specific node, or an argument to be inject with a node's body content.
 * <p/>
 * A method parameter marked with this annotation can have any type you want. Sprox will automatically convert it from
 * the String value in the XML to that type. If you have custom types, you need to provide your own {@link Parser}s.
 * <p/>
 * If namespaces are used and the namespace is not defined on an annotated method, the namespace defaults to the class
 * default. If namespaces are used and the namespace is not defined on an annotated parameter, the namespace defaults
 * to the method default. This is transitive.
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
    String value();

    /**
     * @return Shorthand of the namespace as declared in the {@link Namespace} annotation. When using namespaces,
     *         use this to use a namespace different than the class or method default.
     */
    String ns() default "";
}
