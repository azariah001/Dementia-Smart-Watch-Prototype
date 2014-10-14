<?php
// database details
const DB_NAME="azarelho_agile";
const DB_USER="azarelho_agile";
const DB_PASS="inb372";
const DB_HOST="localhost";

const TABLE_COORD = "gps_data";
const TABLE_PROFILE = "patient_profiles";
const TABLE_GEOFENCE = "geofences";
const TABLE_REMINDER = "patient_reminders";

// Global variable
$latest_reminderID;

// database connection
$pdo = new PDO('mysql:host='. DB_HOST .';dbname='. DB_NAME, DB_USER, DB_PASS);

// set timezone
//date_default_timezone_set("Australia/Brisbane");

function saveLocation($data) {
    // inserts patient location into database
    global $pdo;

    // timestamp is automatically added by MySQL
    $stmt = $pdo->prepare('INSERT INTO `'. DB_NAME .'`.`'. TABLE_COORD .'` '.
        '(`patient`, `lat`, `lng`, `accuracy`, `time`) '.
        'VALUES '.
        '(:patient, :lat, :lng, :accuracy, UTC_TIMESTAMP);');
    $stmt->bindValue(':patient', $data['patient']);
    $stmt->bindValue(':lat', $data['lat']);
    $stmt->bindValue(':lng', $data['lng']);
    $stmt->bindValue(':accuracy', $data['accuracy']);
    $stmt->execute();
}

// TODO: make it work with distinct patients
function getLastLocation($patient) {
	// retrieve last known location for patient
	global $pdo;
	
	$stmt = $pdo->prepare('SELECT `lat`, `lng`, `accuracy`, `time` '.
		'FROM `'. TABLE_COORD .'` '.
		'WHERE `patient` = :patient '.
			'AND `time` > ('.
					'SELECT `time` '.
					'FROM `'. TABLE_COORD .'` '.
					'WHERE `patient` = :patient '.
					'ORDER BY `time` DESC '.
					'LIMIT 0,1 '.
				') - INTERVAL 20 MINUTE '.
		'ORDER BY `time` DESC '.
		'LIMIT 0,15');
	$stmt->bindValue(':patient', $patient);
	$stmt->execute();
	$result = $stmt->fetchAll();
	
	$locations = array();
	foreach ($result as $row) {
		$locations[] = $row['lat'] .','. $row['lng'] .','. $row['time'] .','. $row['accuracy'];
	}
	
	return implode(';', $locations);
}

// TODO: make it work with distinct patients
function getPatientProfile($patient_id) {
	// retrieve patient profile
	global $pdo;
	
	$stmt = $pdo->prepare('SELECT `patient_name`, `patient_age`, `patient_address`, `medical_information`, `emergency_contact` '.
		'FROM `'. TABLE_PROFILE .'` '.
		'WHERE `patient_id` = :patient_id '.
		'LIMIT 0,1');
	$stmt->bindValue(':patient_id', $patient_id);
	$stmt->execute(); 
	$result = $stmt->fetch();
	
	$name = $result[0];
	$age = $result[1];
	$address = $result[2];
	$medical = $result[3];
	$contact = $result[4];
	
	return $name .','. $age .','. $address .','. $medical .','. $contact;
}

function saveGeofence($data) {
    // inserts geofences into database
    global $pdo;

    $stmt = $pdo->prepare('INSERT INTO `'. DB_NAME .'`.`'. TABLE_GEOFENCE .'` '.
        '(`patient`, `lat`, `lng`, `radius`, `expiration`, `transition`) '.
        'VALUES '.
        '(:patient, :lat, :lng, :radius, :expiration, :transition);');
    $stmt->bindValue(':patient', $data['patient']);
    $stmt->bindValue(':lat', $data['lat']);
    $stmt->bindValue(':lng', $data['lng']);
    $stmt->bindValue(':radius', $data['radius']);
    $stmt->bindValue(':expiration', $data['expiration']);
	$stmt->bindValue(':transition', $data['transition']);
    
    $stmt->execute();
	// return the ID of the inserted row
	return $pdo->lastInsertId();
}

