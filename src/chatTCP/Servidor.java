package chatTCP;

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

public class Servidor extends Thread {

	private static ArrayList<BufferedWriter> clientes;
	private static ServerSocket server;
	private String nomeUsuario;
	private Socket socket;
	private InputStream inputStream;
	private InputStreamReader inputStreamReader;
	private BufferedReader bufferedReader;

	/**
	 * Método construtor da class Servidor
	 * 
	 * @param socket
	 *            do tipo Socket
	 */
	public Servidor(Socket socket) {
		this.socket = socket;
		try {
			inputStream = socket.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream);
			bufferedReader = new BufferedReader(inputStreamReader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			String msg;
			OutputStream outputStream = this.socket.getOutputStream();
			Writer writer = new OutputStreamWriter(outputStream);
			BufferedWriter bfw = new BufferedWriter(writer);
			clientes.add(bfw);
			nomeUsuario = msg = bufferedReader.readLine();

			while (msg != null) {
				msg = bufferedReader.readLine();
				sendToAll(bfw, msg);
				System.out.println(msg);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * Método usado para enviar mensagem para todos os clients
	 * 
	 * @param bwSaida
	 *            do tipo BufferedWriter
	 * @param msg
	 *            do tipo String
	 * @throws IOException
	 */
	public void sendToAll(BufferedWriter bwSaida, String msg) throws IOException {
		BufferedWriter bufferedWriter;
		for (BufferedWriter bw : clientes) {
			bufferedWriter = (BufferedWriter) bw;
			if (!(bwSaida == bufferedWriter)) {
				bw.write(nomeUsuario + " --> " + msg + "\r\n");
				bw.flush();
			}
		}
	}

	/***
	 * Instância o servidor
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			JLabel lblMessage = new JLabel("Porta do Servidor:");
			JTextField porta = new JTextField("12345");
			Object[] texto = { lblMessage, porta };
			JOptionPane.showMessageDialog(null, texto);
			server = new ServerSocket(Integer.parseInt(porta.getText()));
			clientes = new ArrayList<BufferedWriter>();
			JOptionPane.showMessageDialog(null, "Servidor ativo na porta: " + porta.getText());

			while (true) {
				System.out.println("Aguardando conexão...");
				Socket socket = server.accept();
				System.out.println("Cliente conectado...");
				Thread t = new Servidor(socket);
				t.start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
