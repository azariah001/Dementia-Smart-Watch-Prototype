<?php
/**
 * Created by PhpStorm.
 * User: azarel
 * Date: 12/06/14
 * Time: 9:37 PM
 */

include($_SERVER['DOCUMENT_ROOT']."/css/css.php");
include($_SERVER['DOCUMENT_ROOT']."/js/js.php");


function head($title) {
  echo "<head>";
    echo "<meta charset='utf-8'>";
    echo "<title>".$title."</title>";
    echo "<meta name='viewport' content='width=device-width, initial-scale=1.0'>";
    css( ["bootstrap",
      "dashboard",
      "flat-ui"] );
    html5_shiv();

    echo '<!-- Polymer Material Design Tests -->
    <!-- 1. Load platform.js for polyfill support. -->
    <script src="/components/platform/platform.js"></script>

    <!-- 2. Use an HTML Import to bring in the element. -->
    <link rel="import" href="/components/core-ajax/core-ajax.html">

    <!-- 3. Import Specific Elements -->
    <link href="/components/paper-shadow/paper-shadow.html" rel="import">';
  echo "</head>";
};

function footer() {
  js( ["jquery-1.8.3",
    "jquery-ui-1.10.3-custom",
    "jquery-ui-touch",
    "bootstrap",
    "bootstrap-select",
    "bootstrap-switch",
    "docs",
    "flatui-checkbox",
    "flatui-radio",
    "jquery-tagsinput",
    "jquery-placeholder"] );
};

// MySQL Server connect settings