function deleteGeofence($id) {
    // inserts geofences into database
    global $pdo;

    $stmt = $pdo->prepare('DELETE FROM `'. DB_NAME .'`.`'. TABLE_GEOFENCE .'` '.
        'WHERE `id` = :id');
    $stmt->bindValue(':id', $id);
    $stmt->execute();
	
	// feedback whether the delete was successful
	if ($stmt->rowCount() > 0) {
		return 1;
	} else {
		return 0;
	}
}

function updateGeofence($id, $radius) {
    // inserts geofences into database
    global $pdo;

    $stmt = $pdo->prepare('UPDATE `'. DB_NAME .'`.`'. TABLE_GEOFENCE .'` '.
		'SET `radius` = :radius '.
        'WHERE `id` = :id');
    $stmt->bindValue(':id', $id);
	$stmt->bindValue(':radius', $radius);
    $stmt->execute();
	
	// feedback whether the update was successful
	if ($stmt->rowCount() > 0) {
		return 1;
	} else {
		return 0;
	}
}

function retrieveGeofences($patientid) {
	// retrieve geofence parameters for patientid
	global $pdo;
	
	$stmt = $pdo->prepare('SELECT `id`, `patient`, `lat`, `lng`, `radius`, `expiration`, `transition` '.
		'FROM `'. TABLE_GEOFENCE .'` '.
		'WHERE `patient` = ":patientid"');
	$stmt->bindValue(':patientid', $patientid);
	$stmt->execute(); 
	$result = $stmt->fetchAll();
	
	$fences = array();
	foreach ($result as $row) {
		$fences[] = $row['id'] .','. $row['patient'] .','. $row['lat'] .','. $row['lng'] .','. $row['radius'] .','. $row['expiration'] .','. $row['transition'];
	}
	
	return implode(';', $fences);
}

function closestGeofences($patients) {
	// retrieve the closest geofence for each patient including the distance
	global $pdo;
	
	$stmt = $pdo->prepare(

'SELECT `distances`.* FROM (
    
    SELECT `my_patients`.*,
        `gf`.`lat` AS `gf_lat`,
        `gf`.`lng` AS `gf_lng`,
        `gf`.`radius` AS `gf_radius`,
        6371000 * 2 * ASIN(SQRT(
            POWER(SIN((`my_patients`.`current_lat`- `gf`.`lat`) * pi()/180 / 2), 2)
                + COS(`my_patients`.`current_lat` * pi()/180) * COS(`gf`.`lat` * pi()/180) 
            * POWER(SIN((`my_patients`.`current_lng` - `gf`.`lng`) * pi()/180 / 2), 2)
        )) - `my_patients`.`current_accuracy` - `gf`.`radius` AS `effective_distance`
    
    FROM (
        SELECT  `p`.`patient_name` AS `name`,
        `p`.`patient_id` AS `id`,
        `p`.`panic_state` AS `panic`,
        `g`.`lat` AS `current_lat`,
        `g`.`lng` AS `current_lng`,
        `g`.`accuracy` AS `current_accuracy`,
        `g`.`time` AS `timestamp`
        FROM `patient_profiles` `p`
        LEFT JOIN `gps_data` `g` 
                INNER JOIN (
                    SELECT `patient`, MAX(`time`) AS `time`
                    FROM `gps_data`
                    GROUP BY `patient`
                ) `max` ON `g`.`patient` = `max`.`patient` AND `g`.`time` = `max`.`time`
            ON `g`.`patient` = `p`.`patient_id`
        GROUP BY `p`.`patient_id`
    ) `my_patients`

    LEFT JOIN `geofences` `gf`
        ON `my_patients`.`id` = `gf`.`patient`
    
) `distances`

INNER JOIN (
    SELECT `id`, MIN(`effective_distance`) AS `effective_distance`
    FROM (
        
        SELECT `my_patients`.*,
        `gf`.`lat` AS `gf_lat`,
        `gf`.`lng` AS `gf_lng`,
        `gf`.`radius` AS `gf_radius`,
        6371000 * 2 * ASIN(SQRT(
            POWER(SIN((`my_patients`.`current_lat`- `gf`.`lat`) * pi()/180 / 2), 2)
                + COS(`my_patients`.`current_lat` * pi()/180) * COS(`gf`.`lat` * pi()/180) 
            * POWER(SIN((`my_patients`.`current_lng` - `gf`.`lng`) * pi()/180 / 2), 2)
        )) - `my_patients`.`current_accuracy` - `gf`.`radius` AS `effective_distance`
    
        FROM (
            SELECT  `p`.`patient_name` AS `name`,
            `p`.`patient_id` AS `id`,
            `p`.`panic_state` AS `panic`,
            `g`.`lat` AS `current_lat`,
            `g`.`lng` AS `current_lng`,
            `g`.`accuracy` AS `current_accuracy`,
            `g`.`time` AS `timestamp`
            FROM `patient_profiles` `p`
            LEFT JOIN `gps_data` `g` 
                    INNER JOIN (
                        SELECT `patient`, MAX(`time`) AS `time`
                        FROM `gps_data`
                        GROUP BY `patient`
                    ) `max` ON `g`.`patient` = `max`.`patient` AND `g`.`time` = `max`.`time`
                ON `g`.`patient` = `p`.`patient_id`
            GROUP BY `p`.`patient_id`
        ) `my_patients`
        
        LEFT JOIN `geofences` `gf`
            ON `my_patients`.`id` = `gf`.`patient`
        
        ) `ss`
    
    GROUP BY `id`
) `mindist` ON `distances`.`id` = `mindist`.`id`
        AND (`distances`.`effective_distance` = `mindist`.`effective_distance`
             OR (`distances`.`effective_distance` IS NULL 
                 AND `mindist`.`effective_distance` IS NULL))

GROUP BY  `distances`.`id`');
	
	$stmt->execute();
	
	$result = $stmt->fetchAll();
	
	$patients = array();
	foreach ($result as $row) {
		$patients[] = $row['name'] .','. $row['id'] .','. $row['effective_distance'] .','. $row['timestamp'] .','. $row['panic'];
	}
	
	return implode(';', $patients);
}

