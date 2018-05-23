package chatTCP;

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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Cliente extends JFrame implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;
	private JTextArea texto;
	private JTextField mensagem;
	private JButton btnSend;
	private JLabel lblHistorico;
	private JLabel lblMsg;
	private JPanel pnlContent;
	private Socket socket;
	private OutputStream outputStream;
	private Writer writer;
	private BufferedWriter bufferedWriter;
	private JTextField IP;
	private JTextField porta;
	private JTextField nomeUsuario;

	public Cliente() throws IOException {
		JLabel lblMessage = new JLabel("Dados da usuário");
		IP = new JTextField("127.0.0.1");
		porta = new JTextField("12345");
		nomeUsuario = new JTextField("Cliente");
		Object[] texts = { lblMessage, IP, porta, nomeUsuario };
		JOptionPane.showMessageDialog(null, texts);
		pnlContent = new JPanel();
		texto = new JTextArea(10, 20);
		texto.setEditable(false);
		texto.setBackground(new Color(240, 240, 240));
		mensagem = new JTextField(20);
		lblHistorico = new JLabel("Chat FLF");
		lblMsg = new JLabel("Mensagem");
		btnSend = new JButton("Enviar");
		btnSend.setToolTipText("Enviar Mensagem");
		btnSend.addActionListener(this);
		btnSend.addKeyListener(this);
		mensagem.addKeyListener(this);
		JScrollPane scroll = new JScrollPane(texto);
		texto.setLineWrap(true);
		pnlContent.add(lblHistorico);
		pnlContent.add(scroll);
		pnlContent.add(lblMsg);
		pnlContent.add(mensagem);
		pnlContent.add(btnSend);
		pnlContent.setBackground(Color.GRAY);
		texto.setBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.BLACK));
		mensagem.setBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.BLACK));
		setTitle(nomeUsuario.getText());
		setContentPane(pnlContent);
		setLocationRelativeTo(null);
		setResizable(false);
		setSize(250, 300);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/***
	 * Método usado para conectar no server socket, retorna IO Exception caso de
	 * algum erro.
	 * 
	 * @throws IOException
	 */
	public void conectar() throws IOException {
		socket = new Socket(IP.getText(), Integer.parseInt(porta.getText()));
		outputStream = (OutputStream) socket.getOutputStream();
		writer = new OutputStreamWriter(outputStream);
		bufferedWriter = new BufferedWriter(writer);
		bufferedWriter.write(nomeUsuario.getText() + "\r\n");
		bufferedWriter.flush();
	}

	/***
	 * Método usado para enviar mensagem para o server socket
	 * 
	 * @param msg
	 *            do tipo String
	 * @throws IOException
	 *             retorna IO Exception
	 */
	public void enviarMensagem(String msg) throws IOException {
		if (msg != null) {
			bufferedWriter.write(msg + "\r\n");
			texto.append(nomeUsuario.getText() + " --> " + mensagem.getText() + "\r\n");
		}
		bufferedWriter.flush();
		mensagem.setText("");
	}

	/**
	 * Método usado para receber mensagem do servidor
	 * 
	 * @throws IOException
	 *             retorna IO Exception.
	 */
	public void escutarServidor() throws IOException {
		InputStream inputStream = (InputStream) socket.getInputStream();
		InputStreamReader inr = new InputStreamReader(inputStream);
		BufferedReader bfr = new BufferedReader(inr);
		String msg = "";
		while (!"Sair".equalsIgnoreCase(msg))
			if (bfr.ready()) {
				msg = bfr.readLine();
				texto.append(msg + "\r\n");
			}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getActionCommand().equals(btnSend.getActionCommand()))
				enviarMensagem(mensagem.getText());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			try {
				enviarMensagem(mensagem.getText());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	/**
	 * Criar um novo usuário, conectar e escutar ele.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Cliente usuario = new Cliente();
		usuario.conectar();
		usuario.escutarServidor();
	}
}
