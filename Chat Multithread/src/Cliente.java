import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import javax.swing.*;

public class Cliente extends JFrame implements ActionListener, KeyListener {

	// Para a construção do formulário foram usados objetos do pacote javax.swing.

	private static final long serialVersionUID = 1L;
	private JTextArea texto;
	private JTextField txtMsg;
	private JButton btnSend;
	private JButton btnSair;
	private JLabel lblHistorico;
	private JLabel lblMsg;
	private JPanel pnlContent;
	private Socket socket;
	private OutputStream ou;
	private Writer ouw;
	private BufferedWriter bfw;
	private JTextField txtIP;
	private JTextField txtPorta;
	private JTextField txtNome;

	// Ao executar a classe cliente aparecerá uma tela para o usuário informar
	// alguns parâmetros como o IP do servidor, a porta e o nome que será visto
	// para os demais usuários no chat. No código está definido como padrão o
	// IP 127.0.0.1, porta 12345 e nome cliente.

	// a classe herda de JFrame, possibilitando a criação de formulários e
	// implementação
	// das interfaces ActionListener e KeyListener para prover ações nos botões e
	// ações das teclas, respectivamente.

	// declaração do método construtor, que verifica os objetos sendo instanciados
	// para a construção da tela do chat. Cada cliente deverá ser uma instância
	// independente.

	public Cliente() throws IOException {
		JLabel lblMessage = new JLabel("Verificar!");
		txtIP = new JTextField("127.0.0.1");
		txtPorta = new JTextField("12345");
		txtNome = new JTextField("Cliente");
		Object[] texts = { lblMessage, txtIP, txtPorta, txtNome };
		JOptionPane.showMessageDialog(null, texts);
		pnlContent = new JPanel();
		texto = new JTextArea(10, 20);
		texto.setEditable(false);
		texto.setBackground(new Color(240, 240, 240));
		txtMsg = new JTextField(20);
		lblHistorico = new JLabel("Histórico");
		lblMsg = new JLabel("Mensagem");
		btnSend = new JButton("Enviar");
		btnSend.setToolTipText("Enviar Mensagem");
		btnSair = new JButton("Sair");
		btnSair.setToolTipText("Sair do Chat");
		btnSend.addActionListener(this);
		btnSair.addActionListener(this);
		btnSend.addKeyListener(this);
		txtMsg.addKeyListener(this);
		JScrollPane scroll = new JScrollPane(texto);
		texto.setLineWrap(true);
		pnlContent.add(lblHistorico);
		pnlContent.add(scroll);
		pnlContent.add(lblMsg);
		pnlContent.add(txtMsg);
		pnlContent.add(btnSair);
		pnlContent.add(btnSend);
		pnlContent.setBackground(Color.LIGHT_GRAY);
		texto.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));
		txtMsg.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));
		setTitle(txtNome.getText());
		setContentPane(pnlContent);
		setLocationRelativeTo(null);
		setResizable(false);
		setSize(250, 300);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/***
	 * Método usado para conectar no server socket, retorna IO Exception caso dê
	 * algum erro.
	 * 
	 * @throws IOException
	 */
	public void conectar() throws IOException {

		socket = new Socket(txtIP.getText(), Integer.parseInt(txtPorta.getText()));
		ou = socket.getOutputStream();
		ouw = new OutputStreamWriter(ou);
		bfw = new BufferedWriter(ouw);
		bfw.write(txtNome.getText() + "\r\n");
		bfw.flush();
	}

	// Método usado para enviar mensagens do cliente para o servidor socket.
	// Assim, toda vez que ele escrever uma mensagem e apertar o botão “Enter”,
	// esta será enviada para o servidor.

	public void enviarMensagem(String msg) throws IOException {

		if (msg.equals("Sair")) {
			bfw.write("Desconectado \r\n");
			texto.append("Desconectado \r\n");
		} else {
			bfw.write(msg + "\r\n");
			texto.append(txtNome.getText() + " diz -> " + txtMsg.getText() + "\r\n");
		}
		bfw.flush();
		txtMsg.setText("");
	}

	// Método usado para escutar (receber) mensagens do servidor. Toda vez que
	// alguém
	// enviar uma, o método será processado pelo servidor e envia para todos os
	// clientes
	// conectados.

	public void escutar() throws IOException {

		InputStream in = socket.getInputStream();
		InputStreamReader inr = new InputStreamReader(in);
		BufferedReader bfr = new BufferedReader(inr);
		String msg = "";

		while (!"Sair".equalsIgnoreCase(msg))

			if (bfr.ready()) {
				msg = bfr.readLine();
				if (msg.equals("Sair"))
					texto.append("Servidor caiu! \r\n");
				else
					texto.append(msg + "\r\n");
			}
	}

	// Usado para desconectar do server socket. Nele o sistema apenas
	// fecha os streams de comunicação.

	public void sair() throws IOException {

		enviarMensagem("Sair");
		bfw.close();
		ouw.close();
		ou.close();
		socket.close();
	}

	// Método usado para receber as ações dos botões dos usuários. Nele foi feito
	// um chaveamento: se o usuário pressionar o botão “send” então será enviada
	// uma mensagem, senão será encerrado o chat.

	@Override
	public void actionPerformed(ActionEvent e) {

		try {
			if (e.getActionCommand().equals(btnSend.getActionCommand()))
				enviarMensagem(txtMsg.getText());
			else if (e.getActionCommand().equals(btnSair.getActionCommand()))
				sair();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	// Declaração do método keyPressed.
	// Este metódo é acionado quando o usuário pressiona “Enter”, verificando 
	// se o key code é o Enter. Caso seja, a mensagem é enviada para o servidor.
	
	
	@Override
	public void keyPressed(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			try {
				enviarMensagem(txtMsg.getText());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}

}
