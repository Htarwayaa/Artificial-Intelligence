/**
*	@author Siger Steenstra, Niek Regterschot and Liese Schmidt.
*	Some comments are in Dutch, as our education used to be in Dutch.
*	In this game you can play battleship against the computer.
*	The computer was supposed to show some intelligent behaviour.
*	Because this was one of the first projects, we worked with a priority list.
*	This project shows an object oriented style of coding.
*/

public class Main {
	static int numberOfPlayers = 2;

	public static void main(String[] args) {
		new Game();
	}
	
	public static int getNumberOfPlayers(){
		return numberOfPlayers;
	}

}