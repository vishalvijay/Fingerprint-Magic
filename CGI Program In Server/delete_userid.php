<?php

/*
 * Following code will delete a product from table
 * A product is identified by product id (pid)
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['imei'])) {
    $imei = $_POST['imei'];

    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

    // mysql update row with matched pid
    $result = mysql_query("DELETE FROM currentname WHERE imei = $imei");
    
    // check if row deleted or not
    if (mysql_affected_rows() > 0) {
        // successfully updated
        $response["success"] = 1;
        // echoing JSON response
        echo json_encode($response);
    } else {
        // no product found
        $response["success"] = 0;

        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;

    // echoing JSON response
    echo json_encode($response);
}
?>