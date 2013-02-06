<?php


$response = array();

require_once __DIR__ . '/db_connect.php';

$db = new DB_CONNECT();

if (isset($_POST["imei"])) {
    $imei = $_POST['imei'];

    $result = mysql_query("SELECT *FROM currentname WHERE imei = $imei");

    if (!empty($result)) {

        if (mysql_num_rows($result) > 0) {

            $result = mysql_fetch_array($result);
            $response["success"] = 1;
			$response["name"]=$result["name"];

            echo json_encode($response);
        } else {
            $response["success"] = 0;
            echo json_encode($response);
        }
    } else {
        $response["success"] = 0;
        echo json_encode($response);
    }
} else {
    $response["success"] = 0;
    echo json_encode($response);
}
?>
