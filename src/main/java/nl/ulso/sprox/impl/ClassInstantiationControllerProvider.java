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

package nl.ulso.sprox.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Provides controllers by instantiating them with reflection.
 */
final class ClassInstantiationControllerProvider implements ControllerProvider {
    private final Class<?> instanceClass;
    private final Constructor constructor;

    ClassInstantiationControllerProvider(Class<?> instanceClass) {
        this.instanceClass = instanceClass;
        try {
            this.constructor = instanceClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(
                    "Controller class doesn't have a no-arg constructor: " + instanceClass.getName(), e);
        }
    }

    @Override
    public Object getController() {
        try {
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Couldn't instantiate controller class: " + instanceClass.getName(), e);
        }
    }
}
