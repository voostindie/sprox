package nl.ulso.sprox;

import org.junit.Test;

import java.util.StringJoiner;

import static nl.ulso.sprox.SproxTests.testControllers;

public class NodeAttributesFromParameterNamesTest {

    @Test
    public void testNodeAttributesFromParameterNames() throws Exception {
        testControllers(
                "data2:data1",
                "<root value1=\"data1\" value2=\"data2\"/>",
                new NodesAttributesFromParametersProcessor()
        );

    }

    public static class NodesAttributesFromParametersProcessor {
        @Node("root")
        public String getAttribute(@Attribute String value1, @Attribute String value2) {
            return new StringJoiner(":").add(value2).add(value1).toString();
        }
    }
}
