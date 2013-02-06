<?php
$response = array();

if (isset($_POST['imei'])&&isset($_POST['username'])&&isset($_POST['phonenumber'])&&isset($_POST['phonemodel'])&&isset($_POST['appversion'])&&isset($_POST['androidversion'])) {
    
    $imei = $_POST['imei'];
	$username= $_POST['username'];
	$phonenumber= $_POST['phonenumber'];
	$phonemodel= $_POST['phonemodel'];
	$appversion=  $_POST['appversion'];
	$androidversion= $_POST['androidversion'];
    require_once __DIR__ . '/db_connect.php';

    $db = new DB_CONNECT();

    $result = mysql_query("INSERT INTO userregister(imei,username,phonenumber,phonemodel,appversion,androidversion)VALUES('$imei', '$username','$phonenumber','$phonemodel','$appversion','$androidversion')");

    if ($result) {
        $response["success"] = 1;
        echo json_encode($response);
    } else {
        $response["success"] = 0;
        echo json_encode($response);
    }
} else {
    $response["success"] = 0;
    echo json_encode($response);
}
?>
