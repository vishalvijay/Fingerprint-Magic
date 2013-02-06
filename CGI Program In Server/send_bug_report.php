<?php

/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['imei'])&&isset($_POST['phonemodel'])&&isset($_POST['appversion'])&&isset($_POST['androidversion'])&&isset($_POST['bug'])) {
    
    $imei = $_POST['imei'];
	$phonemodel= $_POST['phonemodel'];
	$appversion=  $_POST['appversion'];
	$androidversion= $_POST['androidversion'];
	$bug= $_POST['bug'];
    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

    // mysql inserting a new row
    $result = mysql_query("INSERT INTO bugreport(imei,phonemodel,appversion,androidversion,bug)VALUES('$imei','$phonemodel','$appversion','$androidversion','$bug')");

    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;

        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        
        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;

    // echoing JSON response
    echo json_encode($response);
}
?>