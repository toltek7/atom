//<![CDATA[
$(document).ready(function () {
    var cnt = $("#test-js");
    if (cnt) {
        $("<div/>", {
            class: "test7",
            text: "test-7js"
        }).appendTo(cnt);
    }
});
//]]>