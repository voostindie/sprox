# Sprox: Streaming Processor for XML

[![Build Status](https://travis-ci.org/voostindie/sprox.svg?branch=master)](https://travis-ci.org/voostindie/sprox)

## Introduction

Sprox is a small Java 7+ library (around 50 kB) with zero dependencies that provides a simple, annotation-based API for processing XML documents. Sprox can be used in a standalone environment as well as in an OSGi environment.

When you need to process an XML in Java, you basically have three types of libraries at your disposal:

* Document Object Model: W3C, JDOM, DOM4J, XOM and many others.
* Object binding: JAXB, XmlBeans and again many others.
* Low-level parsing: SAX, StAX.

After using each of these options many times in many projects, I found that there's room for a fourth.

XML is a complex beast. There's a lot you can do with it. That's one reason why all existing XML libraries are so complicated. Sprox limits itself to a subset. This allows for a small and simple API. You can use Sprox if:

* You can process the XML in one go. Sprox goes from front to back through the XML, exactly once.
* You need access only to nodes, attributes and/or node content. Sprox doesn't give you access to document preambles for example.
* You do not need to process [mixed content](http://www.w3.org/TR/REC-xml/#sec-mixed-content), like in XHTML for example. Sprox can process documents containing mixed content, as long as it valid XML, but it cannot process the mixed content itself.

In case you missed it: Sprox is one-way. From XML into your system. **Not** the other way around.

## Usage

Adding Sprox to a Maven project is easy. Just add the following dependency:

```xml
<dependency>
    <groupId>nl.ulso.sprox</groupId>
    <artifactId>sprox</artifactId>
    <version>3.1.0-SNAPSHOT</version>
</dependency>
```

This assumes that you use JDK 8+. On JDK 7, use the latest 2.x version.

Note that snapshot releases are not available in central repositories. You'll have to `git clone` and `mvn deploy` this repository yourself if you want to use the latest versions. See the list of tags for the available stable releases.

## Java versions

Version 2.x and 3.x of Sprox are **not** compatible. Version 3.x explicitly requires JDK 8. The API has been retrofitted to better fit the new features in Java 8. Notable differences between Sprox 2.x and 3.x are:

* Sprox 2.x uses a custom `@Nullable` annotation to denote optional parameters. In Sprox 3.x, Sprox uses the built-in `java.util.Optional` to achieve the same. `@Nullable` is no more.
* The Sprox 2.x methods `addParser` and `addControllerFactory` methods of the `XmlProcessorBuilder` interface silently do not work with synthetic types, like lambda's and method references. Sprox 3.x detects this and offers additional methods to support synthetic types.
* If a controller supports multiple namespaces, these had to be annotated with the `@Namespaces` annotation in Sprox 2.x. In Sprox 3.x this annotation needn't be used, as Java 8 supports repeatable annotations: just put as many `@Namespace` annotations on the class as needed.
* In Sprox 3.x, the `@Node`, `@Source` and `@Attribute` annotations have optional values, as names can be resolved from the corresponding parameters (or methods) automatically if they are the same. **For parameters this does require the `-parameters` compiler option to be used when compiling controller classes!**

## Tutorial

This tutorial uses Atom feeds as a running example. All code is delivered as working source code, in test cases. The goal of this tutorial is to introduce the Sprox API step by step. Once you've finished reading the tutorial, you've seen all of it.

### Counting entries

Let's say we have an Atom feed and we need to know how many entries there are. In this example, we'll use the [Google Webmaster Central Blog](http://googlewebmastercentral.blogspot.com). Their feed normally contains the last 25 blog entries. Let's verify that.

First, we need to implement a *controller* for Sprox's processor to use when going through the XML. Here it is:

```java
public class FeedEntryCounter {
    private int numberOfEntries;

    public int getNumberOfEntries() {
        return numberOfEntries;
    }

    @Node("entry")
    public void countEntry() {
        numberOfEntries++;
    }
}
```

There's nothing special about this class. It's just a POJO. The magic is at the `countEntry` method. It's annotated with `@Node("entry")`. This tells Sprox to call this method whenever it encounters that node in the feed. Here we just increment a counter. Once the feed is processed completely, the counter will be equal to the total number of entries in the feed.

> By the way: if the method `countEntry` was named `entry` instead, then the `@Node` annotation wouldn't need a value. Sprox would use the name of the method as the name of the node.

Now we need to use this controller with some XML. Here's a JUnit test method (from class `FeedEntryCounterTest`) that does just that:

```java
public void countAllEntriesInFeed() throws Exception {
    final FeedEntryCounter entryCounter = new FeedEntryCounter();
    final XmlProcessor<Void> processor = createXmlProcessorBuilder(Void.class)
            .addControllerObject(entryCounter)
            .buildXmlProcessor();
    processor.execute(
        getClass().getResourceAsStream("/google-webmaster-central-2013-02-01.xml"));
    assertThat(entryCounter.getNumberOfEntries(), is(25));
}
```

First we instantiate our controller. Then we build an `XmlProcessor` using this controller. Don't worry about the `Void` stuff in there yet; it'll be explained later. Once the processor is constructed, we execute it, in this case on a file. Finally we check that there are indeed 25 entries.

Pretty easy, right?

But there's an issue: the processor cannot be used concurrently, or even more than once, because our controller uses an instance variable to store its state and Sprox uses it as a singleton in the processor. Let's fix that.

### Returning a result

The `XmlProcessor` in the previous example uses `Void` as the generic type parameter. Now is the time to explain what that parameter does: it defines the result type of the processor. In our example, what we're really interested in is a number, an `Integer`. So that's what we'd like our processor to produce.

First, we need to make our controller a little smarter:

```java
public class BetterFeedEntryCounter {
    private int numberOfEntries;

    @Node("feed")
    public Integer getNumberOfEntries() {
        return numberOfEntries;
    }

    @Node("entry")
    public void countEntry() {
        numberOfEntries++;
    }
}
```

This class is almost the same as the previous one, except that the `getNumberOfEntries` is also annotated, with `@Node("feed")`. The method doesn't return an `int` any longer. It now returns an `Integer`. Any annotated method must return either `void` or a non-primitive type. It needs to be `public` as well.

Sprox calls your annotated methods not at the start of the node you're interested in, but at the end. Why it does that will be made clear later on. In this case it means that the `countEntry` method is called first - once for every entry - and only then the `getNumberOfEntries` method. So by the time the latter method is called, the `numberOfEntries` member variable will have been incremented 25 times.

Here's the code that uses our new controller:

```java
public void countAllEntriesInFeedWithResult() throws Exception {
    final XmlProcessor<Integer> processor = createXmlProcessorBuilder(Integer.class)
            .addControllerClass(BetterFeedEntryCounter.class)
            .buildXmlProcessor();
    final int numberOfEntries = processor.execute(
        getClass().getResourceAsStream("/google-webmaster-central-2013-02-01.xml"));
    assertThat(numberOfEntries, is(25));
}
```

Two things are different from the previous example:

1. The `XmlProcessor` is configured to return an `Integer`. Sprox is smart enough to guess that this matches the `getNumberOfEntries` method in the controller. It will use the result from that method as the result of the processor execution.
2. Instead of creating the processor with an instance of our controller, we reference the controller class. Sprox will instantiate it for us. It does so exactly once in each execution. The controller needs to have a constructor with zero arguments of course.

The processor created in this example can safely be used concurrently. All state that is built up in the controller is bound to a single execution. Additionally, the code using the processor doesn't need to know anything about the controller used within the processor. The processor itself collects the result and returns it.

This is mighty nice, but if this would be all that Sprox could do, it would still be pretty useless. Time to make it a bit more interesting!

### Counting entries with a filter

Let's say we still want to count entries, but only the entries published in 2013. Every entry has a node `published`, containing the publication date.

Here's one way to access that data and use it: (Don't worry, we'll replace it with a better one later.)

```java
public class FeedEntryFrom2013Counter {
    private int numberOfEntries;

    @Node("feed")
    public Integer getNumberOfEntries() {
        return numberOfEntries;
    }

    @Node("entry")
    public void countEntry(@Node("published") String publicationDate) {
        if (Integer.parseInt(publicationDate.substring(0, 4)) == 2013) {
            numberOfEntries++;
        }
    }
}
```

Straightforward, right? By adding parameters and annotating them with `@Node("nodeName")`, Sprox injects the contents of those nodes in the controller method.

For parameters annotated with `@Node("nodeName")`, the following rules apply:

* The node you're referring to must be either the node from the controller method itself, or a node *inside* that node.
* If there are multiple nodes with the same name found in the XML, then you'll get the content from the *closest* node. That is:
    * In a hierarchy of nodes, the node whose depth is closest to the node the controller method is annotated with.
    * In a list of nodes with the same name, all on the same depth, the first node.
* If the node you want the content of contains something other than characters, the node is ignored. Only nodes with character data or CDATA  are allowed.

### Other method parameters

The previous section introduced a parameter annotated with `@Node` to inject node content into a controller method. You might now guess that it's also possible to inject an attribute value, with `@Attribute`. That guess would be completely correct: add a parameter, annotate it with `@Attribute("attributeName")` and Sprox will automatically hand the value of that attribute over to your method.

But there's more.

As mentioned briefly earlier, controller methods can return values. Sprox collects these values, for you to inject elsewhere. To be more precise: if there's a controller method that produces values of type `T`, then you can write a second controller method that accepts a parameter of type `T`, or of `List<T>`.

Note that the order in which methods are called depends on the structure of the XML, not on the definition of your methods. Sprox does not build a dependency tree of your methods, it just processes XML events in the order found in the input. So if you define an object or list parameter in a controller method, make sure that the method that produces those objects comes first!

Some more rules that apply:

* If a controller method was invoked by Sprox many times but you inject only a single value elsewhere, you'll get the value that was generated first. All other values are simply discarded.
* If a controller method returns `null` instead of a value, Sprox will ignore it.
* If a controller method produces data that you never inject anywhere, then Sprox will still collect it. At the end of the processing run the data is simply discarded. If you care about memory usage, don't create data you don't need.

Parameter injection is why Sprox calls your methods at the end of the node you annotated the controller method with and not at the beginning. It has to collect the data for the parameters first.

This explanation might look a little complicated now. It really isn't. You'll find that Sprox simply does what you expect it to. Except when it doesn't. Then you'll need to read this again.

### Mapping methods and parameters to XML elements

Sprox tries to support the [convention over configuration](http://softwareengineering.vazexqi.com/files/pattern.html) pattern as much as possible. This applies to the mapping of methods and parameters to XML elements especially.

All annotations - `@Node` and `@Attribute`, as well as the `@Source` annotation you'll be introduced to later - have an *optional* value. If you omit the value, Sprox will pull the name of the XML element (node or attribute) from the method or parameter name.

**Beware!** This works for parameters *only* if you have set the `-parameters` option on the Java compiler when compiling your controller classes. If you don't do that, the names of parameters are not retained in the class files, and therefore Sprox cannot access them.

Sometimes it's not possible to name your methods and/or parameters to XML elements, for example because the XML elements contain hyphens, start with upper case characters, or are just plain ugly. Or maybe you prefer better names on your methods, e.g. `processElement(...)` instead of `element(...)`. In these cases you still don't need to define the element names as the annotation values. Instead you can provide a custom `ElementNameResolver` that implements some algorithm to translate method and parameter names into XML element names. You don't need to worry too much about this algorithm being expensive (in terms of CPU, time or memory). Sprox precomputes all mappings while inspecting the controller classes, when the processor is built. Resolvers are not used during XML processing.

### Optional parameters

By default, Sprox assumes that all data it needs to inject is required. That means that if any of the parameters is not available, Sprox will not call your method at all. You'll never get `null` injected.

This is not always what you want. In such cases you can instruct Sprox to inject empty values. You do that by wrapping the optional parameters in a `java.util.Optional`.

Let's say the `published` node is optional (which it isn't) and we assume that every entry without a publication date was published in 2013. Then we'd have to implement our controller as follows:

```java
public class FeedEntryFrom2013CounterWithOptionalPublicationDate {
    private int numberOfEntries;

    @Node("feed")
    public Integer getNumberOfEntries() {
        return numberOfEntries;
    }

    @Node("entry")
    public void countEntry(@Node("published") Optional<String> publicationDate) {
        if (!publicationData.isPresent()) {
            numberOfEntries++;
        } else {
            if (Integer.parseInt(publicationDate.get().substring(0, 4)) == 2013) {
                numberOfEntries++;
            }
        }
    }
}
```

If entries without a publication date shouldn't count for 2013, then we could just use the controller from the previous example: without the `Optional` wrapper, Sprox would skip all nodes that don't have a publication date.

### Custom parameter types

In the example above, the publication date that is injected into the controller method is a `String`. Couldn't it be a `DateTime` (from [Joda-Time](http://joda-time.sourceforge.net))? Yes it can! Any parameter annotated with `@Node` or `@Attribute` can have any type you want. Sprox might not know how to convert the data from the XML (a `String`) to that type, but that's something you can teach it to. You do that by creating a custom `Parser`.

Here's a parser for dates in Atom feeds:

```java
public class DateTimeParser implements Parser<DateTime> {
    @Override
    public DateTime fromString(String value) throws ParseException {
        try {
            return DateTime.parse(value);
        } catch (IllegalArgumentException e) {
            throw new ParseException(DateTime.class, value, e);
        }
    }
}
```

Provided that Sprox knows about this parser, we can now implement our controller like this:

```java
public class BetterFeedEntryFrom2013Counter {
    private static final DateTime JANUARY_1ST_2013 = DateTime.parse("2013-01-01");
    private int numberOfEntries;

    @Node("feed")
    public Integer getNumberOfEntries() {
        return numberOfEntries;
    }

    @Node("entry")
    public void countEntry(@Node("published") DateTime publicationDate) {
        if (publicationDate.isAfter(JANUARY_1ST_2013)) {
            numberOfEntries++;
        }
    }
}
```

Much cleaner! Controllers are defined with the types that you choose. Nowhere do you need to depend on an XML- or Sprox-specific type. The only thing that Sprox adds are annotations. No leaky abstractions.

So how do we teach Sprox to use our custom parser? By passing it to the `XmlProcessorBuilder`. Here's a test method that does that:

```java
public void countOnlyEntriesPublishedIn2013InFeedWithCustomParser() throws Exception {
    final XmlProcessor<Integer> processor = createXmlProcessorBuilder(Integer.class)
            .addControllerClass(BetterFeedEntryFrom2013Counter.class)
            .addParser(new DateTimeParser())
            .buildXmlProcessor();
    final int numberOfEntries = processor.execute(
            getClass().getResourceAsStream("/google-webmaster-central-2013-02-01.xml"));
    assertThat(numberOfEntries, is(1));
}
```

By default Sprox is able to parse XML content as each of the Java primitive types. If these parsers don't do what you want them to, you can replace them with your own.

### Example: reading a feed into memory

Now that we got introduced to most of the features of Sprox, it's time to give a more useful example. Let's say we want to build an in-memory representation of an Atom feed, using our own (immutable) domain model:

* A **feed** has an author and holds a list of entries.
* An **author** has a name, an email address, and a uri.
* An **entry** has an author, an ID, title, subtitle, publication date and the actual text.
* A piece of **text** has a type - it's either plaintext or (X)HTML) - and the content itself.

It's important to note that we're only modeling the data that we're interested in. An Atom feed contains a lot more data. With Sprox, you never need to see that data; you can just act as if it doesn't exist. Your codebase isn't polluted as it would be if you were generating code from XSD's.

To build our model from an XML input source, all we need is the following controller. Note that all methods and parameters are named according to the XML element they refer to, so that the values of all annotations can be omitted.

```java
public class FeedFactory {
    @Node
    public Feed feed(@Source Text title, @Source Text subtitle,
                     Author author, List<Entry> entries) {
        return new Feed(title, subtitle, author, entries);
    }

    @Node
    public Author author(@Node String name, @Node String uri, @Node String email) {
        return new Author(name, uri, email);
    }

    @Node
    public Entry entry(@Node String id, @Node DateTime published, @Source Text title,
                       @Source Text content, Optional<Author> author) {
        return new Entry(id, published, title, content, author);
    }

    @Node
    public Text title(@Attribute Optional<TextType> type, @Node String title) {
        return createText(type, title);
    }

    @Node
    public Text subtitle(@Attribute Optional<TextType> type, @Node String subtitle) {
        return createText(type, subtitle);
    }

    @Node
    public Text content(@Attribute Optional<TextType> type, @Node String content) {
        return createText(type, content);
    }

    private Text createText(Optional<TextType> textType, String content) {
        return textType.map(type -> {
            switch (type) {
                case TEXT:
                    return new SimpleText(content);
                case HTML:
                    return new HtmlText(content);
                default:
                    // XHTML is not supported
                    throw new IllegalArgumentException(
                        "Unsupported text type: " + textType);
            }
        }).orElse(new SimpleText(content));
    }
}
```

This code pretty much speaks for itself, doesn't it?

To be able to read feeds into memory, we set up a processor and use it, like this:

```java
final XmlProcessor<Feed> processor = createXmlProcessorBuilder(Feed.class)
        .addControllerClass(FeedBuilder.class)
        .addParser(new DateTimeParser())
        .addParser(new TextTypeParser())
        .buildXmlProcessor();
final Feed feed = processor.execute(
    getClass().getResourceAsStream("/google-webmaster-central-2013-02-01.xml"));
```

The astute reader will have noticed the sneaky introduction of another Sprox feature: the `@Source` annotation. The following section explains what it does.

### Overlapping method result types

Sometimes controller methods for different nodes produce the same result type. In the example above, that's true for the controller methods for the nodes `title`, `subtitle` and `content`. All methods return objects of type `Text`. How can you collect and correctly inject those? Up to now, you would have to collect them as a `List<Text>`. That could work, provided that the nodes are always present in the XML and in the same order.

That feels awkward.

Enter the `@Source` annotation. With this annotation you can refer back to the node the output of which you're interested in. You can put it on object and list parameters. The method will then receive values generated from the controller method for that node only.

Both the `title` method and the `subtitle` method return an object of type `Text`. Both are needed in the `feed` method. By declaring two separate parameters of type `Text` and annotating them with `@Source` we are able to get the right values injected.

### Namespace support

We've been completely ignoring XML namespaces thus far. Therefore so did Sprox. By default Sprox processes all elements in the default namespace of XML documents. That's fine in many cases. When it isn't, you can enable support for namespaces.

If all XML elements processed by a controller belong to the same namespace, declare it on the controller with a `@Namespace` annotation. For example:

```java
@Namespace("http://www.w3.org/2005/Atom")
public class FeedFactory {
    ...
}
```

This declaration ensures that `FeedFactory` processes only XML elements in the `http://www.w3.org/2005/Atom` namespace, even if that namespace is not the default.

The Google Webmaster Central Blog uses multiple namespaces. For example it uses a namespace `http://schemas.google.com/g/2005` to refer to images for authors. What if we would like to add these images to our domain model? After adding a class `Image`, we can do this:

```java
@Namespace("http://www.w3.org/2005/Atom")
@Namespace(shorthand = "g", value = "http://schemas.google.com/g/2005")
public class FeedFactory {
    ...
    @Node
    public Author author(@Node String name, @Node String uri, @Node String email, Image image) {
        return new Author(name, uri, email, image);
    }
    ...
    @Node("g:image")
    public Image image(@Attribute String src, @Attribute Integer width, @Attribute Integer height) {
        return new Image(src, width, height);
    }
}
```

The `FeedFactory` now declares it processes two namespaces, the first being the default. The new method `image` triggers on the node `image` belonging to a different namespace. The rest of the class remains the same.

Note that the shorthand for the namespace in the code - `g:` - has absolutely nothing to do with namespace prefixes in the XML itself. They don't need to match. Namespace shorthands just look a lot like namespace prefixes because it is convenient.

Here are the rules regarding namespaces:

* If your controller supports a single namespace and you want to strictly process it, declare it in a `@Namespace` annotation.
* If your controller supports multiple namespaces, declare each of them in a separate `@Namespace` annotation.
* Every namespace except the first requires a shorthand. The first namespace is the default. There's no need to use shorthands for that namespace anywhere.
* If a `@Node` annotation on a method doesn't include a shorthand, Sprox uses the default namespace set on the class.
* If a `@Node`, `@Attribute` or `@Source` annotation on a parameter doesn't include a shorthand, Sprox uses the namespace set on the method.

Again this might look a bit complicated. Again it isn't. Sprox aims to do exactly what you expect it to.

### Recursion

Some XML structures are recursive in nature. [OPML](http://dev.opml.org/spec2.html), for [example](http://hosting.opml.org/dave/spec/states.opml).  By default, Sprox doesn't understand recursive structures. Defining a controller that matches the OPML `outline` node will just match the topmost node, and no more.

Given an `Outline` class that holds a list of `Element`s, with each `Element` in turn holding a list of `Element`s (and so on), the following controller will create an `Outline`, containing all elements in an OPML file:

```java
public class OutlineFactory {

    @Node
    public Outline opml(@Node String title, @Node DateTime dateCreated,
                        @Node DateTime dateModified, List<Element> elements) {
        return new Outline(title, dateCreated, dateModified, elements);
    }

    @Recursive
    @Node
    public Element outline(@Attribute String text, Optional<List<Element>> elements) {

        return new Element(text, elements);
    }
}
```

Just two methods... The magic happens in the `outline` method that is annotated with `@Recursive`.

As you can see in the example, `@Recursive` and `Optional` typically go hand in hand: when creating an `Element`, the list of `Element`s below it is required. Recursively. The leaf elements don't have any children, so their list can be `Optional`.

Because handling recursive structures is a bit tricky and a some additional overhead is involved, you have to explicitly enable it in Sprox.

### Exception handling

The controllers in all the examples up to now had no exceptional cases. Either they are called by Sprox and work correctly, or they aren't called at all. What if your controllers do have exceptional cases? Then you can choose:

* You can throw any unchecked exception, without declaring it naturally. This unchecked exception is then the one that the `execute` method of the `XmlProcessor` will throw.
* You can throw a checked exception declared on the controller. This exception is then wrapped in an `XmlProcessorException` by Sprox, which is also checked.

Remember Item 58 of Effective Java (2nd Edition): "Use checked exceptions for recoverable conditions and runtime exceptions for programming errors". That's what Sprox itself does as well.

## Notes

### How to create an XmlProcessorBuilder

The sample code in the tutorial consistently used a static method `createXmlProcessorBuilder` to create an `XmlProcessorBuilder`. The tutorial is cheating a little.

The static method `createXmlProcessorBuilder` is defined in utility class `SproxTests`, which is of course only available in JUnit tests (where all tutorial code is taken from).

Here is the method implementation:

```java
public static <T> XmlProcessorBuilder<T> createXmlProcessorBuilder(Class<T> resultClass) {
    return new StaxBasedXmlProcessorBuilderFactory().createXmlProcessorBuilder(resultClass);
}
```

So apparently you need a `StaxBasedXmlProcessorBuilderFactory`? No, you don't.

In an OSGi environment, you can deploy Sprox as a bundle, after which a service of type `XmlProcessorBuilderFactory` is available.

In a non-OSGi environment, the preferred way of obtaining the factory is through the JDK's `java.util.ServiceLoader` mechanism, like so:

```java
ServiceLoader.load(XmlProcessorBuilderFactory.class).iterator().next();
```

### Controller factories

In the tutorial, controllers are either registered with a builder as singleton objects, or as classes instantiated by Sprox. There's a third option: using a `ControllerFactory`. Controller factories provide you with the hook to create new instances of controllers for your processors that depend on objects outside of Sprox.

### Controller method ordering

Controller methods are not ordered. Sprox uses Java's reflection API internally to discover and inspect the annotated controller methods. In that API the order of the methods in a class is unspecified. You cannot depend on one controller method having preference simply because it's above all others in your code!

If you do need some kind of ordering, the way to achieve that is to put your controller methods in different classes and then add these controllers to an `XmlProcessorBuilder` in the right order, highest priority first.

Cases where you might need this are probably rare. For a far-fetched example, see class `NodeAttributeTest` in the test cases.

### Memory usage

Sprox itself uses a fixed amount of memory. When creating a processor, it creates an internal model from your controllers. The more complex your controllers, the more memory is needed. Still, it's not much, and it's constant. It's independent of the size of the XML being processed.

The amount of memory used during a processing run depends on your controllers. The more results you produce and the more parameters you inject, the more data Sprox needs to collect. So it's basically up to you!

Truth be told, Sprox hasn't been subjected to intensive load and stress testing yet. That's on the wish list.

### Security

Sprox internally uses a StAX parser. You can provide one yourself, but that's entirely optional. If you don't provide one, Sprox uses a parser provided by the platform. It configures this parser as securely as possible. DTDs are not supported, internal entity references are not replaced and external entity references are disabled. That means Sprox won't go out behind your back reading files from the local filesystem or downloading resources from the internet. Nor is Sprox susceptible to the [Billion Laughs](http://en.wikipedia.org/wiki/Billion_laughs) attack.

Unlike some other XML processors, Sprox is indifferent to attacks using deeply nested nodes. That's because Sprox doesn't actually build up a stack of any kind. There's no recursion in the control flow, nor are there internal data structures that reflect the hierarchy of the XML being processed.

Of course it is possible to make the JVM go out of memory. If you process a big XML and your controllers collect all data in it, that will certainly happen.

Sprox does not protect you from flaws in underlying libraries. If the StAX parser you use (probably the platform default) is insecure, then Sprox most likely is as well.

### On generating XML

Sprox is one-way. You can use it only to pull data from XML, not for generating XML. That's a feature, not a limitation.

Libraries that go both ways are inherently complex. They always will be. There's no way to unify two completely different technologies in one easy-to-use API. Any which way that API cannot be a perfect fit. Requirements from the one will always bleed over to the other. Try [reading data into an immutable object using JAXB](http://stackoverflow.com/questions/11030805/creating-immutable-objects-usingjaxb), for example.

This is, of course, an opinion. Not a fact.

So what's the best way to generate XML, if DOM and object binding are so awful? Well, try a template engine like [StringTemplate](http://www.stringtemplate.org) or [FreeMarker](http://freemarker.sourceforge.net). That will give you much more flexibility and speed while being much less hungry for memory.
