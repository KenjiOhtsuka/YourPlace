<?php
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
  $mysqli = new mysqli(
    "DB_HOST_NAME", // host
    "DB_USER",      // user
    "DB_PASSWORD",          // password,
    "DB_NAME");
  if ($mysqli->connect_errno) {
    echo "Failed to connect";
  } else {
    // try {
    $mysqli->set_charset("utf8");
     // escape
    var_dump($_POST);
    $device       = $mysqli->real_escape_string($_POST['device']);
    $model        = $mysqli->real_escape_string($_POST['model']);
    $sdk_version  = $mysqli->real_escape_string($_POST['sdk_version']);
    $version_code = $mysqli->real_escape_string($_POST['version_code']);
    $stack_trace  = $mysqli->real_escape_string($_POST['stack_trace']);
    $mysqli->query(
      'INSERT INTO yourplace_bug_reports(device, model, sdk_version, version_code, stack_trace, created_at) '
      .'VALUES(\''.$device.'\',\''.$model.'\','.$sdk_version.','.$version_code
      .',\''.$stack_trace.'\',\''.date('Y-m-d H:i:s').'\')');
    $mysqli->close();
  }
} else {
  echo <<< EOT
<!DOCTYPE html>
<html>
<head>
  <meta charset='utf-8'/>
  <title>準備中</title>
  <meta name="robots" content="nofollow"/>
  <meta name="robots" content="noindex"/>
</head>
<body>
  <p>ただいま準備中です。</p>
</body>
</html>
EOT;
}
