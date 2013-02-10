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
 * Declares a list of {@link Namespace}s used in a controller.
 * <p/>
 * If a single controller handles XML elements from different namespaces, use this annotation to declare them, giving
 * each namespace a different shorthand.
 * <p/>
 * The first namespace in the list of namespaces is the default namespace. You can refer to elements in this
 * namespace without using its shorthand. Therefore it doesn't require one, unless of course you need it in your
 * code.
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
