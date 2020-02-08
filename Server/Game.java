package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Game implements Runnable, Constants {
	// X sockets
	private Socket xSocket;
	private PrintWriter xSocketOut;
	private BufferedReader xSocketIn;

	// O sockets
	private Socket oSocket;
	private PrintWriter oSocketOut;
	private BufferedReader oSocketIn;

	private Board theBoard;
	private Player xPlayer;
	private Player oPlayer;

	public Game(Socket xSocket, Socket oSocket, Board theBoard) {
		this.xSocket = xSocket;
		this.oSocket = oSocket;
		this.theBoard = theBoard;
		createPlayers();
	}

	@Override
	public void run() {
		// createPlayers();
		System.out.println("Started game");

		String xLine = null;
		String oLine = null;
		String response = null;
		int checkPlayerMark = 0;
		int row;
		int col;

		// First read in x-Player's move, validate, add to the board, then send back the
		// line to both client objects to display on both boards
		while (true) {
			try {
				// 0 indicates X player's turn
				if (checkPlayerMark == 0) {
					xLine = xSocketIn.readLine();
					System.out.println("x player made move" + xLine);
					String[] temp = xLine.split(",");
					row = Integer.parseInt(temp[0]);
					col = Integer.parseInt(temp[1]);

					if (validateMove(row, col)) {
						theBoard.addMark(row, col, xPlayer.getMark());
						xSocketOut.println(LETTER_X + "," + xLine);
						oSocketOut.println(LETTER_X + "," + xLine);
						checkPlayerMark = 1;
					} else {
						// have error message
						// dont update checkPlayerMark, so it will be X move

					}
					//1 indicates O player's turn
				} else if (checkPlayerMark == 1) {
					// Next, read in oline
					oLine = oSocketIn.readLine();
					System.out.println("o player made move" + oLine);
					String[] temp2 = oLine.split(",");
					row = Integer.parseInt(temp2[0]);
					col = Integer.parseInt(temp2[1]);

					if (validateMove(row, col)) {
						theBoard.addMark(row, col, oPlayer.getMark());
						xSocketOut.println(LETTER_O + "," + oLine);
						oSocketOut.println(LETTER_O + "," + oLine);
						checkPlayerMark = 0;
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			// Send this response over to the clients. Since we have this method
			// synchronised, the output is being sent over
			// to only one client at any given point of time.

			// This also creates the issue of the clients not sharing the board.
			// The board is being updated on the server but since the board is individually
			// updated on the client side, the two clients are
			// are seeing separate boards.

			// Possible solution could be to serialise the board object. Send it to the
			// client after every move and update
			// the board on both the clients.

//			socketOut.println(new Message(theBoard, response, mark));

			theBoard.display();
		}
	}

	/**
	 * Creates the x-Player and o-Player, and initializes their respective sockets
	 * with the input and output streams
	 */
	public void createPlayers() {
		// X-player
		try {
			xSocketIn = new BufferedReader(new InputStreamReader(xSocket.getInputStream()));
			xSocketOut = new PrintWriter(xSocket.getOutputStream(), true);
			xPlayer = new Player(xSocketIn, xSocketOut, LETTER_X);
//			xSocketOut.println( "Setting player" + LETTER_X);
			xSocketOut.println(LETTER_X);

			oSocketIn = new BufferedReader(new InputStreamReader(oSocket.getInputStream()));
			oSocketOut = new PrintWriter(oSocket.getOutputStream(), true);
			oPlayer = new Player(oSocketIn, oSocketOut, LETTER_O);
//			oSocketOut.println("Setting player" + LETTER_O);
			oSocketOut.println(LETTER_O);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public boolean validateMove(int row, int col) {
		// Fix this method
		return true;
	}

	public void sendError() {
		// Fix this method
	}

	public boolean checkWinner() {
		Boolean gameEnd = false;
		if (theBoard.xWins()) {
			gameEnd = true;
			// theView.setMessageArea("Game Over!, " + theView.getNameX() + " is the
			// winner!");
			System.out.println("Game Over!, X is the winner!");
			return gameEnd;
		} else if (theBoard.oWins()) {
			gameEnd = true;
			// theView.setMessageArea("Game Over!, " + theView.getNameO() + " is the
			// winner!");
			System.out.println("Game Over!, O is the winner!");
			return gameEnd;
		} else if (theBoard.isFull()) {
			gameEnd = true;
			// theView.setMessageArea("Game ends in Tie");
			System.out.println("Game ends in Tie");
			return gameEnd;
		}
		return gameEnd;
	}

}
