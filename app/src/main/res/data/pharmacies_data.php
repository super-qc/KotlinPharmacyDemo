<?php

$json_str=file_get_contents('pharmacies_data.json');
$data=json_decode($json_str,true);

$data_json = json_encode($data);
header('Content-type:text/json');
echo $data_json;;


?>