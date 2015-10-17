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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CamelCaseToHyphensElementNameResolverTest {

    private CamelCaseToHyphensElementNameResolver resolver;

    @Before
    public void setUp() throws Exception {
        resolver = new CamelCaseToHyphensElementNameResolver();
    }

    @After
    public void tearDown() throws Exception {
        resolver = null;
    }

    @Test
    public void testFromParameter() throws Exception {
        final Class<TestClass> clazz = TestClass.class;
        final Method method = clazz.getMethod("camelCaseMethodName", String.class);
        final Parameter parameter = method.getParameters()[0];
        assertThat(resolver.fromParameter(clazz, method, parameter), is("camel-case-parameter-name"));
    }

    @Test
    public void testFromMethod() throws Exception {
        final Class<TestClass> clazz = TestClass.class;
        final Method method = clazz.getMethod("camelCaseMethodName", String.class);
        assertThat(resolver.fromMethod(clazz, method), is("camel-case-method-name"));
    }

    public static class TestClass {
        public void camelCaseMethodName(String CamelCaseParameterName) {
            // Nothing to do here.
        }
    }
}