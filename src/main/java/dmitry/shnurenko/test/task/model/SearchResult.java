package dmitry.shnurenko.test.task.model;

import edu.umd.cs.findbugs.annotations.NonNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The class represent unit of searching result. Contains such information as page title, page URL and
 * set of hits in according to search query.
 *
 * @author Dmitry Shnurenko
 */
public final class SearchResult implements Serializable {

    /**
     * Defines count of strings which will be added in search result.
     */
    private static final int COUNT_STRINGS_IN_RESULT    = 4;
    /**
     * Defines count of characters in each string.
     */
    private static final int COUNT_CHARACTERS_IN_STRING = 50;

    private String      pageTitle;
    private String      link;
    private Set<String> hits;

    SearchResult() {
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public String getLink() {
        return link;
    }

    public Set<String> getHits() {
        return hits;
    }

    public static SearchResultBuilder newBuilder() {
        return new SearchResult().new SearchResultBuilder();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        SearchResult that = (SearchResult) other;

        return Objects.equals(pageTitle, that.pageTitle);

    }

    @Override
    public int hashCode() {
        return Objects.hash(pageTitle);
    }

    public class SearchResultBuilder {

        public SearchResultBuilder withPageTitle(@NonNull String pageTitle) {
            SearchResult.this.pageTitle = pageTitle;

            return this;
        }

        public SearchResultBuilder withLink(@NonNull String link) {
            SearchResult.this.link = link;

            return this;
        }

        /**
         * The method convert plain text to set of strings. Each string has to contains one of passed words.
         *
         * @param content      plain text as one string
         * @param wordsToMatch words are using to define string which matches to query
         */
        public SearchResultBuilder withHits(@NonNull String content, @NonNull String[] wordsToMatch) {
            String[] words = content.split(" ");

            Set<String> strings = new HashSet<>();
            StringBuilder builder = new StringBuilder();

            for (String word : words) {
                builder.append(word).append(' ');

                if (strings.size() == COUNT_STRINGS_IN_RESULT) {
                    break;
                }

                if (builder.length() > COUNT_CHARACTERS_IN_STRING) {
                    String string = builder.toString();

                    builder.setLength(0);

                    if (containAny(string.split(" "), wordsToMatch)) {
                        strings.add(string);
                    }
                }
            }
            SearchResult.this.hits = strings;

            return this;
        }

        private boolean containAny(String[] content, String[] wordsToMatch) {
            for (String word : wordsToMatch) {
                if (Arrays.asList(content).contains(word)) {
                    return true;
                }
            }

            return false;
        }

        public SearchResult build() {
            SearchResult searchResult = new SearchResult();
            searchResult.pageTitle = SearchResult.this.pageTitle;
            searchResult.link = SearchResult.this.link;
            searchResult.hits = SearchResult.this.hits;

            return searchResult;
        }
    }
}
