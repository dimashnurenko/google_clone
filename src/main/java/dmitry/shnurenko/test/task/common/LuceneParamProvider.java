package dmitry.shnurenko.test.task.common;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The class provides general instances of objects which need in order to use Lucene.
 *
 * @author Dmitry Shnurenko
 */
@Component
public class LuceneParamProvider {

    private final Directory directory;
    private final Analyzer  analyzer;

    public LuceneParamProvider() throws IOException {
        Path pathToIndexDirectory = Paths.get("/lucene/index");
        this.directory = new SimpleFSDirectory(pathToIndexDirectory);
        this.analyzer = new StandardAnalyzer();
    }

    /**
     * Returns instance of {@link SimpleFSDirectory} which will be used to store index result by Lucene.
     */
    @NonNull
    public Directory getDirectory() {
        return directory;
    }

    /**
     * Returns instance of {@link Analyzer} which will be used during indexing and searching by Lucene.
     */
    @NonNull
    public Analyzer getAnalyzer() {
        return analyzer;
    }
}
