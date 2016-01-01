package nl.ulso.sprox;

/**
 * Factory for controllers. Use these if controller objects and controller classes are not sufficient.
 *
 * @see XmlProcessorBuilder
 */
public interface ControllerFactory<T> {

    /**
     * Creates a controller of type {@code T}; called exactly once per processor execution.
     *
     * @return The controller to use.
     */
    T createController();
}
