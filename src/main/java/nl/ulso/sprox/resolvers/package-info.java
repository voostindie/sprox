/**
 * Package {@code nl.ulso.sprox.resolvers} contains implementations of resolvers that resolve references
 * to XML nodes and attributes from Java method and parameter names.
 * <p>
 * By default uses the {@link nl.ulso.sprox.resolvers.DefaultElementNameResolver}, but you can set a custom
 * one, even per controller your setting up in a builder:
 * <pre><code>
 * // ...
 * builder
 *     .setElementNameResolver(new MyFirstCustomResolver())
 *     .addControllerClass(FirstController.class)
 *     .setElementNameResolver(new MySecondCustomResolver())
 *     .addControllerClass(SecondController.class)
 *     .resetElementNameResolver()
 *     .addControllerClass(DefaultController.class);
 * </code></pre>
 *
 * @see nl.ulso.sprox.XmlProcessorBuilder
 */
package nl.ulso.sprox.resolvers;