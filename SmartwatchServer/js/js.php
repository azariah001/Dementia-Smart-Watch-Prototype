<?php
/**
 * Created by PhpStorm.
 * User: azarel
 * Date: 12/06/14
 * Time: 1:24 PM
 *
 * Implementation Completed Additional References can be added as needed.
 */

function js($arguments) {
  foreach ($arguments as $scripts) {
    switch($scripts) {
      case $scripts == "application": echo "<script type='text/javascript' src='/js/application.js'></script>"; break;
      case $scripts == "bootstrap": echo "<script type='text/javascript' src='/js/bootstrap.min.js'></script>"; break;
      case $scripts == "bootstrap-select": echo "<script type='text/javascript' src='/js/bootstrap-select.js'></script>"; break;
      case $scripts == "bootstrap-switch": echo "<script type='text/javascript' src='/js/bootstrap-switch.js'></script>"; break;
      case $scripts == "bootstrap-typehead": echo "<script type='text/javascript' src='/js/bootstrap-typeahead.js'></script>"; break;
      case $scripts == "docs": echo "<script type='text/javascript' src='http://getbootstrap.com/assets/js/docs.min.js'></script>"; break;
      case $scripts == "flatui-checkbox": echo "<script type='text/javascript' src='/js/flatui-checkbox.js'></script>"; break;
      case $scripts == "flatui-fileinput": echo "<script type='text/javascript' src='/js/flatui-fileinput.js'></script>"; break;
      case $scripts == "flatui-radio": echo "<script type='text/javascript' src='/js/flatui-radio.js'></script>"; break;
      case $scripts == "ftscroller": echo "<script type='text/javascript' src='/js/ftscroller.js'></script>"; break;
      case $scripts == "holder": echo "<script type='text/javascript' src='/js/holder.js'></script>"; break;
      case $scripts == "html5shiv": echo "<script type='text/javascript' src='https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js'></script>"; break;
      case $scripts == "icon-font-ie7": echo "<script type='text/javascript' src='/js/icon-font-ie7.js'></script>"; break;
      case $scripts == "jquery-placeholder": echo "<script type='text/javascript' src='/js/jquery.placeholder.js'></script>"; break;
      case $scripts == "jquery-tagsinput": echo "<script type='text/javascript' src='/js/jquery.tagsinput.js'></script>"; break;
      case $scripts == "jquery-ui-touch": echo "<script type='text/javascript' src='/js/jquery.ui.touch-punch.min.js'></script>"; break;
      case $scripts == "jquery-1.8.3": echo "<script type='text/javascript' src='/js/jquery-1.8.3.min.js'></script>"; break;
      case $scripts == "jquery-1.9.1": echo "<script type='text/javascript' src='/js/jquery-1.9.1.js'></script>"; break;
      case $scripts == "jquery-1.10.2": echo "<script type='text/javascript' src='/js/jquery-1.10.2.min.js'></script>"; break;
      case $scripts == "jquery-2.0.3": echo "<script type='text/javascript' src='/js/jquery-2.0.3.min.js'></script>"; break;
      case $scripts == "jquery-ui": echo "<script type='text/javascript' src='/js/jquery-ui.js'></script>"; break;
      case $scripts == "jquery-ui-1.10.3-custom": echo "<script type='text/javascript' src='/js/jquery-ui-1.10.3.custom.min.js'></script>"; break;
      case $scripts == "rainyday": echo "<script type='text/javascript' src='/js/rainyday.js'></script>"; break;
      case $scripts == "respond": echo "<script type='text/javascript' src='https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js'></script>"; break;
      case $scripts == "typehead": echo "<script type='text/javascript' src='/js/typeahead.js'></script>"; break;
      case $scripts == "video": echo "<script type='text/javascript' src='/js/video.js'></script>"; break;
      default: echo "<!-- Encountered Unkown Script -->"; break;
    }
  }
}
function html5_shiv() {
    echo "<!-- HTML5 shim, for IE6-8 support of HTML5 elements. All other JS at the end of file. -->
    <!--[if lt IE 9]>
      <script src='https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js'></script>
      <script src='https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js'></script>
    <![endif]-->";
}