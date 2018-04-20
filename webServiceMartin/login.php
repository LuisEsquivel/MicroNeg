

<?php


$server = "localhost";
$user   = "root";
$password = "Batman";
$database = "martin";


$conexion = mysqli_connect($server, $user, $password, $database);



$usuario = $_POST["usuario"];
$contraseña = $_POST["contraseña"];



$consulta = "SELECT * FROM usuarios WHERE usuario='$usuario' AND contraseña='$contraseña'";


$resultado = mysqli_query($conexion, $consulta);

if(isset($resultado)){   
         echo "Bienvenido";
}else{
    echo "error";
}

mysqli_close($conexion);

?>
