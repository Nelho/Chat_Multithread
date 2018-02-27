import java.io.IOException;

public class Chat {

	// É criado apenas um cliente e são configurados os métodos conectar e escutar.
	
	public static void main(String[] args) throws IOException{

		Cliente app = new Cliente();
		app.conectar();
		app.escutar();

	}

}
