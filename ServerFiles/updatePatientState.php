<?php
require_once './database.inc.php';

$data['patient'] = $_POST['patient'];
$data['panic_state'] = $_POST['panic_state'];


echo htmlspecialchars(updatePatientState($data));
?>