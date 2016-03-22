package dmitry.shnurenko.test.task.common;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The class provides business logic which allows to add, store and provide indexed pages.
 *
 * @author Dmitry Shnurenko
 */
@Component
public class IndexedPages {

    private final Map<String, String> indexedPages;

    public IndexedPages() {
        this.indexedPages = new ConcurrentHashMap<>();
    }

    public void add(@NonNull String url, @NonNull String pageTitle) {
        indexedPages.put(url, pageTitle);
    }

    @NonNull
    public Map<String, String> get() {
        return indexedPages;
    }

    public boolean contains(@NonNull String key) {
        return indexedPages.containsKey(key);
    }
}
