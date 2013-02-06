<?php

/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['imei'])&&isset($_POST['username'])&&isset($_POST['phonenumber'])&&isset($_POST['phonemodel'])&&isset($_POST['appversion'])&&isset($_POST['androidversion'])) {
    
    $imei = $_POST['imei'];
	$username= $_POST['username'];
	$phonenumber= $_POST['phonenumber'];
	$phonemodel= $_POST['phonemodel'];
	$appversion=  $_POST['appversion'];
	$androidversion= $_POST['androidversion'];
    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

    // mysql inserting a new row
    $result = mysql_query("INSERT INTO userregister(imei,username,phonenumber,phonemodel,appversion,androidversion)VALUES('$imei', '$username','$phonenumber','$phonemodel','$appversion','$androidversion')");

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