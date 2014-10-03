<?php
require_once './database.inc.php';

echo htmlspecialchars(deleteGeofence($_POST['id']));
?>