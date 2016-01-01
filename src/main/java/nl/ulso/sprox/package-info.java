/**
 * Package {@code nl.ulso.sprox} defines the public API of the Streaming Processor for XML (Sprox). Clients depend
 * only on types in this package.
 * <p>
 * Sprox allows you to process XML documents efficiently in one pass. Without having to resort to low-level XML parsers
 * like SAX or StAX. Without having to load complete XML documents in document object models like
 * {@link org.w3c.dom.Document}, DOM4J, JDOM or XOM. Without having to generate code from XSDs and consume lots of
 * CPU by using an XML marshaller like JAXB or XmlBeans.
 * </p>
 * <p>
 * Sprox implements the nitty-gritty of parsing XML efficiently, calling high-level code you specify where necessary.
 * You focus on functionality.
 * </p>
 * <p>
 * A basic design principle of the API is that no method ever returns `null`, and no parameter may ever be `null`.
 * </p>
 *
 * @see nl.ulso.sprox.XmlProcessorBuilderFactory
 */
package nl.ulso.sprox;