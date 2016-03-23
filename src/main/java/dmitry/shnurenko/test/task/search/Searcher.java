package dmitry.shnurenko.test.task.search;

import dmitry.shnurenko.test.task.common.IndexedPages;
import dmitry.shnurenko.test.task.common.LuceneParamProvider;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The class provides methods which allows search full matched text in prepared indexed data.
 *
 * @author Dmitry Shnurenko
 */
@Component
public class Searcher {

    private static final int HINTS_PER_PAGE = 100;

    private final IndexedPages        indexedPages;
    private final LuceneParamProvider luceneParamProvider;

    @Autowired
    public Searcher(IndexedPages indexedPages, LuceneParamProvider luceneParamProvider) {
        this.indexedPages = indexedPages;
        this.luceneParamProvider = luceneParamProvider;
    }

    /**
     * The method search full text matches and returns Map which contains url of resource which contains text
     * as key and page title as value.
     *
     * @param textToSearch text for which search will executes
     * @return an instance of Map<String,String> which contains resource url as key and page title as value.
     * @throws IOException    can be thrown during searching or opening Lucene RAM directory.
     * @throws ParseException can be thrown during query parsing
     */
    @NonNull
    public Map<String, String> search(@NonNull String textToSearch) throws IOException, ParseException {
        Map<String, String> result = new HashMap<>();

        try (IndexReader reader = DirectoryReader.open(luceneParamProvider.getDirectory())) {
            IndexSearcher searcher = new IndexSearcher(reader);

            for (Map.Entry<String, String> entry : indexedPages.get().entrySet()) {
                String resourceUrl = entry.getKey();
                String pageTitle = entry.getValue();

                QueryParser queryParser = new QueryParser(pageTitle, luceneParamProvider.getAnalyzer());
                Query query = queryParser.parse(textToSearch);

                TopDocs docs = searcher.search(query, HINTS_PER_PAGE);
                ScoreDoc[] hits = docs.scoreDocs;

                if (hits.length > 0) {
                    result.put(pageTitle, resourceUrl);
                }
            }
        }

        return result;
    }
}
