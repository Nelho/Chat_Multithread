import java.io.IOException;

public class Chat {

	// � criado apenas um cliente e s�o configurados os m�todos conectar e escutar.
	
	public static void main(String[] args) throws IOException{

		Cliente app = new Cliente();
		app.conectar();
		app.escutar();

	}

}
