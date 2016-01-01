package nl.ulso.sprox.impl;

import nl.ulso.sprox.ControllerFactory;

/**
 * Provides controllers by calling a factory.
 */
final class FactoryBasedControllerProvider implements ControllerProvider {
    private final ControllerFactory<?> factory;

    FactoryBasedControllerProvider(ControllerFactory<?> factory) {
        this.factory = factory;
    }

    @Override
    public Object getController() {
        return factory.createController();
    }
}
