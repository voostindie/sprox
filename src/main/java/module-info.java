module nl.ulso.sprox {
    exports nl.ulso.sprox;
    exports nl.ulso.sprox.parsers;
    exports nl.ulso.sprox.resolvers;

    provides nl.ulso.sprox.XmlProcessorBuilderFactory
            with nl.ulso.sprox.impl.StaxBasedXmlProcessorBuilderFactory;
    
    requires java.xml;
}