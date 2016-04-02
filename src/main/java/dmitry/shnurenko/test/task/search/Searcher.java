package dmitry.shnurenko.test.task.search;

import dmitry.shnurenko.test.task.common.LuceneParamProvider;
import dmitry.shnurenko.test.task.model.SearchResult;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * The class provides business logic which allows execute search operations via Lucene.
 *
 * @author Dmitry Shnurenko
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Searcher {

    private static final int HINTS_PER_PAGE = 100;

    private final LuceneParamProvider luceneParamProvider;

    @Autowired
    public Searcher(LuceneParamProvider luceneParamProvider) {
        this.luceneParamProvider = luceneParamProvider;
    }

    /**
     * The method search full text matches and returns result which contains url of resource, page title and some hits.
     *
     * @param textToSearch text for which search will be executed
     * @return an instance of Set<SearchResult> which contains set of resource url, page title and hits.
     * @throws IOException    can be thrown during searching or opening Lucene FS directory.
     * @throws ParseException can be thrown during query parsing
     */
    @NonNull
    public Set<SearchResult> search(@NonNull String textToSearch) throws IOException, ParseException {
        Set<SearchResult> result = new HashSet<>();

        try (IndexReader reader = DirectoryReader.open(luceneParamProvider.getDirectory())) {
            IndexSearcher searcher = new IndexSearcher(reader);

            QueryParser queryParser = new QueryParser("content", luceneParamProvider.getAnalyzer());

            Query query = createQuery(textToSearch, queryParser);

            TopDocs docs = searcher.search(query, HINTS_PER_PAGE);
            ScoreDoc[] hits = docs.scoreDocs;

            for (ScoreDoc scoreDoc : hits) {
                String content = searcher.doc(scoreDoc.doc).get("content");
                String title = searcher.doc(scoreDoc.doc).get("title");
                String resourceUrl = searcher.doc(scoreDoc.doc).get("url");

                SearchResult searchResult = SearchResult.newBuilder()
                                                        .withPageTitle(title)
                                                        .withLink(resourceUrl)
                                                        .withHits(content, textToSearch.split(" "))
                                                        .build();
                if (!searchResult.getHits().isEmpty()) {
                    result.add(searchResult);
                }
            }
        }

        return result;
    }

    private Query createQuery(String textToSearch, QueryParser queryParser) throws ParseException {
        String readyText = textToSearch.trim();

        Query keyWordsQuery = queryParser.parse(readyText);

        PhraseQuery.Builder phraseBuilder = new PhraseQuery.Builder();

        String[] words = readyText.split(" ");

        for (int i = 0; i < words.length; i++) {
            phraseBuilder.add(new Term("content", words[i]), i);
        }

        PhraseQuery phraseQuery = phraseBuilder.build();

        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();

        booleanQuery.add(keyWordsQuery, BooleanClause.Occur.MUST);
        booleanQuery.add(phraseQuery, BooleanClause.Occur.SHOULD);

        return booleanQuery.build();
    }
}
