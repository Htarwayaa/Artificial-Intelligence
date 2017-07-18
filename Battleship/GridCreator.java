import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


@SuppressWarnings("serial")
public class GridCreator extends JPanel {
    
	final private static int grid = 10;
	final private static int buttonSize = 25;
	@SuppressWarnings("unused")
	private String naam;
	JButton[][] buttons;

	/**
	 * The constructor of GridCreator will create a grid.
	 * x is used for x-coordinate in for loop.
	 * y is used for y-coordinate in for loop.
	 * buttons is a two-dimensional array of JButtons, which can be clicked by the end user.
	 */
	public GridCreator(String name1) {
		this.naam = name1;
		setLayout(null);
		setBounds(50, 50, grid * buttonSize, grid * buttonSize);
	    setSize(grid * buttonSize, grid * buttonSize);
	    buttons = new JButton[ grid ][ grid ];
        
        for(int y=0;y<buttons.length;y++){
			for (int x=0;x<buttons[y].length;x++){
				buttons[x][y]=new JButton();
				add(buttons[x][y]);

				buttons[x][y].setEnabled(false);
				buttons[x][y].setBounds(buttonSize * x, buttonSize * y, buttonSize, buttonSize);
			}
		}
	}
	
	/**
	 * This method will enable or disable a grid.
	 * @param enabled Depending on this boolean, a grid is either enabled or disabled.
	 */
	public void enabledGrid(Boolean enabled){
		for(int y=0;y<buttons.length;y++){
			for(int x=0;x<buttons[y].length;x++){
				buttons[x][y].setEnabled(enabled);
			}
		}
	}
	
	/**
	 * The name of this method isn't the best, what it actually does is add a plaatsSchipListener on every button of a grid.
	 * This method was created so the player would be able to map his ships.
	 */
	public void plaatsSchepen(){
		for(int y=0;y<buttons.length;y++){
			for(int x=0;x<buttons[y].length;x++){
				removeListeners(buttons[x][y]);
				buttons[x][y].addActionListener(new PlaatsSchipListener(x,y));
				buttons[x][y].setEnabled(true);
			}
		}
	}
	
	/**
	 * This adds a an actionListener to every button of a grid. It is used for the player to shoot at the computers grid.
	 */
	public void buttonListener(){
		for(int y=0;y<buttons.length;y++){
			for(int x=0;x<buttons[y].length;x++){
				removeListeners(buttons[x][y]);
				buttons[x][y].addActionListener(new ButtonListener(x,y));
				buttons[x][y].setEnabled(true);
			}
		}
	}
	
	/**
	 * removes any listener from a certain button.
	 * Both buttonListener() and plaatSchepen() call this method in their for-loops
	 * @param button This is the button the listener is removed from.
	 */
	public void removeListeners(JButton button){
		while (button.getActionListeners().length > 0){
			button.removeActionListener(button.getActionListeners()[0]);
		}
	}
	

