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

public class Servidor extends Thread { // A classe servidor é um tipo de Thread e é iniciada na instrução “t.start()”.

	// Atributos estáticos e de instâncias da classe servidor.java.

	private static ArrayList<BufferedWriter> clientes;
	private static ServerSocket server;
	private String nome;
	private Socket con;
	private InputStream in;
	private InputStreamReader inr;
	private BufferedReader bfr;

	// Método construtor, que recebe um objeto socket como parâmetro
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

	// Declaração do método “run”: toda vez que um cliente novo chega ao servidor,
	// esse método é acionado e alocado numa Thread e também fica verificando
	// se existe alguma mensagem nova. Caso exista, esta será lida e o evento
	// “sentToAll” será acionado para enviar a mensagem para os demais usuários
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
	// é necessário percorrer a lista de clientes e mandar uma cópia da
	// mensagem para cada um.

	public void sendToAll(BufferedWriter bwSaida, String msg) throws IOException {

		BufferedWriter bwS;

		for (BufferedWriter bw : clientes) {
			bwS = (BufferedWriter) bw;
			if (!(bwSaida == bwS)) {
				bw.write(nome + " -> " + msg + "\r\n");
				bw.flush(); // Enquanto você está escrevendo no arquivo, com o método write,
							// essas alterações não estão realmente no arquivo ainda, quando você
							// invoca o flush, você diz que quer enviar todos os seu conteúdo naquele
							// momento.
			}
		}
	}

	// Declaração do método main, que ao iniciar o servidor, fará a configuração do
	// servidor socket
	// e sua respectiva porta. Ele começa criando uma janela para informar a porta e
	// depois entra no
	// “while(true)”. Na linha “server.accept()” o sistema fica bloqueado até que um
	// cliente socket
	// se conecte: se ele fizer isso é criada uma nova Thread do tipo servidor.

	public static void main(String[] args) {

		try {
			// Cria os objetos necessário para instânciar o servidor
			JLabel lblMessage = new JLabel("Porta do Servidor:");
			JTextField txtPorta = new JTextField("12345");
			Object[] texts = { lblMessage, txtPorta };
			JOptionPane.showMessageDialog(null, texts);
			server = new ServerSocket(Integer.parseInt(txtPorta.getText()));
			clientes = new ArrayList<BufferedWriter>();
			JOptionPane.showMessageDialog(null, "Servidor ativo na porta: " + txtPorta.getText());

			while (true) {
				System.out.println("Aguardando conexão...");
				Socket con = server.accept();
				System.out.println("Cliente conectado...");
				Thread t = new Servidor(con);
				t.start();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}// Fim do método main
}
