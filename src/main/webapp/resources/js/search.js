if (window.addEventListener)
    window.addEventListener("load", init);
else if (window.attachEvent)
    window.attachEvent("onload", init);

function init() {
    var searchResult = $(".search_result");
    var query;

    $(".search_button").click(function () {
        query = $(".search_text_box").val();

        if (!query || query.length == 0) {
            return;
        }

        var resourceUrl = "/search?q=" + query;

        $.ajax({
            method: "GET",
            url: resourceUrl,
            success: function (results) {
                history.pushState("", results, resourceUrl);


                searchResult.empty();

                var id = 0;

                if (isEmpty(results)) {
                    searchResult.text("Query has no results...");

                    return;
                }

                for (var i in results) {
                    if (!results.hasOwnProperty(i)) {
                        continue;
                    }

                    var result = results[i];

                    createWidget(result, ++id);
                }
            },
            error: function () {
                searchResult.text("Query has no results... Try to index some page  ");

                searchResult.append("<a href='http://localhost:8080/index'>Index Page</a>");
            }
        });
    });

    function isEmpty(obj) {
        var hasOwnProperty = Object.prototype.hasOwnProperty;

        if (obj == null) return true;
        if (obj.length > 0)    return false;
        if (obj.length === 0)  return true;

        for (var key in obj) {
            if (hasOwnProperty.call(obj, key)) return false;
        }

        return true;
    }

    function createWidget(result, id) {
        var title = result["pageTitle"];
        var link = result["link"];
        var hits = result["hits"];

        var mainElementId = "main" + id;
        var urlId = "id" + id;
        var titleId = "title" + id;

        searchResult.append('<div id=' + mainElementId + ' class="element">');

        var mainElement = $("#" + mainElementId);

        mainElement.click(function () {
            var url = $("#" + urlId).text();

            var newTab = window.open(url, '_blank');
            newTab.focus();
        });

        mainElement.append('<div id=' + titleId + ' class="title">')
            .append('<div id=' + urlId + ' class="url">');

        $("#" + urlId).text(link);
        $("#" + titleId).text(typeof title === 'undefined' || title === "" ? "Title" : title);

        createMatchesWidget(mainElement, hits, id);
    }

    function createMatchesWidget(mainElement, hits, id) {
        var matchesId = "matches" + id;

        mainElement.append('<div id=' + matchesId + ' class="matches">');

        for (var hitIndex in hits) {
            if (!hits.hasOwnProperty(hitIndex)) {
                continue;
            }

            var hit = hits[hitIndex];

            var words = hit.split(" ");

            for (var wordIndex in words) {
                if (!words.hasOwnProperty(wordIndex)) {
                    continue;
                }

                var word = words[wordIndex];

                var wordId = matchesId + hitIndex + wordIndex;

                $("#" + matchesId).append('<div id=' + wordId + ' class="word">');
                var wordElement = $("#" + wordId);
                wordElement.text(word);

                selectMatchedWord(word, wordId);
            }
        }
    }

    function selectMatchedWord(word, wordId) {
        var queryWords = query.split(" ");

        for (var i in queryWords) {
            if (!queryWords.hasOwnProperty(i)) {
                continue
            }

            var queryWord = queryWords[i];

            if (queryWord.toLowerCase() === word) {
                $("#" + wordId).attr("style", "background: yellow");
            }
        }
    }
}