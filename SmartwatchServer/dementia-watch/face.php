<?php
/**
 * Created by PhpStorm.
 * User: azarel
 * Date: 11/08/14
 * Time: 2:40 PM
 */
include "template.php";

$face = '
      <div class="row">
        <div class="tile height-1 width-1 call">
          <img src="phone.png">
        </div>
        <div class="tile height-1 width-1" style="background-color: #01706B"></div>
        <div class="tile height-1 width-2 clock">
          <p>11:59<br></p>
          <p>Saturday <br> 3rd Sept 2014</p>
        </div>
      </div>
      <div class="row">
        <div class="tile height-1 width-2" style="background-color: #0A7001"></div>
        <div class="tile height-1 width-1 location">
          <img src="location.png">
        </div>
        <div class="tile height-1 width-1 help">
          <p>?</p>
        </div>
      </div>
    ';
watch($face);