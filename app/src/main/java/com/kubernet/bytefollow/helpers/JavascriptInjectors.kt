package com.kubernet.bytefollow.helpers

class JavascriptInjectors {
    companion object {
        const val followInject = "javascript:(function() {\n" +
                "\t(document.getElementsByClassName(\"tab\"))[0].hidden = true;\n" +
                "\t(document.getElementsByClassName(\"video-list\"))[0].innerHTML = \"\";\n" +
                "\t(document.getElementsByClassName(\"footer-bar-container\")).length > 0 ? (document.getElementsByClassName(\"footer-bar-container\"))[0].outerHTML = \"\" : \"\";\n" +
                "\t(document.getElementsByClassName(\"guide\")).length > 0 ? (document.getElementsByClassName(\"guide\"))[0].outerHTML = \"\" : \"\";\n" +
                "\t(document.getElementsByClassName(\"mask\")).length > 0 ? (document.getElementsByClassName(\"mask\"))[0].outerHTML = \"\" : \"\";\n" +
                "\n" +
                "\tsetTimeout(function() {\n" +
                "\t\t(document.getElementsByClassName(\"follow-button\"))[0].click();\n" +
                "\t}, 1000);\n" +
                "\n" +
                "})();"

        const val likeInject = "javascript:(function() {\n" +
                "\t(document.getElementsByClassName(\"guide\")).length > 0 ? (document.getElementsByClassName(\"guide\"))[0].outerHTML = \"\" : \"\";\n" +
                "\t(document.getElementsByClassName(\"mask\")).length > 0 ? (document.getElementsByClassName(\"mask\"))[0].outerHTML = \"\" : \"\";\n" +
                "\tsetTimeout(function() {\n" +
                "\t\t(document.getElementsByClassName(\"heart-twink\"))[0].click()\n" +
                "\t}, 1000);\n" +
                "\n" +
                "})();"
    }


}