package nl.ulso.sprox;

/**
 * Entry point into Sprox: to create a processor you need a builder and to create a builder, you need this factory.
 * <p>
 * Creating a processor to process XML streams into some type {@code T} consists of the following steps:
 * </p>
 * <ul>
 * <li>Obtaining an instance of this {@link XmlProcessorBuilderFactory}</li>
 * <li>Creating an {@link XmlProcessorBuilder} for the type {@code T}, using the factory</li>
 * <li>Configuring the builder, by adding controllers and parsers, the latter being optional</li>
 * <li>Creating an {@link XmlProcessor}.</li>
 * </ul>
 * <p>
 * Once an {@link XmlProcessor} has been created, it can be reused over and over again. Sprox itself is thread-safe.
 * Whether an {@link XmlProcessor} is thread-safe however depends on the controllers that you provide.
 * </p>
 * <p>
 * An implementation of this factory is accessible through the {@link java.util.ServiceLoader}.
 * </p>
 *
 * @see XmlProcessorBuilder
 * @see XmlProcessor
 */
public interface XmlProcessorBuilderFactory {
    <T> XmlProcessorBuilder<T> createXmlProcessorBuilder(Class<T> resultClass);
}
