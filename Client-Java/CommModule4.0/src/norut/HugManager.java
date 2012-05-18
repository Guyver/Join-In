package norut;




public class HugManager{
	
	static private HugManager hgeh = null;

	private int remainingHugs=0;
	private boolean rightArmExtended=false;
	private boolean rightArmClosed=false;
	private boolean leftArmExtended=false;
	private boolean leftArmClosed=false; 

	
	
	static public HugManager getHugManager(int numberOfHugs) {
	
	        if (hgeh == null) {
	            hgeh = new HugManager(numberOfHugs);
	        }
	        return hgeh;
	        
	        
     }
	

	private HugManager(int numberOfHugs){
		
		remainingHugs=numberOfHugs;

	}
	
	public synchronized void leftArmClosed(){
		
		leftArmClosed=true;
		leftArmExtended=false;
		checkState();
	}
	public synchronized void leftArmExtended(){
		leftArmClosed=false;
		leftArmExtended=true;
		checkState();
	}
	public synchronized void rightArmClosed(){
		rightArmClosed=true;
		rightArmExtended=false;
		checkState();
	}
	public synchronized void rightArmExtended(){
		rightArmClosed=false;
		rightArmExtended=true;
		checkState();
	}
	public synchronized void checkState(){
		if((rightArmClosed && leftArmClosed) || (rightArmExtended && leftArmExtended)){
			System.out.println("NICE HUG!");	
			resetRightArm();
			resetLeftArm();
			remainingHugs--;
			if(remainingHugs==0){
				System.out.println("Good job, you've finished!");
			
			
			}
		
		}
		
	}
	public synchronized void resetRightArm(){
		rightArmClosed=false;
		rightArmExtended=false;	
	}
	public synchronized void resetLeftArm(){
		leftArmClosed=false;
		leftArmExtended=false;
		
	}
	public synchronized void setNumberOfHugs(int hugs){
		remainingHugs= hugs;
	}
	public synchronized void addHugs(int extraHugs){
		remainingHugs+=extraHugs;
	}
	
	
	
}
