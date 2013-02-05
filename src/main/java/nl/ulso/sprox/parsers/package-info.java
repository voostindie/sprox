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

/**
 * Package {@code nl.ulso.sprox.parsers} contains implementations of parsers for primitive types.
 * <p/>
 * All parsers in this package are available by default in all XML processors. That means that it's possible to inject
 * any node or attribute as any primitive type.
 * <p/>
 * By writing your own parsers and registering them with an {@link nl.ulso.sprox.XmlProcessorBuilder} you can
 * support additional types, or replace the default parsers.
 * <p/>
 * Each parser implementation in this package may serve as a base class for your custom implementations.
 *
 * @see nl.ulso.sprox.XmlProcessorBuilder
 */
package nl.ulso.sprox.parsers;