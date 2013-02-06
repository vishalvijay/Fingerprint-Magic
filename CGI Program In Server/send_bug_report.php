<?php
$response = array();
if (isset($_POST['imei'])&&isset($_POST['phonemodel'])&&isset($_POST['appversion'])&&isset($_POST['androidversion'])&&isset($_POST['bug'])) {
    
    $imei = $_POST['imei'];
	$phonemodel= $_POST['phonemodel'];
	$appversion=  $_POST['appversion'];
	$androidversion= $_POST['androidversion'];
	$bug= $_POST['bug'];
	
    require_once __DIR__ . '/db_connect.php';
    $db = new DB_CONNECT();
    $result = mysql_query("INSERT INTO bugreport(imei,phonemodel,appversion,androidversion,bug)VALUES('$imei','$phonemodel','$appversion','$androidversion','$bug')");
    
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
