package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;

public class Client implements Constants {

	private Socket aSocket;
	private PrintWriter socketOut;
	private BufferedReader socketIn;
	private GameView theView;

	public Client(String serverName, int portNumber, GameView theView) {
		this.theView = theView;
		try {
			aSocket = new Socket(serverName, portNumber);
			// Socket input Stream
			socketIn = new BufferedReader(new InputStreamReader(aSocket.getInputStream()));

			// Socket output Stream
			socketOut = new PrintWriter(aSocket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendButtonPress(int row, int col) {
		// Convert row and columns into a single string
		String line = "";
		line = row + "," + col;

		/*
		 * Check if this line is required or can be replaced with something beter
		 */
		if (line != null && !line.isEmpty()) {
			System.out.println("Sending row,col values to server..");
			socketOut.println(line);
		}
	}

//	public void getPlayerMark() {
//		// Convert row and columns into a single string
//		String line = "";
//		line = row + "," + col;
//
//		/*
//		 * Check if this line is required or can be replaced with something beter
//		 */
//		if (line != null && !line.isEmpty()) {
//			System.out.println("Sending row,col values to server..");
//			socketOut.println(line);
//		}
//	}

	public void setMark(String response) {
		// We are assuming the response here consists of the row and column of the mark.
		// Parse the response to separate the row and column
		// Place the mark at the correct position on the board.
		int row, col = 0;
		String mark;
		String[] temp = new String[3];
//		String response = null;

//		try {
//			response = socketIn.readLine();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		// Checking to see if the server response is anything other than the string
		// containing row,col and mark
		if (response != null) {
			temp = response.split(",");

			// Add an array size check here
			if (temp.length == 3) {
				row = Integer.parseInt(temp[1]);
				col = Integer.parseInt(temp[2]);
				mark = temp[0];
				JButton button[][] = theView.getButton();
				System.out.println("response row, col, mark: " + row + col + mark);

				if ((row >= 0 && row <= 2) && (col >= 0 && col <= 2)) {

					// Changing mark to lower case letters so that they are visible on the GUI
					if (mark.charAt(0) == LETTER_X) {
						button[row][col].setText("x");
					} else {
						System.out.println("Mark is not equal to X");
					}
					if (mark.charAt(0) == LETTER_O) {
						button[row][col].setText("o");
					} else {
						System.out.println("Mark is not equal to O");
					}
				} else {
					System.out.println("Row and column not in limits!");
				}
			}

		}
	}

	public String getServerResponse() {
		while (true) {
			String response = "";
			try {
				response = socketIn.readLine();
				System.out.println("Server response is: " + response);

				if (response != null) {
					setPlayer(response.charAt(0));
					setMark(response);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setPlayer(char response) {
		// Checking if the string actually consists of the player Mark
		// Character c = theView.getPlayerSymbol();
		// System.out.println("Character of JTextField" + c);
		if (theView.getPlayerSymbol() == null) {
			if (response == LETTER_X || response == LETTER_O) {
				if (response == LETTER_X) {
					theView.setPlayerSymbol(LETTER_X);
				} else if (response == LETTER_O) {
					theView.setPlayerSymbol(LETTER_O);
				}
			}
		}
	}
}
