<%@ taglib prefix="c" uri="http://www.springframework.org/tags" %>
<html>
<head lang="en">
    <title>Google clone)))</title>

    <c:url value="/resources/libraries/jquery-2.2.2.min.js" var="jquery"/>
    <script src="${jquery}" type="text/javascript"></script>

    <c:url value="/resources/js/index.js" var="index_js"/>
    <script src="${index_js}" type="text/javascript"></script>

    <c:url value="/resources/css/index.css" var="index_css"/>
    <link href="${index_css}" rel="stylesheet">

    <c:url value="/resources/css/loader.css" var="loader_css"/>
    <link href="${loader_css}" rel="stylesheet">
</head>
<body>
<div class="main">
    <div class="path_to_res_div">
        <label class="path_to_res_label main_font">Path To Resource:</label>
        <input type="text" placeholder="Path to resource" id="query" class="index_text_box">
    </div>
    <div class="deep_div">
        <label class="deep_label main_font">Index Deep:</label>
        <input type="text" placeholder="Indexing deep" id="deep" class="deep_text_box">
    </div>
    <div class="index_button">
        <label class="main_font">Index</label>
    </div>

    <div class="loader">
        <label class="loader_label main_font">Indexing...</label>
        <div class="cs-loader">
            <div class="cs-loader-inner">
                <label>.</label>
                <label>.</label>
                <label>.</label>
                <label>.</label>
                <label>.</label>
                <label>.</label>
            </div>
        </div>
    </div>
</div>
</body>
</html>