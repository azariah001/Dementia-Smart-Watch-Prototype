<?php
require_once './database.inc.php';

echo htmlspecialchars(updateGeofence($_POST['id'], $_POST['radius'], $_POST['active']));
?>
