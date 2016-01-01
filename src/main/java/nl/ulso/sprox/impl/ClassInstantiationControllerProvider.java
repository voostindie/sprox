package nl.ulso.sprox.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Provides controllers by instantiating them with reflection.
 */
final class ClassInstantiationControllerProvider implements ControllerProvider {
    private final Class<?> instanceClass;
    private final Constructor constructor;

    ClassInstantiationControllerProvider(Class<?> instanceClass) {
        this.instanceClass = instanceClass;
        try {
            this.constructor = instanceClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(
                    "Controller class doesn't have a no-arg constructor: " + instanceClass.getName(), e);
        }
    }

    @Override
    public Object getController() {
        try {
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Couldn't instantiate controller class: " + instanceClass.getName(), e);
        }
    }
}
