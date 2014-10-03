<?php
require_once './database.inc.php';
echo htmlspecialchars(getPatientProfile($_POST['patient_id']));
?>