package nl.ulso.sprox;

import org.junit.Test;

import java.util.List;

import static java.lang.Double.compare;
import static java.lang.Double.doubleToLongBits;
import static java.lang.Float.floatToIntBits;
import static java.lang.Integer.parseInt;
import static java.util.Collections.reverse;
import static nl.ulso.sprox.NodeAttributeTest.Primitives.PrimitiveInjectionProcessor;
import static nl.ulso.sprox.SproxTests.testControllers;

public class NodeAttributeTest {

    @Test
    public void testThatControllersAreProcessedInOrder() throws Exception {
        testControllers("[1, 2, 3, 4]",
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
        final Primitives primitives = new Primitives(299792458, (short) 42, 42l, 2.72f, 3.14, (byte) 16, 'V');
        testControllers(primitives, "<root i=\"299792458\" s=\"42\" l=\"42\" f=\"2.72\" d=\"3.14\" b=\"16\" c=\"V\"/>",
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
            return parseInt(integer);
        }

    }

    public static final class NodeLevel2AttributeProcessor {
        @Node("node")
        public Integer level2Node(@Attribute("i") String integer) {
            return parseInt(integer);
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
        final long l;
        final float f;
        final double d;
        private final byte b;
        private final char c;

        public Primitives(int i, short s, long l, float f, double d, byte b, char c) {
            this.i = i;
            this.s = s;
            this.l = l;
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
                    ", l=" + l +
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
            return b == that.b && c == that.c && compare(that.d, d) == 0 && Float.compare(that.f, f) == 0
                    && i == that.i && l == that.l && s == that.s;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = i;
            result = 31 * result + (int) s;
            result = 31 * result + (int) (l ^ (l >>> 32));
            result = 31 * result + (f != +0.0f ? floatToIntBits(f) : 0);
            temp = d != +0.0d ? doubleToLongBits(d) : 0L;
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            result = 31 * result + (int) b;
            result = 31 * result + (int) c;
            return result;
        }

        public static final class PrimitiveInjectionProcessor {
            @Node("root")
            public Primitives mapPrimitives(@Attribute("i") int i, @Attribute("s") short s, @Attribute("l") long l,
                                            @Attribute("f") float f, @Attribute("d") double d, @Attribute("b") byte b,
                                            @Attribute("c") char c) {
                return new Primitives(i, s, l, f, d, b, c);
            }
        }
    }
}
