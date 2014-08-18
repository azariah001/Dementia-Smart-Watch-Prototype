<?php
/**
 * Created by PhpStorm.
 * User: azarel
 * Date: 7/08/14
 * Time: 4:05 PM
 */

include($_SERVER['DOCUMENT_ROOT'] . "/php/style-kit.php");

function watch($page) {
  echo '<!DOCTYPE html>
<html>
<head>
  <title></title>
  <link rel="stylesheet" type="test/css" href="/css/bootstrap.css">

  <link rel="stylesheet" type="test/css" href="/css/jquery-ui.css">

  <style>
  html {
    height: 100%;
    width: 100%;
    padding: 0px;
      margin: 0px;
    }
    body {
    padding: 0px;
      margin: 0px;
      height: 100%;
      width: 100%;
      background-color: #333333;
    }
    .watch {
      padding: 0px;
      margin: 0px;
      height: 100%;
      width: 100%;

    }
    .watch > .face {
    height: 200px;
      width: 400px;
      position: relative;
      top: 50%;
      bottom: 50%;
      left: 50%;
      right: 50%;
      margin-top: -100px;
      margin-left: -200px;
      margin-right: -200px;
      margin-bottom: -100px;
      background-color: #000000;
      padding: 0px;
    }
    .watch > .face > .row {
    position: static;
    float: none;
    height: 50%;
    width: 100%;
    padding: 0px;
      margin: 0px;
    }
    .watch > .face > .row > .tile {
    position: relative;
    margin: 5px;
      background-color: #ffffff;
      float: left;
      border-radius: 50px;
    }
    .watch > .face > .row > .tile.height-1 {
    height: 90px;
    }
    .watch > .face > .row > .tile.height-2 {
    height: 190px;
    }
    .watch > .face > .row > .tile.width-1 {
    width: 90px;
    }
    .watch > .face > .row > .tile.width-2 {
    width: 190px;
    }
    .watch > .face > .row > .tile.clock {
      background-color: #2F4580;
      color: #ffffff;
    }
    .tile.clock > p:first-child {
      text-align: center;
      font-size: 20px;
      line-height: 20px;
      margin: 0px;
      margin-bottom: 5px;
      margin-top: 10px;
    }
    .tile.clock > p:last-child {
      text-align: center;
      font-size: 20px;
      line-height: 20px;
      margin: 0px;
      margin-top: 5px;
    }
    .watch > .face > .row > .tile.help {
      background-color: #0036C9;
      color: #ffffff;
    }
    .tile.help {
      font-size: 70px;
      line-height: 90px;
      text-align: center;
      font-weight: bold;
    }
    .watch > .face > .row > .tile.location {
      background-color: #00C431;
    }
    .tile.location > img {
      width: 90px;
      height: 90px;
    }
    .watch > .face > .row > .tile.call {
      background-color: #7900C4;
    }
    .tile.call > img {
      width: 50px;
      height: 50px;
      margin: 20px;
    }
  </style>


  <!-- 1. Load platform.js for polyfill support. -->
  <script src="/components/platform/platform.js"></script><style type="text/css"></style>

  <!-- 2. Use an HTML Import to bring in the element. -->
  <link rel="import" href="/components/core-ajax/core-ajax.html">

  <link href="/components/paper-shadow/paper-shadow.html" rel="import">
</head>
<body>
  <div class="watch">
    <div class="face">
    ' . $page . '
    </div>
  </div>

  <script type="text/javascript" src="/js/jquery-1.8.3.min.js"></script>
  <script type="text/javascript" src="/js/jquery-ui-1.10.3.custom.min.js"></script>
  <script type="text/javascript" src="/js/jquery.ui.touch-punch.min.js"></script>
  <script type="text/javascript" src="/js/bootstrap.min.js"></script>
  <script type="text/javascript" src="/js/bootstrap-select.js"></script>
  <script type="text/javascript" src="/js/bootstrap-switch.js"></script>
  <script type="text/javascript" src="http://getbootstrap.com/assets/js/docs.min.js"></script>
  <script type="text/javascript" src="/js/flatui-checkbox.js"></script>
  <script type="text/javascript" src="/js/flatui-radio.js"></script>
  <script type="text/javascript" src="/js/jquery.tagsinput.js"></script>
  <script type="text/javascript" src="/js/jquery.placeholder.js"></script>
</body>
</html>';
}