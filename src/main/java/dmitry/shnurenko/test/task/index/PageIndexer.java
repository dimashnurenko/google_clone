package dmitry.shnurenko.test.task.index;

import dmitry.shnurenko.test.task.common.IndexedPages;
import dmitry.shnurenko.test.task.common.LuceneParamProvider;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The class provides business logic which allows index page via Lucene.
 *
 * @author Dmitry Shnurenko
 */
@Component
public class PageIndexer {
    private static final Logger logger = LogManager.getLogger(PageIndexer.class);

    private final IndexedPages        indexedPages;
    private final LuceneParamProvider luceneParamProvider;

    private ExecutorService executor;
    private IndexWriter     indexWriter;

    @Autowired
    public PageIndexer(LuceneParamProvider luceneParamProvider, IndexedPages indexedPages) {
        this.indexedPages = indexedPages;
        this.luceneParamProvider = luceneParamProvider;
    }

    public void initIndexer() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(luceneParamProvider.getAnalyzer());
        indexWriter = new IndexWriter(luceneParamProvider.getDirectory(), config);
        executor = Executors.newCachedThreadPool();
    }

    /**
     * The method performs page indexing. The indexing performs recursively and in different threads. For each
     * internal link will be created different thread. The method has ability to limit recursive deep.
     *
     * @param resourceUrl URL to page which will be indexed
     * @param deep        deep of indexing
     */
    public void indexPage(@NonNull String resourceUrl, int deep) {
        if (deep < 1 || indexedPages.contains(resourceUrl)) {
            return;
        }

        try {
            PageParser parser = new PageParser(resourceUrl);

            String plainText = parser.getPlainText();
            String pageTitle = parser.getPageTitle();

            indexedPages.add(resourceUrl, pageTitle);

            Document document = new Document();
            document.add(new TextField(resourceUrl, plainText, Field.Store.YES));

            indexInternalLinks(parser, --deep);

            indexWriter.addDocument(document);
        } catch (IOException exception) {
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
            executor.invokeAll(tasks);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            logger.error("Indexing failed " + exception.getLocalizedMessage());
        }
    }

    public void close() throws IOException {
        executor.shutdown();

        if (indexWriter != null) {
            indexWriter.close();
        }
    }
}
