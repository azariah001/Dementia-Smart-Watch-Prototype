<?php
require_once './database.inc.php';
echo htmlspecialchars(getLastLocation($_POST['patient']));
?>