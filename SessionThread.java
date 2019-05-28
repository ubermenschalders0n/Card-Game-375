package se375;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class SessionThread extends SocketAction {
	int MaxNumberOfPlayers;
	String sessionName;
	int firstBingo;
	AtomicInteger playerCnt = new AtomicInteger(0);

	public SessionThread(Socket s, String sessionName, int MaxNumberOfPlayers) {
		super(s);
		this.sessionName = sessionName;
		this.MaxNumberOfPlayers = MaxNumberOfPlayers;
	}

	CopyOnWriteArrayList<Integer> bingoQueue = new CopyOnWriteArrayList<>();
	ConcurrentHashMap<Integer, Integer> playersWithPoints = new ConcurrentHashMap<>();
	ConcurrentHashMap<Integer, ArrayList<Integer>> playersWithHands = new ConcurrentHashMap<>();
	ConcurrentHashMap<Integer, MyServer.ClientHandler> playerThreadsWithPlayerNumbers = new ConcurrentHashMap<>();

	@Override
	public void run() {
		playerThreadsWithPlayerNumbers.get(1).send("Game Session Created waiting for Other players...");
		boolean everyoneJoined = false;

		while (!everyoneJoined) {// wait for players

			if (playerCnt.intValue() == MaxNumberOfPlayers)
				everyoneJoined = true;
		}

		sendToAll("Everyone joined. starting the game...");

		for (int i = 1; i <= playerThreadsWithPlayerNumbers.size(); i++) {
			playerThreadsWithPlayerNumbers.get(i).send("you are player " + i);
			playerThreadsWithPlayerNumbers.get(i).playerNumber = i;
		}

		String temp = "";

		while (!checkPointsofAllPLayers()) {
			Stack deck = createDeck(playerCnt.get());

			deck = deckShuffle(deck);

			dealInitialHands(deck);

			sendCardinfoToAll();

			while (!checkAllHandsForBingo()) {// buraya lock gerekebilir emin değilim
				sendToAll("Choose your card to give: ");
				for (int i = 1; i <= playerThreadsWithPlayerNumbers.size(); i++) {
					try {
						while (true) {
							String tempo = playerThreadsWithPlayerNumbers.get(i).receive();
							if (Pattern.matches("[0-9]+", tempo)
									&& playersWithHands.get(i).contains(Integer.parseInt(tempo))) {
								Integer card = Integer.parseInt(tempo);
								sendCardFromOneToAnother(i, card);
								break;
							} else
								playerThreadsWithPlayerNumbers.get(i).send("That is an invalid entry");

						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				sendCardinfoToAll();
			}
			playerThreadsWithPlayerNumbers.get(firstBingo)
					.send("you have all same cards in your hand.Write bingo to win: ");
			try {
				temp = playerThreadsWithPlayerNumbers.get(firstBingo).receive();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (temp.equalsIgnoreCase("bingo")) {
				bingoQueue.add(0);
				bingoQueue.add(firstBingo);
			}

			for (int i = 1; i <= playerThreadsWithPlayerNumbers.size(); i++) {
				if (i != firstBingo)
					playerThreadsWithPlayerNumbers.get(i)
							.send("player " + firstBingo + " said bingo. Write bingo  to get points");
			}
			// bingo diyen hariç userlardan receive
			for (int i = 1; i <= playerCnt.intValue(); i++) {
				try {

					String addedPlayer;
					if (i != firstBingo) {
						while (true) {
							addedPlayer = (playerThreadsWithPlayerNumbers.get(i).receive());
							if (addedPlayer.equalsIgnoreCase("bingo")) {
								bingoQueue.add(i);
								break;
							} else
								playerThreadsWithPlayerNumbers.get(i).send("you must say bingo");
						}

					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			assignPoints();
			displayPoints();
			emptyTheHands();
			bingoQueue.clear();
			firstBingo = 0;
		} // game over

		displayPoints();// final points

	}

	public void emptyTheHands() {
		for (int i = 1; i <= playersWithHands.size(); i++) {
			playersWithHands.get(i).clear();
		}
	}

	public void displayPoints() {
		sendToAll("points: ");
		for (int i = 1; i <= playerCnt.intValue(); i++) {
			sendToAll("player " + i + ": " + playersWithPoints.get(i));
			// database e bağlanacak falan filan
		}
	}

	public void assignPoints() {
		int points = playerCnt.intValue();
		for (int i = 1; i <= playerCnt.intValue(); i++) {
			playersWithPoints.replace(bingoQueue.get(i), playersWithPoints.get(i) + points);
			// database'e de yazılacak falan filan
			points--;
		}
	}

	public boolean checkPointsofAllPLayers() {
		boolean gameOver = false;
		for (int i = 1; i <= playersWithPoints.size(); i++) {
			if (playersWithPoints.get(i) >= playerCnt.intValue() * playerCnt.intValue()) {
				sendToAll("player " + i + "has won. game over.");
				gameOver = true;
			}

		}
		return gameOver;
	}

	public void sendCardinfoToAll() {
		for (int i = 1; i <= playersWithHands.size(); i++) {
			playerThreadsWithPlayerNumbers.get(i).send("your hand:");
			for (int j = 0; j < playersWithHands.get(i).size(); j++) {
				playerThreadsWithPlayerNumbers.get(i).send(Integer.toString(playersWithHands.get(i).get(j)));
			}
		}
	}

	public void sendCardFromOneToAnother(int giver, Integer card) {
		if (giver == playerCnt.intValue()) {
			playersWithHands.get(1).add(card);
			playersWithHands.get(playerCnt.intValue()).remove(card);
		} else {
			playersWithHands.get(giver + 1).add(card);
			playersWithHands.get(giver).remove(card);
		}
	}

	public void sendToAll(String msg) {
		for (int i = 1; i <= playerThreadsWithPlayerNumbers.size(); i++) {
			playerThreadsWithPlayerNumbers.get(i).send(msg);
		}
	}

	public void dealInitialHands(Stack deck) {
		for (int i = 1; i <= playersWithHands.size(); i++) {
			for (int j = 0; j < 4; j++) {
				playersWithHands.get(i).add(Integer.parseInt(deck.pop().toString()));
			}
		}
	}

	public boolean checkAllHandsForBingo() {
		boolean isBingo = false;
		for (int i = 1; i <= playersWithHands.size(); i++) {
			int temp = playersWithHands.get(i).get(0);

			for (int j = 1; j < playersWithHands.get(i).size(); j++) {
				if (j == playersWithHands.get(i).size() - 1) {
					if (playersWithHands.get(i).get(j) == temp) {
						isBingo = true;
						firstBingo = i;
						break;
					}
				} else if (playersWithHands.get(i).get(j) == temp) {
					temp = playersWithHands.get(i).get(j);
					continue;
				} else
					break;
			}

		}
		return isBingo;
	}

	public Stack deckShuffle(Stack deck) {
		Collections.shuffle(deck);
		return deck;
	}

	public Stack createDeck(int numberOfPlayers) {
		Stack deck = new Stack();
		for (int i = 0; i < numberOfPlayers; i++) {
			for (int j = 0; j < 4; j++) {
				deck.add(i + 1);
			}
		}
		Collections.shuffle(deck);
		return deck;
	}
}
