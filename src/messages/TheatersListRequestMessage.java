package messages;

public class TheatersListRequestMessage extends Message {

	private int zone;
	
	public TheatersListRequestMessage(int zone) {
		this.zone = zone;
	}
	
	public int getZone(){
		return this.zone;
	}

}
