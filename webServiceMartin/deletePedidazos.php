



<?PHP
$hostname_localhost="localhost";
$database_localhost="martin";
$username_localhost="root";
$password_localhost="Batman";

	if(isset($_GET["id"])){
		$id=$_GET["id"];
				
		$conexion = mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);
		$sql="DELETE FROM pedidos WHERE id= ? ";
		$stm=$conexion->prepare($sql);
		$stm->bind_param('i',$id);
			
		if($stm->execute()){
			echo "Producto eliminado";
		}else{
			echo "Producto no eliminado";
		}
		
		mysqli_close($conexion);
	}
	else{
		echo "No Existe";
	}
?>