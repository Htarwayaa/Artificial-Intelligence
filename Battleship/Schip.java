public class Schip{
//	int x;
//	int y;
	int breedte;
	int lengte;
	String naam;
	int[][] coordinates;
	boolean horizontal = false;
	boolean heleSchipKapot = false;
	int kapotCounter=0;
	
	/**
	 * This method is called to get a certain ship's orientation in a String.
	 * @return It returns the orientation of a certain ship. This can either be "Horizontaal" or "Verticaal, and is used for display in a textfield in the GUI.
	 */
	public String getOrientation(){
		String orientation;
		if(horizontal){
			orientation = "Horizontaal";
		}
		else{
			orientation = "Verticaal";
		}
		return orientation;
	}
	
	public int getBreedte(){
		return breedte;
	}
	
	public int getLengte(){
		return lengte;
	}
	
	/**
	 * Switches height and length of a ship when rotate button in GUI is clicked.
	 */
	public void switchlb(){
		int templengte = lengte;
		lengte = breedte;
		breedte = templengte;
	}
	
	public String getNaam(){
		return naam;
	}
}
