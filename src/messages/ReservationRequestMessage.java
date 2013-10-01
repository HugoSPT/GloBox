package messages;

public class ReservationRequestMessage extends Message {

	private int theater_id;
	private int seatReservation;
	
	public ReservationRequestMessage(int theater_id, int seatReservation) {
		this.theater_id = theater_id;
		this.seatReservation = seatReservation;
	}
	
	public int getTheaterID(){
		return this.theater_id;
	}
	
	public int getSeatReservation(){
		return this.seatReservation;
	}

}
