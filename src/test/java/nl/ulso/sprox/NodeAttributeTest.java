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

import org.junit.Test;

import java.util.List;

import static java.util.Collections.reverse;

public class NodeAttributeTest {

    @Test
    public void testThatControllersAreProcessedInOrder() throws Exception {
        SproxTests.testControllers("[1, 2, 3, 4]",
                "<root><node i=\"1\"><node i=\"2\"><node i=\"3\"><node i=\"4\"></node></node></node></node></root>",
                new NestedNodeAttributeProcessor(),
                new NodeLevel1AttributeProcessor(),
                new NodeLevel2AttributeProcessor(),
                new NodeLevel3AttributeProcessor(),
                new NodeLevel4AttributeProcessor()
        );
    }

    @Test
    public void testMappingForPrimitivesInAttributes() throws Exception {
        final Primitives primitives = new Primitives(299792458, (short) 42, 2.72f, 3.14, (byte) 16, 'V');
        SproxTests.testControllers(primitives, "<root i=\"299792458\" s=\"42\" f=\"2.72\" d=\"3.14\" b=\"16\" c=\"V\"/>",
                new PrimitiveInjectionProcessor());
    }

    public static final class NestedNodeAttributeProcessor {
        @Node("root")
        public String getNestedContent(List<Integer> numbers) {
            reverse(numbers);
            return numbers.toString();
        }
    }

    public static final class NodeLevel1AttributeProcessor {
        @Node("node")
        public Integer level1Node(@Attribute("i") String integer) {
            return Integer.parseInt(integer);
        }

    }

    public static final class NodeLevel2AttributeProcessor {
        @Node("node")
        public Integer level2Node(@Attribute("i") String integer) {
            return Integer.parseInt(integer);
        }
    }

    public static final class NodeLevel3AttributeProcessor {
        @Node("node")
        public Integer level3Node(@Attribute("i") int integer) {
            return integer;
        }
    }

    public static final class NodeLevel4AttributeProcessor {
        @Node("node")
        public Integer level4Node(@Attribute("i") Integer integer) {
            return integer;
        }
    }

    public static final class Primitives {
        final int i;
        final short s;
        final float f;
        final double d;
        private final byte b;
        private final char c;

        public Primitives(int i, short s, float f, double d, byte b, char c) {
            this.i = i;
            this.s = s;
            this.f = f;
            this.d = d;
            this.b = b;
            this.c = c;
        }

        @Override
        public String toString() {
            return "Primitives{" +
                    "i=" + i +
                    ", s=" + s +
                    ", f=" + f +
                    ", d=" + d +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Primitives that = (Primitives) o;
            return b == that.b && c == that.c && Double.compare(that.d, d) == 0 && Float.compare(that.f, f) == 0
                    && i == that.i && s == that.s;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = i;
            result = 31 * result + (int) s;
            result = 31 * result + (f != +0.0f ? Float.floatToIntBits(f) : 0);
            temp = d != +0.0d ? Double.doubleToLongBits(d) : 0L;
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            result = 31 * result + (int) b;
            result = 31 * result + (int) c;
            return result;
        }
    }

    public static final class PrimitiveInjectionProcessor {
        @Node("root")
        public Primitives mapPrimitives(@Attribute("i") int i, @Attribute("s") short s, @Attribute("f") float f,
                                        @Attribute("d") double d, @Attribute("b") byte b, @Attribute("c") char c) {
            return new Primitives(i, s, f, d, b, c);
        }
    }
}
