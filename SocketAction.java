package se375;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// this class is for code reuse
class SocketAction extends Thread {
	private BufferedReader inStream = null;
	protected PrintWriter outStream = null;
	protected Socket socket = null;

	public SocketAction(Socket sock) {
		super("SocketAction");
		try {
			socket = sock;
			inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outStream = new PrintWriter(socket.getOutputStream(), true);

		} catch (IOException e) {
			System.out.println("Couldn't initialize SocketAction: " + e);
			System.exit(1);
		}
	}

	public void send(String s) {
		outStream.println(s);
	}

	public String receive() throws IOException {
		return inStream.readLine();
	}

	public void closeConnections() {
		try {
			socket.close();
			socket = null;
		} catch (IOException e) {
			System.out.println("Couldn't close socket: " + e);
		}
	}

	public boolean isConnected() {
		return ((inStream != null) && (outStream != null) && (socket != null));
	}

	protected void closeSocket() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("Couldn't close socket: " + e);
			}
			socket = null;
		}
	}
}