function getPatientState($idpatient_profiles) {
	// retrieve patient profile
	global $pdo;
	
	$stmt = $pdo->prepare('SELECT `panic_state`, `patient_name` '.
		'FROM `'. TABLE_PROFILE .'` '.
		'LIMIT 0,1');
	//$stmt->bindValue(':patient', $patient);
	$stmt->execute(); 
	$result = $stmt->fetch();
	
	$state = $result[0];
	$name = $result[1];
	
	return $state.','.$name;
}

function updatePatientState($patient_id, $panic_state) {
	
	global $pdo;

	$stmt = $pdo->prepare('UPDATE `'. DB_NAME .'`.`'. TABLE_PROFILE .'` '.
		'SET `panic_state` = :panic_state '.
		'WHERE `patient_id` = :patient_id');
	$stmt->bindValue(':panic_state', $panic_state);
	$stmt->bindValue(':patient_id', $patient_id);
	$stmt->execute();
}

// Functions relating to Patient Reminders

function getLatestReminderID() {
	
	global $pdo;
	
	// Variables to store the create and store the latest reminderID
	$id_increment = 1;
	
	// Gets the last reminderID
	
	// Creates the Statement Handler
	$stmt = $pdo->query("SELECT * FROM " . DB_NAME . "." . TABLE_REMINDER . " ORDER BY patient_reminders.reminderID ASC");
	
	// Setting the fetch mode
	$stmt->setFetchMode(PDO::FETCH_ASSOC);
	
	// Increments the last used Patient ID and assigns that value to the latest Patient ID variable
	while($row = $stmt->fetch()) {
		$GLOBALS['latest_reminderID'] = $row['reminderID'];	
	}
}

function setLatestReminderID() {
	global $pdo;
	
	// Variables to store the create and store the latest reminderID
	$id_increment = 1;
	
	// Gets the last reminderID
	
	// Creates the Statement Handler
	$stmt = $pdo->query("SELECT * FROM " . DB_NAME . "." . TABLE_REMINDER . " ORDER BY patient_reminders.reminderID ASC");
	
	// Setting the fetch mode
	$stmt->setFetchMode(PDO::FETCH_ASSOC);
	
	// Increments the last used Patient ID and assigns that value to the latest Patient ID variable
	while($row = $stmt->fetch()) {
		$GLOBALS['latest_reminderID'] = $row['reminderID'] + $id_increment;	
	}
}

