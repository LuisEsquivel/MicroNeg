
<?php

$server = "localhost";
$user   = "root";
$password = "Batman";
$database = "martin";


$conexion = mysqli_connect($server, $user, $password, $database);

$usuario = $_POST["usuario"];
$contraseña = $_POST["contraseña"];


$insert ="INSERT INTO usuarios VALUES (?,?,?)";
$stman    =$conexion->prepare($insert);
$stman->bind_param('sss', $id,  $usuario, $contraseña );

if($stman->execute()){
    echo "registrado con exito";
}else{
    echo "no se pudo registrar";
}

?>