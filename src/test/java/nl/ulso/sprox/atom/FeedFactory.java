package nl.ulso.sprox.atom;

import nl.ulso.sprox.Attribute;
import nl.ulso.sprox.Namespace;
import nl.ulso.sprox.Node;
import nl.ulso.sprox.Source;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Optional;

@Namespace("http://www.w3.org/2005/Atom")
@Namespace(shorthand = "g", value = "http://schemas.google.com/g/2005")
public class FeedFactory {
    @Node
    public Feed feed(@Source Text title, @Source Text subtitle,
                     Author author, List<Entry> entries) {
        return new Feed(title, subtitle, author, entries);
    }

    @Node
    public Author author(@Node String name, @Node String uri, @Node String email, Image image) {
        return new Author(name, uri, email, image);
    }

    @Node
    public Entry entry(@Node String id, @Node DateTime published, @Source Text title, @Source Text content,
                       @Attribute("g:etag") String etag, Optional<Author> author) {
        return new Entry(id, published, title, content, etag, author);
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

    @Node("g:image")
    public Image image(@Attribute String src, @Attribute Integer width, @Attribute Integer height) {
        return new Image(src, width, height);
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
                    throw new IllegalArgumentException("Unsupported text type: " + textType);
            }
        }).orElse(new SimpleText(content));
    }
}
