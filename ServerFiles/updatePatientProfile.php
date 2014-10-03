<?php
require_once './database.inc.php';

echo htmlspecialchars(updatePatientProfile($_POST['id'], $_POST['name'], $_POST['age'], $_POST['address'], $_POST['medical'], $_POST['contact']));
?>