	/**
	 * This class listens to every button on a grid where it is active.
	 * It calls other methods when a certain event occurs, in this case
	 * only action is taken when the mouse button is pressed and released.
	 *
	 */
    class ButtonListener implements ActionListener {
    	private int xco; 
    	private int yco;
        public ButtonListener(int x, int y){
        	xco = x;
        	yco = y;
        }
        
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            if (Game.getStrike(xco,yco,Game.computer1)) {
            	mainPanel.melding.setText("Raak geschoten!");
                button.setBackground(Color.red);
            } else {
            	mainPanel.melding.setText("Mis poes!");
            	Game.computerSchiet(true);
                button.setBackground(Color.cyan);
            }
            button.setEnabled(false);
        }
    }

    
    /**
     * This class listens to every button on a grid. It is actually only used on the players grid, when
     * the player has to place his ships.
     */
    class PlaatsSchipListener implements ActionListener{
    	
    	private int xco = 0;
    	private int yco = 0;
    	
    	public PlaatsSchipListener(int x, int y){
    		xco = x;
    		yco = y;
    		
    	}
    	/**
    	 * When a button is clicked, it checks if it is allowed to place a certain ship on the coordinate corresponding to that button.
    	 */
    	public void actionPerformed(ActionEvent e){
    		@SuppressWarnings("unused")
			JButton button = (JButton) e.getSource();
    		boolean conflict = false;
    		if(Game.speler.fleet.get(Game.speler.getShipnumber()).horizontal){ //horizontaal
    			for(int xLoper=0; xLoper < Game.speler.fleet.get(Game.speler.getShipnumber()).getBreedte();xLoper++){ //controleer coordinaten die huidige schip wil hebben op conflicten
					for(int iConflictIndex=0; iConflictIndex < Game.speler.getShipnumber(); iConflictIndex++){// deze lus pakt alle eerder geplaatste schepen (heeft geen zin om nog niet geplaatste schepen te controleren)
						for(int iConflictCo=0;iConflictCo < Game.speler.fleet.get(iConflictIndex).coordinates.length;iConflictCo++){ //doorloopt coordinaten van een eerder geplaatst schip en controleert op overeenkomsten.
							if(Game.speler.fleet.get(iConflictIndex).coordinates[0][iConflictCo] == xco+xLoper && Game.speler.fleet.get(iConflictIndex).coordinates[1][iConflictCo] == yco){
								conflict = true; //indien overeenkomst, is er een conflict.
							}
						}
					}
				}
    			if(Game.speler.fleet.get(Game.speler.getShipnumber()).getBreedte()+xco > 10 || conflict){
    				mainPanel.melding2.setText("Niet mogelijk om schip hier te plaatsen ");
    			} else{
    				
    				for(int i=0; i<Game.speler.fleet.get(Game.speler.getShipnumber()).getBreedte();i++){
    					Game.speler.fleet.get(Game.speler.getShipnumber()).coordinates[0][i]=xco+i;
    					Game.speler.fleet.get(Game.speler.getShipnumber()).coordinates[1][i]=yco;
    					buttons[xco+i][yco].setBackground(Color.black);
    					buttons[xco+i][yco].setEnabled(false);
    				}
    				for(int i=0; i<Game.speler.fleet.get(Game.speler.getShipnumber()).getBreedte();i++){
        				System.out.println(Game.speler.fleet.get(Game.speler.getShipnumber()).coordinates[0][i] + " " + Game.speler.fleet.get(Game.speler.getShipnumber()).coordinates[1][i]);
        			}
    				Game.speler.setShipnumber(Game.speler.getShipnumber()+1);
    				mainPanel.melding2.setText("");
    			}
    		} else{ // verticaal
    			for(int yLoper=0; yLoper < Game.speler.fleet.get(Game.speler.getShipnumber()).getLengte();yLoper++){
					for(int iConflictIndex=0; iConflictIndex < Game.speler.getShipnumber(); iConflictIndex++){
						for(int iConflictCo=0;iConflictCo < Game.speler.fleet.get(iConflictIndex).coordinates.length;iConflictCo++){
							if(Game.speler.fleet.get(iConflictIndex).coordinates[0][iConflictCo] == xco && Game.speler.fleet.get(iConflictIndex).coordinates[1][iConflictCo] == yco+yLoper){
								conflict = true;
							}
						}
					}
				}
    			if(Game.speler.fleet.get(Game.speler.getShipnumber()).getLengte()+yco > 10 || conflict){
    				mainPanel.melding2.setText("Niet mogelijk om schip hier te plaatsen");
    			} else{		
    				for(int i=0; i<Game.speler.fleet.get(Game.speler.getShipnumber()).getLengte();i++){
    					Game.speler.fleet.get(Game.speler.getShipnumber()).coordinates[0][i]=xco;
    					Game.speler.fleet.get(Game.speler.getShipnumber()).coordinates[1][i]=yco+i;
    					buttons[xco][yco+i].setBackground(Color.black);
    					buttons[xco][yco+i].setEnabled(false);
    				}
    				for(int i=0; i<Game.speler.fleet.get(Game.speler.getShipnumber()).getLengte();i++){
        				System.out.println(Game.speler.fleet.get(Game.speler.getShipnumber()).coordinates[0][i] + " " + Game.speler.fleet.get(Game.speler.getShipnumber()).coordinates[1][i]);
        			}
    				Game.speler.setShipnumber(Game.speler.getShipnumber()+1);
    				mainPanel.melding2.setText("");
    			}
    		}
    		if(Game.speler.getShipnumber() == 5){
    			mainPanel.rotate.setEnabled(false);
    			Game.plaatsComputerVloot();
    		} else{
    			Game.speler.getMeldingen();
    		}
    	}
    }
	
	
}
