<?php
require_once './database.inc.php';

$data['patient'] = $_POST['patient'];
$data['lat'] = $_POST['lat'];
$data['lng'] = $_POST['lng'];
$data['accuracy'] = $_POST['acc'];

echo htmlspecialchars(saveLocation($data));
?>