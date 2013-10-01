package messages;

public class TheaterSeatsRequestMessage extends Message {

	private int theater_id;
	
	public TheaterSeatsRequestMessage(int theater_id) {
		this.theater_id = theater_id;
	}
	
	public int getTheaterID(){
		return this.theater_id;
	}
	
}
