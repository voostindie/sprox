/**
 * Package {@code nl.ulso.sprox.parsers} contains implementations of parsers for primitive types.
 * <p>
 * All parsers in this package are available by default in all XML processors. That means that it's possible to inject
 * any node or attribute as any primitive type.
 * </p>
 * <p>
 * By writing your own parsers and registering them with an {@link nl.ulso.sprox.XmlProcessorBuilder} you can
 * support additional types, or replace the default parsers.
 * </p>
 * <p>
 * Each parser implementation in this package may serve as a base class for your custom implementations.
 * </p>
 *
 * @see nl.ulso.sprox.XmlProcessorBuilder
 */
package nl.ulso.sprox.parsers;