function putPatientReminder($data) {
	
	setLatestReminderID();
	
	global $pdo;
	
	// Creates a new Patient Reminder
	
	// Creates the Statement Handler to insert a new Patient reminder into the patient_reminders table
	$stmt = $pdo->prepare("INSERT INTO `" . DB_NAME . "`.`" . TABLE_REMINDER . "`" 
	. " (reminderID, title, organiser, beginTime, description, rrule) values (:reminderID, :title, :organiser, :beginTime, :description, :rrule)");
	$title = "title desu2";
	$organiser = "richard desu2";
	$beginTime = "3";
	$description = "description desu2";
	$rrule = "FREQ=DAILY";
	// Assigns variables to each placeholder
	$stmt->bindParam(':reminderID' , $GLOBALS['latest_reminderID']);
	$stmt->bindParam(':title'      , $data['title']);
	$stmt->bindParam(':organiser'  , $data['organiser']);
	$stmt->bindParam(':beginTime'  , $data['beginTime']);
	$stmt->bindParam(':description', $data['description']);
	$stmt->bindParam(':rrule'      , $data['RRULE']);
	
	// Executes the prepared statement
	$status = $stmt->execute();
}

function getPatientReminder() {
	
	getLatestReminderID();
	
	global $pdo;
	
	//$dicks = 1;
	// Creates the Statement Handler to get the latest reminderID from the patient_reminders table
	$stmt = $pdo->prepare("SELECT * FROM " . TABLE_REMINDER . " WHERE reminderID = :reminderID");
	
	// Assigns the variables to each placeholder
	$stmt->bindValue(':reminderID', $GLOBALS['latest_reminderID']);
	
	// Queries the database
	$stmt->execute();
	$stmtField = $stmt->fetchAll();
	
	// For each reminder field return the result as a row
	foreach($stmtField as $row) {
		// Assigns the values from the query to the declared variables
		$reminderID  = $row['reminderID'];
		$title       = $row['title'];
		$organiser   = $row['organiser'];
		$beginTime   = $row['beginTime'];
		$description = $row['description'];
		$rrule       = $row['rrule'];
	}
	return $title . ',' . $organiser . ',' . $beginTime . ',' . $description . ',' . $rrule;
	//return "TestTitle" . "," . "Richard" . "," . "3" . "," . "TestDescription" . "," . "FREQ=DAILY";
}

function updatePatientProfile($patient_id, $patient_name, $patient_age, $patient_address, $medical_information, $emergency_contact) {
	// updates patient profile information
    global $pdo;

    $stmt = $pdo->prepare('UPDATE `'. DB_NAME .'`.`'. TABLE_PROFILE .'` '.
		'SET `patient_name` = :patient_name, `patient_age` = :patient_age, `patient_address` = :patient_address, `medical_information` = :medical_information, `emergency_contact` = :emergency_contact '.
        'WHERE `patient_id` = :patient_id');
    $stmt->bindValue(':patient_id', $patient_id);
	$stmt->bindValue(':patient_name', $patient_name);
	$stmt->bindValue(':patient_age', $patient_age);
	$stmt->bindValue(':patient_address', $patient_address);
	$stmt->bindValue(':medical_information', $medical_information);
	$stmt->bindValue(':emergency_contact', $emergency_contact);
    $stmt->execute();
	
	// feedback whether the update was successful
	if ($stmt->rowCount() > 0) {
		return 1;
	} else {
		return 0;
	}
}

function retrieveMyPatients($carer_id) {
	global $pdo;

	$stmt = $pdo->prepare('SELECT `patient_id`, `patient_name` '.
		'FROM `'. TABLE_PROFILE .'` '.
		'WHERE `carer_id` = :carer_id');
	$stmt->bindValue(':carer_id', $carer_id);
	$stmt->execute(); 
	$result = $stmt->fetchAll();

	$patients = array();
	foreach ($result as $row) {
		$patients[] = $row['patient_id'] .','. $row['patient_name'];
	}

	return implode(';', $patients);
}

?>