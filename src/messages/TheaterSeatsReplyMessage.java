package messages;

import domain.TheaterSeats;

public class TheaterSeatsReplyMessage extends Message {

	private int theater_id;
	private TheaterSeats seats;
	private int reservation;
	
	public TheaterSeatsReplyMessage(int theater_id, TheaterSeats seats) {
		this.theater_id = theater_id;
		this.seats = seats;
		reservation = -1;
	}
	
	public int getTheater_id(){
		return this.theater_id;
	}
		
	public TheaterSeats getTheaterSeats(){
		return this.seats;
	}
	
	public int getReservation(){
		return this.reservation;
	}
	
	public void setReservation(int seat){
		this.reservation = seat;
	}

}
