package messages;

import domain.TheaterSeats;

public class ReservationReplyMessage extends Message {

	//MODIFICACOES FEITAS, ENVIAR DEPOIS
	private boolean state;
	private TheaterSeats seats;
	private int theaterId;
	
	public ReservationReplyMessage(int theaterId, TheaterSeats newSeatsState, boolean state) {
		this.state = state;
		this.seats = newSeatsState;
	}
	
	public boolean getState(){
		return this.state;
	}
	
	public TheaterSeats getTheaterSeats(){
		return this.seats;
	}
	
	public int getTheaterId(){
		return this.theaterId;
	}

}
