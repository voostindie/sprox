package nl.ulso.sprox.impl;

/**
 * Provides controller objects. The {@link StaxBasedXmlProcessor} keeps a list of providers. Right before processing
 * an XML, it accesses each provider once to acquire a controller for that run. Those controllers are then stored in
 * the {@link ExecutionContext}, storing them for that run only.
 */
interface ControllerProvider {
    Object getController();
}
