<?php
require_once './database.inc.php';
echo htmlspecialchars(getPatientState($_POST['idpatient_profiles']));
?>