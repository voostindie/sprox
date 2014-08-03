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

package nl.ulso.sprox.serviceloader;

import nl.ulso.sprox.XmlProcessorBuilderFactory;
import nl.ulso.sprox.impl.StaxBasedXmlProcessorBuilderFactory;
import org.junit.Test;

import java.util.Iterator;
import java.util.ServiceLoader;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests that the {@link XmlProcessorBuilderFactory} is registered in the JDK's ServiceLoader.
 */
public class ServiceLoaderTest {
    @Test
    public void testServiceLoaderMechanism() throws Exception {
        ServiceLoader.load(XmlProcessorBuilderFactory.class).iterator().next();
        final Iterator<XmlProcessorBuilderFactory> iterator = ServiceLoader.load(XmlProcessorBuilderFactory.class).iterator();
        assertTrue(iterator.hasNext());
        final XmlProcessorBuilderFactory factory = iterator.next();
        assertNotNull(factory);
        assertTrue(factory instanceof StaxBasedXmlProcessorBuilderFactory);

    }
}
