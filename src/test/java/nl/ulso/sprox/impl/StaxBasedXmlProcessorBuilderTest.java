package nl.ulso.sprox.impl;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StaxBasedXmlProcessorBuilderTest {
    @Test
    public void testSizeOfInternalTableInHashMap() throws Exception {
        final Field mapField = StaxBasedXmlProcessorBuilder.class.getDeclaredField("DEFAULT_PARSERS");
        mapField.setAccessible(true);
        final HashMap map = (HashMap) mapField.get(null);
        assertThat(map.size(), is(9));
        final Field tableField = map.getClass().getDeclaredField("table");
        tableField.setAccessible(true);
        Object[] table = (Object[]) tableField.get(map);
        // The size of the internal array is always a power of 2. So 9 items require an array of length 16.
        assertThat(table.length, is(16));
    }
}
