package dmitry.shnurenko.test.task.index;

import dmitry.shnurenko.test.task.common.LuceneParamProvider;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * The class provides business logic which allows index page via Lucene.
 *
 * @author Dmitry Shnurenko
 */
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PageIndexer {
    private static final Logger logger = LogManager.getLogger(PageIndexer.class);

    private final LuceneParamProvider luceneParamProvider;

    private ExecutorService executor;
    private IndexWriter     indexWriter;

    @Autowired
    public PageIndexer(LuceneParamProvider luceneParamProvider) {
        this.luceneParamProvider = luceneParamProvider;
    }

    public void initIndexer() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(luceneParamProvider.getAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        indexWriter = new IndexWriter(luceneParamProvider.getDirectory(), config);
        executor = Executors.newFixedThreadPool(20);
    }

    /**
     * The method performs page indexing. The indexing performs recursively and in different threads. For each
     * internal link will be created different thread. The method has ability to limit recursive deep.
     *
     * @param resourceUrl URL to page which will be indexed
     * @param deep        deep of indexing
     */
    public void indexPage(@NonNull String resourceUrl, int deep) {
        if (deep < 1) {
            return;
        }

        try {
            PageParser parser = new PageParser(resourceUrl);

            String plainText = parser.getPlainText();
            String pageTitle = parser.getPageTitle();

            Document document = new Document();
            document.add(new TextField("content", plainText, Field.Store.YES));
            document.add(new StringField("url", resourceUrl, Field.Store.YES));
            document.add(new StringField("title", pageTitle, Field.Store.YES));

            indexWriter.addDocument(document);

            indexInternalLinks(parser, --deep);
        } catch (IOException exception) {
            Thread.currentThread().interrupt();

            logger.error("Can not index " + resourceUrl + "__________" + exception.getLocalizedMessage());
        }
    }

    private void indexInternalLinks(@NonNull PageParser parser, int deep) {
        Set<String> links = parser.getLinks();

        List<Callable<String>> tasks = new ArrayList<>();

        for (String link : links) {
            tasks.add(() -> {
                indexPage(link, deep);
                return null;
            });
        }

        try {
            executor.invokeAll(tasks, 3_000, MILLISECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            logger.error("Indexing failed " + exception.getLocalizedMessage());
        }
    }

    public void close() throws IOException {
        executor.shutdownNow();

        if (indexWriter != null) {
            indexWriter.close();
        }
    }
}
