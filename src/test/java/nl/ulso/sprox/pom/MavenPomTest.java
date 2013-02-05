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

package nl.ulso.sprox.pom;

import nl.ulso.sprox.XmlProcessor;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

import static nl.ulso.sprox.XmlProcessorFactory.createXmlProcessorBuilder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class MavenPomTest {

    @Test
    public void testProcessMavenPom() throws Exception {
        final XmlProcessor<Project> processor = createXmlProcessorBuilder(Project.class)
                .addControllerObject(new MavenPomProcessor())
                .buildXmlProcessor();
        final FileInputStream inputStream = new FileInputStream(new File("pom.xml"));
        final Project project = processor.execute(inputStream);
        assertNotNull(project);
        assertThat(project.getGroupId(), is("nl.ulso.sprox"));
        assertThat(project.getArtifactId(), is("sprox"));
        assertThat(project.getDependencies().get(0).getArtifactId(), is("junit"));

    }
}
