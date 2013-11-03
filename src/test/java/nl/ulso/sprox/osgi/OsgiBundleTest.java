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

package nl.ulso.sprox.osgi;

import nl.ulso.sprox.Node;
import nl.ulso.sprox.XmlProcessor;
import nl.ulso.sprox.XmlProcessorBuilderFactory;
import nl.ulso.sprox.opml.OutlineFactoryTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import javax.inject.Inject;
import java.io.StringReader;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.ops4j.pax.exam.CoreOptions.*;

/**
 * Tests Sprox as an OSGi bundle. This test is kind of special, because normally OSGi integration tests are implemented
 * in separate test modules. In this case, it's all in one module. That requires some special configuration in the
 * Maven project (see {@code pom.xml} as well as here.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class OsgiBundleTest {

    @Inject
    private XmlProcessorBuilderFactory factory;

    @Configuration
    public Option[] config() {
        return options(
                bundle("reference:file:target/classes"), // Add the compiler output as a bundle
                mavenBundle("joda-time", "joda-time", "2.3"),
                junitBundles()
        );
    }

    @Test
    public void testXmlProcessorBuilderFactory() throws Exception {
        assertNotNull(factory);
    }

    @Test
    public void testNodeCounterProcessor() throws Exception {
        final XmlProcessor<Integer> processor = factory.createXmlProcessorBuilder(Integer.class)
                .addControllerClass(NodeCounter.class)
                .buildXmlProcessor();
        final int count = processor.execute(new StringReader("<root><node/><node/><node/></root>"));
        assertThat(count, is(3));
    }

    @Test
    public void testOpmlInOsgiEnvironment() throws Exception {
        final OutlineFactoryTest test = new OutlineFactoryTest();
        test.setFactory(factory);
        test.testOutlineFactory();
    }

    @Test
    public void testInterfaceClassesAreAvailable() throws Exception {
        Class.forName("nl.ulso.sprox.XmlProcessorBuilderFactory");
    }

    @Test(expected = ClassNotFoundException.class)
    public void testImplementationClassesAreUnavailable() throws Exception {
        Class.forName("nl.ulso.sprox.impl.StaxBasedXmlProcessorBuilderFactory");
    }


    public static class NodeCounter {
        private int count = 0;

        @Node("root")
        public Integer totalCount() {
            return count;
        }

        @Node("node")
        public void countNode() {
            count++;
        }
    }
}
