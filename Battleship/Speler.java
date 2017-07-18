import java.util.ArrayList;


public class Speler {
	
	protected ArrayList<Schip> fleet;
	public int i=0;
	int schepenKapotCounter = 0;
	boolean heleVlootKapot = false;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Speler(){
		fleet = new ArrayList();
		
		
		Schip vliegdekschip = new Vliegdekschip();
		Schip mijnenveger = new Mijnenveger();
		Schip onderzeeer = new Onderzeeer();
		Schip jager = new Jager();
		Schip slagschip = new Slagschip();
		
		fleet.add(vliegdekschip);
		fleet.add(mijnenveger);
		fleet.add(onderzeeer);
		fleet.add(jager);
		fleet.add(slagschip);
		
		for(int j=0; j<5; j++){
			for(int k=0; k < fleet.get(j).getLengte();k++){
				fleet.get(j).coordinates[0][k]=-1;
				fleet.get(j).coordinates[1][k]=-1;
			}
		}
	}
	
	public int getShipnumber(){
		return i;
	}
	
	public void setShipnumber(int setI){
		i=setI;
	}
	
	public void getMeldingen(){
		mainPanel.melding.setText("Plaats " + fleet.get(i).getNaam() + " " + fleet.get(i).getBreedte() + "x" + fleet.get(i).getLengte());
		mainPanel.orientation.setText(fleet.get(i).getOrientation());
		System.out.println(fleet.get(i).getNaam() + fleet.get(i).getBreedte() + fleet.get(i).getLengte());
	}
}
