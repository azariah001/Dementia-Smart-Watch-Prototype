<?php
require_once './database.inc.php';

// TODO: only require the current admin id
echo htmlspecialchars(closestGeofences($_POST['patients']));
?>