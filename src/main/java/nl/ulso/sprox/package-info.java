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
 * Package {@code nl.ulso.sprox} defines the public API of the Streaming Processor for XML (Sprox). Clients depend
 * only on types in this package.
 * <p/>
 * Sprox allows you to process XML documents efficiently in one pass. Without having to resort to low-level XML parsers
 * like SAX or StAX. Without having to load complete XML documents in document object models like the
 * {@link org.w3c.dom.Document}, DOM4J, JDOM or XOM. Without having to generate code from XSDs and consume lots of
 * CPU by using an XML marshaller like JAXB or XmlBeans.
 * <p/>
 * Sprox implements the nitty-gritty of parsing XML efficiently, calling high-level code you specify where necessary.
 * You focus on functionality.
 *
 * @see nl.ulso.sprox.XmlProcessorBuilderFactory
 */
package nl.ulso.sprox;