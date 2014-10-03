<?php
require_once './database.inc.php';

echo htmlspecialchars(retrieveGeofences($_POST['patientid']));
?>