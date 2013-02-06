<?php

/*
 * Following code will get single product details
 * A product is identified by product id (pid)
 */

// array for JSON response
$response = array();


// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();

// check for post data
if (isset($_POST["imei"])) {
    $imei = $_POST['imei'];

    // get a product from products table
    $result = mysql_query("SELECT *FROM currentname WHERE imei = $imei");

    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {

            $result = mysql_fetch_array($result);
            // success
            $response["success"] = 1;
			$response["name"]=$result["name"];

            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
            $response["success"] = 0;

            // echo no users JSON
            echo json_encode($response);
        }
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