


<?php

$server="localhost";
$user  ="root";
$password ="Batman";
$database ="martin";



$json=array();

$conexion = mysqli_connect($server, $user, $password, $database);

$consulta = "SELECT * FROM pedidos";



$resultado = mysqli_query($conexion, $consulta);



while($registro=mysqli_fetch_array($resultado)){
$result["id"]=$registro['id'];
$result["nombre"]=$registro['nombre'];
$result["precio"]=$registro['precio'];
$result["descripcion"]=$registro['descripcion'];
$result["rutaImagen"]=$registro['rutaImagen'];
$result["direccion"]=$registro['direccion'];
$result["telefono"]=$registro['telefono'];

$json['pedidos'][]=$result;
}

mysqli_close($conexion);

echo json_encode($json);

?>