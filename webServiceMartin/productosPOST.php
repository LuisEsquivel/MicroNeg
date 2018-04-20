


<?php

$server = "localhost";
$user   = "root";
$password = "Batman";
$database = "martin";


$conexion = mysqli_connect($server, $user, $password, $database);

$nombre = $_POST["nombre"];
$precio = $_POST["precio"];
$descripcion = $_POST["descripcion"];
$imagen = $_POST["imagen"];
 
$rutaImagen = "imagenes/$nombre.jpg";

$url = "http://$server//martin/$rutaImagen";
    
file_put_contents($rutaImagen, base64_decode($imagen));
$bytesArchivo = file_get_contents($rutaImagen);

$insert ="INSERT INTO productos(id, nombre, precio ,imagen, rutaImagen, descripcion) VALUES (?,?,?,?,?,?)";
$stm    =$conexion->prepare($insert);
$stm->bind_param('ssssss', $id, $nombre, $precio, $bytesArchivo, $rutaImagen,$descripcion );

if($stm->execute()){
    echo "producto registrado";
}else{
    echo "no se pudo registrar";
}

?>