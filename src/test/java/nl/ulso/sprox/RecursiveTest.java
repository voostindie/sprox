package nl.ulso.sprox;

import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static nl.ulso.sprox.SproxTests.testControllers;

public class RecursiveTest {
    @Test
    public void testThatRecursiveNodeIsProcessed() throws Exception {
        testControllers(3, "<root><tag><tag><tag></tag></tag></tag></root>", new RecursiveNodeProcessor());
    }

    @Test
    public void testRecursionWithNodeContent() throws Exception {
        final TreeNode tree = new TreeNode("level-1",
                asList(new TreeNode("level-2",
                        asList(new TreeNode("level-3", null))))
        );
        testControllers(tree,
                "<node><title>level-1</title><node><node><title>level-3</title></node><title>level-2</title></node></node>",
                new RecursiveNodeWithContentProcessor());
    }

    public static final class RecursiveNodeProcessor {
        private int level = 0;

        @Node("root")
        public Integer root() {
            return level;
        }

        @Recursive
        @Node("tag")
        public void tag() {
            level++;
        }
    }

    public static final class RecursiveNodeWithContentProcessor {

        @Recursive
        @Node("node")
        public TreeNode createNode(@Node("title") String title, Optional<List<TreeNode>> children) {
            return new TreeNode(title, children.orElse(null));
        }
    }

    public static final class TreeNode {
        private final String title;
        private final List<TreeNode> children;

        public TreeNode(String title, List<TreeNode> children) {
            this.title = title;
            this.children = children;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TreeNode node = (TreeNode) o;
            return !(children != null ? !children.equals(node.children) : node.children != null) && title.equals(node.title);
        }

        @Override
        public int hashCode() {
            int result = title.hashCode();
            result = 31 * result + (children != null ? children.hashCode() : 0);
            return result;
        }
    }

}
