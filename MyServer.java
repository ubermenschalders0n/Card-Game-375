
package se375;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MyServer {

	public static CopyOnWriteArrayList<SessionThread> gameSessionThreads = new CopyOnWriteArrayList<>();

	public static void main(String[] args) {
		System.out.println("The CollectFour server is running...");
		ExecutorService pool = Executors.newFixedThreadPool(500);
		try (ServerSocket listener = new ServerSocket(5000)) {
			while (true) {
				pool.execute(new MyServer().new ClientHandler(listener.accept()));
			}
		} catch (Exception e) {
			e.getStackTrace();
		}
	}

	class ClientHandler extends SocketAction {
		String userName;
		int playerNumber = 0;
		boolean isLogin = false;
		boolean isBingo = false;
		boolean isCreating = false;
		boolean isFinding = false;

		ClientHandler(Socket s) throws IOException {
			super(s);
			
		}

		public boolean login() {
			String name,password;
			boolean found=false;
			try {
				send("Enter name: ");
				name = receive();
				send("Enter password: ");
				password = receive();
				found=gameUser.checkData(name, password);
				if(found) {
					gameUser.isLogin(name);
				}
				userName=name;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return found;
		}

		public boolean signUp() {
			String Name,password;	
			try {
				send("Enter new name: ");
				Name = receive();
				send("Enter new password : ");
				password = receive();
				gameUser.insertData(Name, password);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;			
		}

		@Override
		public void run() {
			try {

				while (!isLogin) {
					send("Press L to login or S to sign up and login: ");
					String tempor = receive();
					if (tempor.equalsIgnoreCase("l")) {
						isLogin = login();
						if(!isLogin) {
							send("Couldn't find the inserted data");
						}
					} else if (tempor.equalsIgnoreCase("s")) {
						isLogin = signUp();
					}

				}
				while (!isCreating && !isFinding) {
					send("Press C to create a game lobby or press F to find one: ");
					String temp = receive();
					if (temp.equalsIgnoreCase("c")) {
						createGame();
						isCreating = true;
					} else if (temp.equalsIgnoreCase("f")) {
						findGame();
						isFinding = true;
					}
				}
			} catch (Exception e) {
				e.getStackTrace();
			}
		}

		public void createGame() {
			String sessionName;
			String password;
			int numberOfPlayers;

			try {
				send("Enter new session name: ");
				sessionName = receive();
				logout(sessionName);
				send("Enter new optional password or enter 0 : ");
				password = receive();
				logout(password);
				send("Enter number of players : ");
				numberOfPlayers = Integer.parseInt(receive());
				// gameSession.insertSession(sessionName,password);
				SessionThread createdSession = new SessionThread(socket, sessionName, numberOfPlayers);
				gameSessionThreads.add(createdSession);
				createdSession.playersWithPoints.putIfAbsent(createdSession.playerCnt.incrementAndGet(), 0);
				createdSession.playersWithHands.putIfAbsent(createdSession.playerCnt.get(), new ArrayList<Integer>());

				createdSession.playerThreadsWithPlayerNumbers.putIfAbsent(createdSession.playerCnt.get(), this);
				createdSession.start();
				createdSession.join();

			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		public void logout(String message) {
			try {
				message.toLowerCase();
				if(message.equals("logout")) {
					gameUser.isLogout(userName);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public void findGame() 
		{
			String sessionName;
			String password;
			boolean gameFound = false;

			try {
				send("all available games: ");
				// gameSession.printRs();
				send("enter the name of the session that you want to join:");
				sessionName = receive();
				logout(sessionName);
				for (int i = 0; i < gameSessionThreads.size(); i++) {
					if (gameSessionThreads.get(i).sessionName.equalsIgnoreCase(sessionName)) {
						gameSessionThreads.get(i).playersWithPoints
								.putIfAbsent(gameSessionThreads.get(i).playerCnt.incrementAndGet(), 0);
						gameSessionThreads.get(i).playersWithHands
								.putIfAbsent(gameSessionThreads.get(i).playerCnt.get(), new ArrayList<Integer>());
						gameSessionThreads.get(i).playerThreadsWithPlayerNumbers
								.putIfAbsent(gameSessionThreads.get(i).playerCnt.get(), this);
						// gameSessionThreads.get(i).start();
						// gameSessionThreads.get(i).join();
						gameFound = true;
						break;
					}
				}
				if (gameFound == false) {
					send("game doesn't exist");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}