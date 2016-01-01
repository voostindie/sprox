package nl.ulso.sprox.impl;

import nl.ulso.sprox.XmlProcessorBuilderFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * OSGi bundle activator; it registers the {@link StaxBasedXmlProcessorBuilderFactory} as a service of type
 * {@link XmlProcessorBuilderFactory}.
 */
public class SproxBundleActivator implements BundleActivator {

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        bundleContext.registerService(XmlProcessorBuilderFactory.class, new StaxBasedXmlProcessorBuilderFactory(), null);
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        // Nothing to do here.
    }
}
