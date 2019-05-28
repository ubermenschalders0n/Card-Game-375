package se375;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class MyClientConnector extends SocketAction {
	static final int PORTNUM = 5000;
	private String userName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public MyClientConnector(String userName) throws IOException {
		super(new Socket("localhost", PORTNUM));
		this.userName = userName;
	}

	public static void main(String[] args) {
		String userName = "arda";
		MyClientConnector client = null;
		Scanner scan = new Scanner(System.in);
		try {
			client = new MyClientConnector(userName);
			new Thread(client).start();
			// client.send(userName);// to set the handler thread's userName

		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {
			System.out.print("> ");
			// read message from user
			String msg = scan.nextLine();
			// logout if message is LOGOUT
			if (msg.equalsIgnoreCase("LOGOUT")) {
				client.send("LOGOUT");
				break;
			}

			// regular text message
			else {
				client.send(msg);
			}
		}
		// close resource
		scan.close();
		// client completed its job. disconnect client.
		client.closeConnections();

	}

	public void run() {
		while (true) {
			try {
				// read the message form the input datastream
				String msg = receive();
				// print the message
				System.out.println(msg);
				System.out.print("> ");
			} catch (IOException e) {
				System.out.println("Server has closed the connection: " + e);
				break;
			}

		}
	}

}