<%@ taglib prefix="c" uri="http://www.springframework.org/tags" %>
<html>
<head lang="en">
    <title>Google clone)))</title>

    <c:url value="/resources/libraries/jquery-2.2.2.min.js" var="jquery"/>
    <script src="${jquery}" type="text/javascript"></script>

    <c:url value="/resources/js/search.js" var="search_js"/>
    <script src="${search_js}" type="text/javascript"></script>

    <c:url value="/resources/css/search.css" var="search_css"/>
    <link href="${search_css}" rel="stylesheet">
</head>
<body>
<div class="main_style">
    <div class="letters">
        <div class="google_word">
            <label class="font g1">G</label>
            <label class="font o1">o</label>
            <label class="font o2">o</label>
            <label class="font g2">g</label>
            <label class="font l">l</label>
            <label class="font e">e</label>
        </div>
        <div class="clone_word">
            <label class="font c">C</label>
            <label class="font l">l</label>
            <label class="font o1">o</label>
            <label class="font n">n</label>
            <label class="font g1">e</label>
        </div>
    </div>
    <input type="text" placeholder="Type query" class="search_text_box">
    <div class="search_button">Search</div>

    <div class="search_result">

    </div>
</div>
</body>
</html>