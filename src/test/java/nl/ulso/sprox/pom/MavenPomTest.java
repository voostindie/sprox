package nl.ulso.sprox.pom;

import nl.ulso.sprox.XmlProcessor;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

import static nl.ulso.sprox.SproxTests.createXmlProcessorBuilder;
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
        assertThat(project.getDependencies().get(0).getArtifactId(), is("org.osgi.core"));
    }
}
