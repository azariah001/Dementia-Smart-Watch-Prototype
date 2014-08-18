<?php
/**
 * Created by PhpStorm.
 * User: azarel
 * Date: 11/06/14
 * Time: 9:53 PM
 *
 * Implementation Completed Additional References can be added as needed.
 */

function css($arguments) {
    foreach ($arguments as $classes) {
        switch($classes) {
            case $classes == "bootstrap": echo "<link rel='stylesheet' type='test/css' href='/css/bootstrap.css'>"; break;
            case $classes == "bootstrap-docs": echo "<link rel='stylesheet' type='test/css' href='/css/bootstrap-docs.css'>"; break;
            case $classes == "flat-ui": echo "<link rel='stylesheet' type='test/css' href='/css/flat-ui.css'>"; break;
            case $classes == "ftscroller": echo "<link rel='stylesheet' type='test/css' href='/css/ftscroller.css'>"; break;
            case $classes == "jquery-ui": echo "<link rel='stylesheet' type='test/css' href='/css/jquery-ui.css'>"; break;
            case $classes == "prettify": echo "<link rel='stylesheet' type='test/css' href='/css/prettify.css'>"; break;
            case $classes == "signin": echo "<link rel='stylesheet' type='test/css' href='/css/signin.css'>"; break;
            case $classes == "dashboard": echo "<link rel='stylesheet' type='text/css' href='http://getbootstrap.com/examples/dashboard/dashboard.css'>"; break;
            case $classes == "internal-shadows": echo "<style>.internal-shadow-1 { box-shadow: rgba(0,0,0,0.5) 2px 2px 10px 1px inset; }</style>"; break;
            default: echo "<!-- Encountered Unkown Class -->"; break;
        }
    }
}
