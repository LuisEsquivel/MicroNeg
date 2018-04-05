<?PHP
$hostname_localhost="localhost";
$database_localhost="martin";
$username_localhost="root";
$password_localhost="admin13";

$conexion=mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);

    $id= $_POST["id"];
	$nombre = $_POST["nombre"];
	$precio = $_POST["precio"];
    $descripcion = $_POST["descripcion"];
    $imagen = $_POST["imagen"];

    $rutaImagen = "imagenes/$nombre.jpg";

	$url = "imagenes/".$nombre.".jpg";
    //$url = "http://$hostname_localhost/MicroNeg/$rutaImagen";

	file_put_contents($rutaImagen,base64_decode($imagen));
	$bytesArchivo=file_get_contents($rutaImagen);

	$sql="UPDATE productos SET nombre= ?,precio=?, descripcion=?, imagen=?, rutaImagen=? WHERE id=?";
	$stm=$conexion->prepare($sql);
	$stm->bind_param('ssssss',$nombre,$precio,$descripcion,$bytesArchivo,$url,$id);
		
	if($stm->execute()){
		echo "Producto actualizado";
	}else{
		echo "no se Actualizo";
	}
	mysqli_close($conexion);
?>
