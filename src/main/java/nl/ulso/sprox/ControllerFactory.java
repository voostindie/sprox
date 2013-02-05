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

/**
 * Factory for controllers. Use these if controller objects and controller classes are not sufficient.
 */
public interface ControllerFactory<T> {

    /**
     * Creates a controller of type {@code T}; called exactly once per processor execution.
     *
     * @return The controller to use; never {@code null}.
     */
    T createController();
}
