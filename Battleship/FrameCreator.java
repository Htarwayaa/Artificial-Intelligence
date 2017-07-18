import java.awt.BorderLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


@SuppressWarnings("serial")
public class FrameCreator extends JFrame{
	
	static JFrame frame;
	
//	maakt menu
	JMenuBar bar;
	JMenu menu1;
	JMenu menu2;
	
	JMenuItem gamebutton = new JMenuItem("Start nieuw spel");
	JMenuItem exitbutton = new JMenuItem("Afsluiten");
	JMenuItem helpbutton = new JMenuItem("Uitleg spel");
	
//	Maakt het hoofdframe
	public FrameCreator(){
		boolean bigscreen = false;
		if (Main.getNumberOfPlayers() >= 3){
			bigscreen = true;
		}
		int yBound;
		frame = new JFrame();
		if (bigscreen){
			yBound = 950;
		} else{
			yBound = 600;
		}
	
		setBounds(50, 50, yBound, 680);
		setResizable(false);
		
		
		setVisible(true);
		
//      init menu
		menu1 = new JMenu("Spel");
		menu1.add(gamebutton);
		menu1.add(exitbutton);
		
		menu2 = new JMenu("Help");
		menu2.add(helpbutton);
		
		bar = new JMenuBar();
		bar.add(menu1);
		bar.add(menu2);
		

		add(bar, BorderLayout.NORTH);
		
//		create listeners
		SymAction lSymAction = new SymAction();
		gamebutton.addActionListener(lSymAction);
		exitbutton.addActionListener(lSymAction);
		helpbutton.addActionListener(lSymAction);
	}
	
	
	

//		menu actionbutton
	class SymAction implements ActionListener {
		public void actionPerformed(ActionEvent MouseClick) {
			Object object = MouseClick.getSource();
			if (object == gamebutton) {
				mainPanel mainPanel1 = new mainPanel();
				getContentPane().add(mainPanel1, BorderLayout.EAST);
				Game.createPlayers();
				repaint();
			}
			else if (object == exitbutton) {
				System.exit(0);
			}
			else if (object == helpbutton) {
				showHelp();
			}

		}
	}
	
//	Help functie
	Label textLabel;
	JTextArea textArea;
	public void showHelp(){
		JFrame help = new JFrame();
		help.setSize(500,500);
		help.setVisible(true);

		textArea = new JTextArea(
		    "Zeeslag wordt gespeeld op een veld van 10 x 10 hokjes." +
		    "Iedere speler mag op zijn grid vijf schepen plaatsen met een omvang varierend van twee" +
		    "tot vijf hokjes. Schepen mogen horizontaal of verticaal staan, maar niet diagonaal. " +
		    "Schepen kunnen ook niet samen hetzelfde hokje in beslag nemen." +
		    "Het is de bedoeling dat spelers om beurten op elkaars schepen schieten." +
		    "Wanneer alle schepen van een speler vernietigd zijn, is het spel afgelopen." +
		    "Om het spel te starten, kies 'Spel', en dan 'Nieuw spel starten'" +
			"Vervolgens dienen er in het spelerveld 5 schepen te worden geplaatst," +
			"daarna gaat het spel verder, het spel wordt dan gestart. En kan men gaan schieten."
		);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);		
		help.add(textArea);
	}
}
