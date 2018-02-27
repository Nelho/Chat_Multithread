import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Servidor extends Thread { // A classe servidor � um tipo de Thread e � iniciada na instru��o �t.start()�.

	// Atributos est�ticos e de inst�ncias da classe servidor.java.

	private static ArrayList<BufferedWriter> clientes;
	private static ServerSocket server;
	private String nome;
	private Socket con;
	private InputStream in;
	private InputStreamReader inr;
	private BufferedReader bfr;

	// M�todo construtor, que recebe um objeto socket como par�metro
	// e cria um objeto do tipo BufferedReader

	public Servidor(Socket con) {
		this.con = con;
		try {
			in = con.getInputStream();
			inr = new InputStreamReader(in);
			bfr = new BufferedReader(inr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Declara��o do m�todo �run�: toda vez que um cliente novo chega ao servidor,
	// esse m�todo � acionado e alocado numa Thread e tamb�m fica verificando
	// se existe alguma mensagem nova. Caso exista, esta ser� lida e o evento
	// �sentToAll� ser� acionado para enviar a mensagem para os demais usu�rios
	// conectados no chat.

	public void run() {

		try {

			String msg;
			OutputStream ou = this.con.getOutputStream();
			Writer ouw = new OutputStreamWriter(ou);
			BufferedWriter bfw = new BufferedWriter(ouw);
			clientes.add(bfw);
			nome = msg = bfr.readLine();

			while (!"Sair".equalsIgnoreCase(msg) && msg != null) {
				msg = bfr.readLine();
				sendToAll(bfw, msg);
				System.out.println(msg);
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	// Quando um cliente envia uma mensagem, o servidor recebe e manda
	// esta para todos os outros clientes conectados. Para isso
	// � necess�rio percorrer a lista de clientes e mandar uma c�pia da
	// mensagem para cada um.

	public void sendToAll(BufferedWriter bwSaida, String msg) throws IOException {

		BufferedWriter bwS;

		for (BufferedWriter bw : clientes) {
			bwS = (BufferedWriter) bw;
			if (!(bwSaida == bwS)) {
				bw.write(nome + " -> " + msg + "\r\n");
				bw.flush(); // Enquanto voc� est� escrevendo no arquivo, com o m�todo write,
							// essas altera��es n�o est�o realmente no arquivo ainda, quando voc�
							// invoca o flush, voc� diz que quer enviar todos os seu conte�do naquele
							// momento.
			}
		}
	}

	// Declara��o do m�todo main, que ao iniciar o servidor, far� a configura��o do
	// servidor socket
	// e sua respectiva porta. Ele come�a criando uma janela para informar a porta e
	// depois entra no
	// �while(true)�. Na linha �server.accept()� o sistema fica bloqueado at� que um
	// cliente socket
	// se conecte: se ele fizer isso � criada uma nova Thread do tipo servidor.

	public static void main(String[] args) {

		try {
			// Cria os objetos necess�rio para inst�nciar o servidor
			JLabel lblMessage = new JLabel("Porta do Servidor:");
			JTextField txtPorta = new JTextField("12345");
			Object[] texts = { lblMessage, txtPorta };
			JOptionPane.showMessageDialog(null, texts);
			server = new ServerSocket(Integer.parseInt(txtPorta.getText()));
			clientes = new ArrayList<BufferedWriter>();
			JOptionPane.showMessageDialog(null, "Servidor ativo na porta: " + txtPorta.getText());

			while (true) {
				System.out.println("Aguardando conex�o...");
				Socket con = server.accept();
				System.out.println("Cliente conectado...");
				Thread t = new Servidor(con);
				t.start();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}// Fim do m�todo main
}
