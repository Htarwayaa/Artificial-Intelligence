import java.awt.Color;
import java.util.Random;

/**
*	@author Siger Steenstra, Niek Regterschot and Liese Schmidt.
*	Some comments are in Dutch, as our education used to be in Dutch.
*	In this game you can play battleship against the computer.
*	The computer was supposed to show some intelligent behaviour.
*	Because this was one of the first projects, we worked with a priority list.
*	This project shows an object oriented style of coding.
*/

public class Game {
	static Speler speler;
	static Speler computer1;
	static int spelerGridArray[][] = new int[10][10];

	public Game() {
		new FrameCreator();
	}

	public static void createPlayers() { //zorgt ervoor dat je met meerdere computers tegelijk kan spelen. 
		computer1 = new ComputerSpeler();
		speler = new PersoonSpeler();
//		if (Main.getNumberOfPlayers() >= 3) { 
//			Speler computer2 = new ComputerSpeler();
//		}
//		if (Main.getNumberOfPlayers() == 4) {
//			Speler computer3 = new ComputerSpeler();
//		}


	}
	//werkt niet optimaal, heel soms vindt er nog overlap plaats en soms wordt een schip niet volledig geplaatst. We hebben hier lang op zitten proberen, maar hebben de fout niet kunnen vinden.
	/**
	 * this method places the fleet of the computer.
	 */
	public static void plaatsComputerVloot(){
		for (int iPlaats = 0; iPlaats < 5; iPlaats++){
			boolean shipOrientation = Math.random() < 0.5;
			Game.computer1.fleet.get(iPlaats).horizontal = shipOrientation;
			if (Game.computer1.fleet.get(iPlaats).horizontal) {
				Game.computer1.fleet.get(iPlaats).switchlb();
			}
			Random random = new Random();
			boolean conflict = false;
			if(Game.computer1.fleet.get(iPlaats).horizontal){ //horizontaal
				int randomx = random.nextInt(11 - Game.computer1.fleet.get(iPlaats).getBreedte());
				int randomy = random.nextInt(10);
				for(int xLoper=0; xLoper < Game.computer1.fleet.get(iPlaats).getBreedte();xLoper++){ //controleer coordinaten die huidige schip wil hebben op conflicten
					for(int iConflictIndex=0; iConflictIndex < iPlaats; iConflictIndex++){// deze lus pakt alle eerder geplaatste schepen (heeft geen zin om nog niet geplaatste schepen te controleren)
						for(int iConflictCo=0;iConflictCo < Game.computer1.fleet.get(iConflictIndex).coordinates.length;iConflictCo++){ //doorloopt coordinaten van een eerder geplaatst schip en controleert op overeenkomsten.
							if(Game.computer1.fleet.get(iConflictIndex).coordinates[0][iConflictCo] == randomx+xLoper && Game.computer1.fleet.get(iConflictIndex).coordinates[1][iConflictCo] == randomy){
								conflict = true; //indien overeenkomst, is er een conflict.
							}
						}
					}
				}
				if(!conflict){
					for(int l = 0; l < Game.computer1.fleet.get(iPlaats).getBreedte(); l++){ 
						Game.computer1.fleet.get(iPlaats).coordinates[0][l] = randomx + l;
						Game.computer1.fleet.get(iPlaats).coordinates[1][l] = randomy;
					}
					
				}
				
			}else{ //verticaal. komt grotendeels overeen met horizontaal, commentaar: zie daar.
				int randomx = random.nextInt(10);
				int randomy = random.nextInt(11 - Game.computer1.fleet.get(iPlaats).getLengte());
				for(int yLoper=0; yLoper < Game.computer1.fleet.get(iPlaats).getLengte();yLoper++){
					for(int iConflictIndex=0; iConflictIndex < iPlaats; iConflictIndex++){
						for(int iConflictCo=0;iConflictCo < Game.computer1.fleet.get(iConflictIndex).coordinates.length;iConflictCo++){
							if(Game.computer1.fleet.get(iConflictIndex).coordinates[0][iConflictCo] == randomx && Game.computer1.fleet.get(iConflictIndex).coordinates[1][iConflictCo] == randomy+yLoper){
								conflict = true;
							}
						}
					}
				}
				if(!conflict){
					for(int l = 0; l < Game.computer1.fleet.get(iPlaats).getLengte(); l++){
						Game.computer1.fleet.get(iPlaats).coordinates[0][l] = randomx;
						Game.computer1.fleet.get(iPlaats).coordinates[1][l] = randomy + l;
					}
				}
				
			}
			if(conflict){
				iPlaats--;
			}
		}
		spelen(); //alles geplaatst, dan spelen.
	}
	
	/**
	 * This method makes the step from the initialization of the game to the actual gameplay.
	 * It makes sure that there are listeners on the buttons of the computer grid,
	 * the player cannot click on his own grid anymore, and the grid of the computer is enabled
	 * so the player can shoot.
	 */
	public static void spelen() {
		for(int x=0;x<10;x++){
			for(int y=0;y<10;y++){
				spelerGridArray[x][y] = 0;
			}
		}
		mainPanel.grid1.buttonListener();
		mainPanel.grid1.enabledGrid(true);
		mainPanel.grid2.enabledGrid(false);
	}

