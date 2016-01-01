package nl.ulso.sprox;

import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static nl.ulso.sprox.SproxTests.testControllers;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class ObjectInjectionTest {
    @Test
    public void testThatInjectingASingleValueInjectsTheFirstFromTheList() throws Exception {
        testControllers("value1", "<root1><node1>value1</node1><node1>value2</node1></root1>", ObjectInjector.class);
    }

    @Test
    public void testThatSourcedParameterFirstGetsItsValue() throws Exception {
        testControllers("node1", "<root2><node1>node1</node1><node2>node2</node2></root2>", ObjectInjector.class);
    }

    @Test
    public void testThatSourceParameterListLosesItsValue() throws Exception {
        testControllers("", "<root3><node1>node1</node1><node2>node2</node2></root3>", ObjectInjector.class);
    }

    public static final class ObjectInjector {
        @Node("root1")
        public String root1(String node) {
            return node;
        }

        @Node("root2")
        public String root2(@Source("node1") String node1, List<String> otherNodes) {
            assertThat(otherNodes.size(), is(1));
            assertThat(otherNodes.get(0), is("node2"));
            return node1;
        }

        @Node("root3")
        public String root3(List<String> otherNodes, @Source("node1") Optional<String> node1) {
            assertThat(otherNodes.size(), is(2));
            assertThat(otherNodes.get(0), is("node1"));
            assertThat(otherNodes.get(1), is("node2"));
            assertFalse(node1.isPresent());
            return "";
        }

        @Node("node1")
        public String node1(@Node("node1") String value) {
            return value;
        }

        @Node("node2")
        public String node2(@Node("node2") String value) {
            return value;
        }
    }
}
