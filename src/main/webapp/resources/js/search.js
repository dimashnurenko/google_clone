if (window.addEventListener)
    window.addEventListener("load", init);
else if (window.attachEvent)
    window.attachEvent("onload", init);

function init() {
    var searchResult = $(".search_result");

    $(".search_button").click(function () {
        var query = $(".search_text_box").val();

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

                for (var title in results) {
                    if (!results.hasOwnProperty(title)) {
                        continue;
                    }

                    var url = results[title];

                    createWidget(title, url, ++id);
                }
            },
            error: function () {
                searchResult.text("Query has no results... Try to index some page  ");

                searchResult.append("<a href='http://localhost:8080/index'>Index Page</a>");
            }
        });
    });

    function createWidget(title, url, id) {
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

        $("#" + urlId).text(url);
        $("#" + titleId).text(typeof title === 'undefined' || title === "" ? "Title" : title);
    }

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
}