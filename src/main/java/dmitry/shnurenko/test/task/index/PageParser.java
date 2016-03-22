package dmitry.shnurenko.test.task.index;

import dmitry.shnurenko.test.task.util.URLValidator;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * The class provides methods to parse html pages.
 *
 * @author Dmitry Shnurenko
 */
class PageParser {

    private final Document document;
    private final URL      url;

    public PageParser(@NonNull String resourceUrl) throws IOException {
        this.url = new URL(resourceUrl);

        InputStream inputStream = url.openStream();

        try {
            String html = IOUtils.toString(inputStream, "UTF-8");

            this.document = Jsoup.parse(html);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Returns title of parsed page.
     */
    @NonNull
    public String getPageTitle() {
        return document.title();
    }

    /**
     * Returns plain text representation of page.
     */
    @NonNull
    public String getPlainText() throws IOException {
        return document.text();
    }

    /**
     * Returns set of internal links from page
     */
    @NonNull
    public Set<String> getLinks() {
        Elements links = document.select("a");

        Set<String> allLinks = new HashSet<>();

        for (Element link : links) {
            String href = link.attr("href");

            if (URLValidator.isUrlValid(href)) {
                allLinks.add(href);
            }

            addRelativeLink(allLinks, href);
        }

        return allLinks;
    }

    private void addRelativeLink(@NonNull Set<String> allLinks, @NonNull String href) {
        if (href.startsWith("/")) {
            try {
                allLinks.add(new URL(url, href).toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
}