	// bij red dan heeft het raak geschoten, en mag je nog een keer. Bij cyan dan ligt daar geen boot. 
	// -1 is al beschoten, 1 is prioriteit, 0 is geen prioriteit+niet beschoten
	/**
	 * This method is called when the player did not hit a ship with clicking a button.
	 * It gives the computer an opportunity to shoot on a certain coordinate on the players grid.
	 * When there is a hit, the button will turn red, when there is no hit, the button will turn cyan.
	 * When there is a hit, the surrounding coordinates get priority, they will be tried first when
	 * the next shot is placed. When there is a hit, the computer is allowed to try again. The boolean
	 * beurt is used for that. Though it's not actually a turn, so much as a variable that allows the
	 * computer to shoot again while true. This happens so fast that the player won't be able to place
	 * a shot before the computer's 'turn' is over.
	 * @param beurt used to check if it is 'still' the computers turn to shoot. When computer misses,
	 * beurt will be set to false.
	 */
	public static void computerSchiet(boolean beurt){
		while (beurt){
			int prioX = -1;
			int prioY = -1;
			for(int i=0;i<10;i++){
				for(int j=0;j<10;j++){
					if (spelerGridArray[i][j] == 1){
						prioX=i;
						prioY=j;
					}
				}
			}
			if(prioX == -1 && prioY == -1){
				Boolean magRandomSchot=false;
				for(int i=0;i<100;i++){
					Random random = new Random();
					int randomX = random.nextInt(10);
					int randomY = random.nextInt(10);
					if(spelerGridArray[randomX][randomY] == 0){
						prioX=randomX;
						prioY=randomY;
						magRandomSchot=true;
					}
				}
				if(!magRandomSchot){
					for(int i=0;i<10;i++){
						for(int j=0;j<10;j++){
							if (spelerGridArray[i][j] == 0){
								prioX=i;
								prioY=j;
							}
						}
					}
				}
			}
			if(prioX == -1 && prioY == -1){
				System.out.println("Ik kan niet meer schieten");
				beurt=false;
			} else{
				if(getStrike(prioX,prioY,Game.speler)){
					mainPanel.grid2.buttons[prioX][prioY].setBackground(Color.red);
					if(prioX>0){
						if(spelerGridArray[prioX-1][prioY]!=-1){
							spelerGridArray[prioX-1][prioY]=1;
						}
					}
					if(prioX<9){
						if(spelerGridArray[prioX+1][prioY]!=-1){
							spelerGridArray[prioX+1][prioY]=1;
						}
					}
					if(prioY>0){
						if(spelerGridArray[prioX][prioY-1]!=-1){
							spelerGridArray[prioX][prioY-1]=1;
						}
					}
					if(prioY<9){
						if(spelerGridArray[prioX][prioY+1]!=-1){
							spelerGridArray[prioX][prioY+1]=1;
						}
					}
				} else{
					mainPanel.grid2.buttons[prioX][prioY].setBackground(Color.cyan);
					beurt=false;
				}
				spelerGridArray[prioX][prioY] = -1;
			}
		}
	}

	/**
	 * This method determines if there is a ship on the coordinate that is selected by either the player or the computer.
	 * This method is called for both player and computer.
	 * @param xco Selected x-coordinate
	 * @param yco Selected y-coordinate
	 * @param strikeSpeler the player of which should be checked if there is a hit.
	 * @return It returns true or false. If there is a hit, it returns true. If not, false.
	 */
	public static boolean getStrike(int xco, int yco, Speler strikeSpeler) {
		boolean strike = false;
		for (int i = 0; i < 5; i++) {
			int numberOfCoordinates;
			if (strikeSpeler.fleet.get(i).horizontal) {
				numberOfCoordinates = strikeSpeler.fleet.get(i).getBreedte();
			} else {
				numberOfCoordinates = strikeSpeler.fleet.get(i).getLengte();
			}
			for (int j = 0; j < numberOfCoordinates; j++) {
				if (strikeSpeler.fleet.get(i).coordinates[0][j] == xco
						&& strikeSpeler.fleet.get(i).coordinates[1][j] == yco) {
					strike = true;
					strikeSpeler.fleet.get(i).kapotCounter++;
					if(strikeSpeler.fleet.get(i).kapotCounter==numberOfCoordinates){
						strikeSpeler.fleet.get(i).heleSchipKapot=true;
						if(strikeSpeler==Game.speler){
							mainPanel.melding2.setText(strikeSpeler.fleet.get(i).getNaam() + " van speler is gezonken.");
						} else{
							mainPanel.melding2.setText(strikeSpeler.fleet.get(i).getNaam() + " van computer is gezonken");
						}
						strikeSpeler.schepenKapotCounter++;
						System.out.println("Schip " + strikeSpeler.fleet.get(i).getNaam() + " kapot");
					}
					if(strikeSpeler.schepenKapotCounter==5){
						strikeSpeler.heleVlootKapot=true;
						endGame();
					}
				}
			}
		}
		return strike;
	}

	
	/**
	 * This method ends the game. It disables the computer grid so no more shooting is possible
	 * and also displays the winner.
	 */
	public static void endGame(){
		mainPanel.grid1.enabledGrid(false);
		if(speler.heleVlootKapot){
			mainPanel.melding2.setText("Helaas, de computer heeft gewonnen.");
		}else{
			mainPanel.melding2.setText("Gefeliciteerd, u heeft gewonnen.");
		}
	}

}
