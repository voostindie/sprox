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

import java.io.StringReader;

import static nl.ulso.sprox.XmlProcessorFactory.createXmlProcessorBuilder;
import static org.junit.Assert.assertEquals;

/**
 * Utility methods for testing Sprox
 */
public final class SproxTests {

    private SproxTests() {
    }

    public static <T> void testProcessor(T expected, String xml, XmlProcessor<T> processor) {
        final T actual = processor.execute(new StringReader(xml));
        assertEquals(expected, actual);
    }

    public static <T> void testControllers(T expected, String xml, Object... controllers) {
        @SuppressWarnings("unchecked")
        final XmlProcessorBuilder<Object> builder = (XmlProcessorBuilder<Object>) createXmlProcessorBuilder(expected.getClass());
        for (Object controller : controllers) {
            if (controller instanceof Class) {
                builder.addControllerClass((Class) controller);
            } else if (controller instanceof ControllerFactory) {
                builder.addControllerFactory((ControllerFactory<?>) controller);
            } else {
                builder.addControllerObject(controller);
            }
        }
        final XmlProcessor<Object> processor = builder.buildXmlProcessor();
        testProcessor(expected, xml, processor);
    }
}
