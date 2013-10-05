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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static nl.ulso.sprox.SproxTests.testControllers;

public class RecursiveTest {
    @Test
    public void testThatRecursiveNodeIsProcessed() throws Exception {
        testControllers(3, "<root><tag><tag><tag></tag></tag></tag></root>", new RecursiveNodeProcessor());
    }

    @Test
    public void testRecursionWithNodeContent() throws Exception {
        final TreeNode tree = new TreeNode("level-1",
                Arrays.asList(new TreeNode("level-2",
                        Arrays.asList(new TreeNode("level-3", null))))
        );
        testControllers(tree, "<node><title>level-1</title><node><node><title>level-3</title></node><title>level-2</title></node></node>",
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
        public TreeNode createNode(@Node("title") String title, @Nullable List<TreeNode> children) {
            System.out.println(title);
            return new TreeNode(title, children);
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
            if (children != null ? !children.equals(node.children) : node.children != null) return false;
            if (!title.equals(node.title)) return false;
            return true;
        }

        @Override
        public int hashCode() {
            int result = title.hashCode();
            result = 31 * result + (children != null ? children.hashCode() : 0);
            return result;
        }
    }

}
