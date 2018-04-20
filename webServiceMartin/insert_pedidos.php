<?php

$server = "localhost";
$user   = "root";
$password = "Batman";
$database = "martin";


$conexion = mysqli_connect($server, $user, $password, $database);

$ud = $_POST["id"];
$nombre = $_POST["nombre"];
$precio = $_POST["precio"];
$imagen = $_POST["imagen"];
$descripcion = $_POST["descripcion"];
$direccion = $POST["direccion"];
$telefono = $POST["telefono"];


$rutaImagen = "imagenes/$nombre.jpg";

$url = "http://$server//martin/$rutaImagen";
    
file_put_contents($rutaImagen, base64_decode($imagen));
$bytesArchivo = file_get_contents($rutaImagen);

$insert ="INSERT INTO pedidos(id, nombre, precio ,imagen, rutaImagen, descripcion, direccion, telefono) VALUES (?,?,?,?,?,?,?,?)";
$stm    =$conexion->prepare($insert);
$stm->bind_param('ssssssss', $id, $nombre, $precio, $bytesArchivo, $rutaImagen,$descripcion, $direccion, $telefono );

if($stm->execute()){
    echo "pedido registrado";
}else{
    echo "no se pudo registrar";
}

?>