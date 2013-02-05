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

package nl.ulso.sprox.impl;

/**
 * Provides controller objects. The {@link StaxBasedXmlProcessor} keeps a list of providers. Right before processing
 * an XML, it accesses each provider once to acquire a controller for that run. Those controllers are then stored in
 * the {@link ExecutionContext}, storing them for that run only.
 */
interface ControllerProvider {
    Object getController();
}
