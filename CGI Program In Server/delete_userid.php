<?php

$response = array();

if (isset($_POST['imei'])) {
    $imei = $_POST['imei'];

    require_once __DIR__ . '/db_connect.php';

    $db = new DB_CONNECT();

    $result = mysql_query("DELETE FROM currentname WHERE imei = $imei");

    if (mysql_affected_rows() > 0) {
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
