if (window.addEventListener)
    window.addEventListener("load", init);
else if (window.attachEvent)
    window.attachEvent("onload", init);

function init() {
    var loader = $(".loader").attr("hidden", "hidden");
    var indexingProcess = false;

    $(".index_button").click(function () {
        if (indexingProcess) {
            return;
        }

        var resourceUrl = "/index?q=" + $("#query").val() + "&deep=" + $("#deep").val();

        history.pushState("", "", resourceUrl);

        loader.removeAttr("hidden");

        indexingProcess = true;
        $.ajax({
            method: "POST",
            url: resourceUrl,
            success: function () {
                window.location.replace("http://localhost:8080");

                loader.attr("hidden", "hidden");

                indexingProcess = false;
            },
            error: function () {
                window.location.href = '../resources/views/error.jsp';
            }
        });
    });
}