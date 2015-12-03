$(document).ready(function () {
    var report = $("#report");
    if (report) {
        var num = $("#test7").length,
            errors = 0,
            node1 = $("<p/>", {text: "check that inline (7) js only one on page - "});
        report.append(node1);
        if (num != 1) {
            errors++;
            node1.append($("<b/>", {class: "error", text: "failed"}))
        } else {
            node1.append($("<b/>", {class: "success", text: "done"}))
        }
    }
});

