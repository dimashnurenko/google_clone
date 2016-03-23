package dmitry.shnurenko.test.task.controller;

import dmitry.shnurenko.test.task.index.PageIndexer;
import dmitry.shnurenko.test.task.search.Searcher;
import dmitry.shnurenko.test.task.util.URLValidator;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Dmitry Shnurenko
 */
@Controller
@RequestMapping("/")
public class GoogleCloneController {

    private static final int MAX_DEEP = 3;

    @Autowired
    private PageIndexer pageIndexer;
    @Autowired
    private Searcher    searcher;

    @RequestMapping(method = GET)
    public String showSearchPage() {
        return "search";
    }

    @RequestMapping(method = GET,
                    path = "/index")
    public String showIndexPage() {
        return "index";
    }

    @RequestMapping(method = POST,
                    path = "/index")
    public void indexPage(@RequestParam("q") String resourceUrl,
                          @RequestParam(value = "deep",
                                        defaultValue = "1") int deep) throws IOException {
        if (deep > MAX_DEEP) {
            throw new IllegalArgumentException("Deep must be in range 1-3 the value " + deep + " is not allowed");
        }
        if (!URLValidator.isUrlValid(resourceUrl)) {
            throw new IllegalArgumentException("URL " + resourceUrl + " is not valid.");
        }

        pageIndexer.initIndexer();
        pageIndexer.indexPage(resourceUrl, deep);
        pageIndexer.close();
    }

    @RequestMapping(method = GET,
                    path = "/search")
    @ResponseBody
    public Map<String, String> searchText(@RequestParam("q") String textToSearch) throws IOException, ParseException {
        return searcher.search(textToSearch);
    }
}
