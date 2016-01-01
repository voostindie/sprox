package nl.ulso.sprox.impl;

/**
 * Provides controllers by returning a singleton object.
 */
final class SingletonControllerProvider implements ControllerProvider {
    private final Object singleton;

    SingletonControllerProvider(Object singleton) {
        this.singleton = singleton;
    }

    @Override
    public Object getController() {
        return singleton;
    }
}
