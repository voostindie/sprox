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

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ReflectionUtilTest {
    @Test
    public void testSizeOfInternalTableInHashMap() throws Exception {
        final Field mapField = ReflectionUtil.class.getDeclaredField("PRIMITIVE_PARAMETER_TYPES");
        mapField.setAccessible(true);
        final HashMap map = (HashMap) mapField.get(null);
        assertThat(map.size(), is(8));
        final Field tableField = map.getClass().getDeclaredField("table");
        tableField.setAccessible(true);
        Object[] table = (Object[]) tableField.get(map);
        // The size of the internal array is always a power of 2. 8 items fit precisely fit in an array of length 2^3.
        assertThat(table.length, is(8));
    }
}
