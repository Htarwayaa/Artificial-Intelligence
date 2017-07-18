import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class mainPanel extends JPanel {
	
	final private static int gridSize = 250;
	static GridCreator grid1;
	static GridCreator grid2;
	static GridCreator grid3;
	static GridCreator grid4;
	static TextField melding = new TextField();
	static TextField melding2 = new TextField();
	static TextField orientation = new TextField();
	static JButton rotate = new JButton();
	
	
	public mainPanel() {
		int numberOfPlayers = Main.getNumberOfPlayers();
		setBounds(0, 0, 950, 660);
		setBackground(Color.BLUE);
		setLayout(new BorderLayout());
		
		melding.setBounds(30, 500, 250, 25);
		melding.setBackground(Color.white);
		melding.setForeground(new Color(7,141,16));
		melding.setText("");
		melding2.setBounds(30,535,250,25);
		melding2.setBackground(Color.white);
		melding2.setForeground(new Color(255,0,0));
		melding2.setText("");
		
		orientation.setBounds(190, 400, 100, 25);
		orientation.setEditable(false);
		
		rotate.setBounds(30, 400, 150, 25);
		rotate.setText("Draai 90 graden");
		
		grid1 = new GridCreator("Computer 1");
		grid1.setBounds(330, 50, gridSize, gridSize);
		add(grid1);
		Label label1 = new Label();
		if (numberOfPlayers >= 3){
			label1.setText("Computer 1");
		} else {
			label1.setText("Computer");
		}
		label1.setVisible(true);
		label1.setBounds(330, 20, 75, 25);
		
		
		grid2 = new GridCreator("Speler");
		grid2.setBounds(330, 350, gridSize, gridSize);
		add(grid2);
		Label label2 = new Label();
		label2.setText("Speler");
		label2.setVisible(true);
		label2.setBounds(330, 310, 75, 25);
		
		Label label3 = new Label();
		label3.setVisible(false);
		Label label4 = new Label();
		label4.setVisible(false);
		
		if (numberOfPlayers >= 3){
			grid3 = new GridCreator("Computer 2");
			grid3.setBounds(630, 50, gridSize, gridSize);
			add(grid3);
			label3.setBounds(630, 20, 75, 25);
			label3.setText("Computer 2");
			label3.setVisible(true);
		}
		
		if (numberOfPlayers == 4){
			grid4 = new GridCreator("Computer 3");
			grid4.setBounds(630, 350, gridSize, gridSize);
			add(grid4);
			label4.setBounds(630, 310, 75, 25);
			label4.setText("Computer 3");
			label4.setVisible(true);
		}
		
		add(label1);
		add(label2);
		add(label3);
		add(label4);
		add(melding);
		add(melding2);
		add(orientation);
		add(rotate);
		
		rotate.addActionListener(new ButtonListener());
	}
	
    class ButtonListener implements ActionListener{
    	
    	public ButtonListener(){
    		
    	}
    	public void actionPerformed(ActionEvent e){
    		JButton button = (JButton) e.getSource();
    		if (button == rotate) {
				System.out.println("Rotate knop geklikt");
				Game.speler.fleet.get(Game.speler.getShipnumber()).switchlb();
				Game.speler.getMeldingen();
				if(Game.speler.fleet.get(Game.speler.getShipnumber()).horizontal){
					Game.speler.fleet.get(Game.speler.getShipnumber()).horizontal = false;
				} else{
					Game.speler.fleet.get(Game.speler.getShipnumber()).horizontal = true;
				}
				orientation.setText(Game.speler.fleet.get(Game.speler.getShipnumber()).getOrientation());
				System.out.println(Game.speler.fleet.get(Game.speler.getShipnumber()).getNaam() + Game.speler.fleet.get(Game.speler.getShipnumber()).getBreedte() + Game.speler.fleet.get(Game.speler.getShipnumber()).getLengte());
    		}
    	}
    }

}