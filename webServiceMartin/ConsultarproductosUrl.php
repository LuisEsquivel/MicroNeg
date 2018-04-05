<?PHP
$hostname_localhost ="localhost";
$database_localhost ="martin";
$username_localhost ="root";
$password_localhost ="admin13";

$json=array();

	if(isset($_GET["id"])){
		$id=$_GET["id"];
				
		$conexion = mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);

		$consulta="select * from productos where id= '{$id}'";
		$resultado=mysqli_query($conexion,$consulta);
			
		if($registro=mysqli_fetch_array($resultado)){
			$result["id"]=$registro['id'];
			$result["nombre"]=$registro['nombre'];
			$result["descripcion"]=$registro['descripcion'];
            $result["precio"]=$registro['precio'];
			$result["rutaImagen"]=$registro['rutaImagen'];
			$json['productos'][]=$result;
		}else{
			$resultar["id"]=0;
			$resultar["nombre"]='no registra';
			$resultar["descripcion"]='no registra';
            $resultar["precio"]='no registra';
			$result["rutaImagen"]='no registra';
			$json['productos'][]=$resultar;
		}
		
		mysqli_close($conexion);
		echo json_encode($json);
	}
	else{
		$resultar["success"]=0;
		$resultar["message"]='Ws no Retorna';
		$json['productos'][]=$resultar;
		echo json_encode($json);
	}
?>
