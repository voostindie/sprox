/*
 * Copyright 2013-2014 Vincent Oostindië
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

package nl.ulso.sprox.resolvers;

import nl.ulso.sprox.ElementNameResolver;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Default {@link nl.ulso.sprox.ElementNameResolver}. It returns method and parameter names as is.
 */
public class DefaultElementNameResolver implements ElementNameResolver {
    @Override
    public String fromParameter(Class<?> controllerClass, Method method, Parameter parameter) {
        return parameter.getName();
    }

    @Override
    public String fromMethod(Class<?> controllerClass, Method method) {
        return method.getName();
    }
}
