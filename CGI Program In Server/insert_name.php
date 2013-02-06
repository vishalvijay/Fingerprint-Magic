<?php

$response = array();

if (isset($_POST['imei'])&&isset($_POST['name'])) {
    
    $imei = $_POST['imei'];
	$name = $_POST['name'];

    require_once __DIR__ . '/db_connect.php';

    $db = new DB_CONNECT();

	$deleteResult = mysql_query("DELETE FROM currentname WHERE imei = $imei");
    $result = mysql_query("INSERT INTO currentname(imei,name)VALUES('$imei','$name')");

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
