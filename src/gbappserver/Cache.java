package gbappserver;

import java.util.LinkedList;
import domain.*;

public class Cache {

	private static final int MAX_SEATS = 1000;
	private LinkedList<TheaterSeats> cache;
	private LinkedList<LinkedList<Theater>> theaters;
	private LinkedList<int[]> reservations;

	/**
	 * Creates a cache to tune up the AppServer
	 */
	public Cache() {
		this.cache = new LinkedList<TheaterSeats>();
		this.theaters = new LinkedList<LinkedList<Theater>>();
		this.reservations = new LinkedList<int[]>();
	}

	/**
	 * Returns the list of theaters from a zone
	 * @param zone the requested zone
	 * @return the list of theaters or null if not found
	 */
	public LinkedList<Theater> getAllTheaters(int zone){
		for(LinkedList<Theater> list : this.theaters)
			if(list.getFirst().getZone() == zone)
				return list;
		return null;
	}

	/**
	 * Adds the theaters of a zone to the theaters list
	 * @param theaters the theaters to add
	 * @param zone the zone to add
	 * @requires this method should not be called more than once for the same zone,
	 * if it is called more than once for the same zone it simply ignore it.
	 */
	public synchronized void addZone(LinkedList<Theater> theaters, int zone){
		boolean found = false;
		for(LinkedList<Theater> list : this.theaters)
			if(list.getFirst().getZone() == zone)
				found = true;
		if(!found)
			this.theaters.add(theaters);	
	}

	/**
	 * Adds new theater seats to cache
	 * @param theaterSeats the theater with seats to add
	 */
	public synchronized void addTheaterSeats(TheaterSeats theaterSeats){
		if(theaterSeats == null)
			return;
		
		if(cache.size() < MAX_SEATS){
			for(TheaterSeats seats : cache)		
				if(seats.getId() == theaterSeats.getId()){
					seats.update(theaterSeats);
					return;
				}
			cache.addFirst(theaterSeats);
		}
		else{
			cache.removeLast();
			addTheaterSeats(theaterSeats);
		}

	}

	/**
	 * Find all seats from theaterID theater
	 * @param theaterID the theater with seats that we want
	 * @return seats of this theaterID theater or null if not found
	 */
	public synchronized TheaterSeats getTheaterSeats(int theaterID){
		for(TheaterSeats seats : cache)
			if(seats.getId() == theaterID)
				return seats;
				
		return null;
	}

	/**
	 * Changes the reserved seat on a theater
	 * @param theaterID the theater where the seat is
	 * @param oldReservation the old reserved seat
	 * @param newReservation the new seat to be reserved
	 * @return if the seat was changed or not;
	 */
	public synchronized boolean setReservation(int clientID, int theaterID, int oldReservation, int newReservation) {
		free(clientID);
		TheaterSeats seats = this.getTheaterSeats(theaterID);
		if(seats == null)
			return false;
		if(seats.getSeatStatus(newReservation/40, newReservation%40) == 'L'){
			seats.setSeatStatus(newReservation/40, newReservation%40, 'R');
			int[] reserv = {clientID, theaterID, newReservation};
			this.reservations.add(reserv);
			return true;
		} else {
			newReservation = seats.getFirstFreeSeat();
			int[] reserv = {clientID, theaterID, newReservation};
			this.reservations.add(reserv);
		}
		return false;
	}

	public synchronized void free(int clientID) {
		
		//substituir por metodo que devolve o lugar reservado
		int seat = -1;
		int theaterID = -1;
		for(int[] reservation : this.reservations)
			if(reservation != null && reservation[0] == clientID){
				seat = reservation[2];
				theaterID = reservation[1];
				this.reservations.remove(reservation);
				break;
			}
		
		if(seat != -1 && theaterID != -1)
			for(TheaterSeats seats : cache)
				if(seats.getId() == theaterID)
					//Checks if the reserved seat is not already taken
					if(seats.getSeatStatus(seat/40, seat%40) == 'R')
						seats.setSeatStatus(seat/40, seat%40, 'L');
	
	}

	public synchronized void addReservedSeat(int clientID, int theaterID, int reservation) {
		int [] seat = {clientID,theaterID,reservation};
		reservations.add(seat);
	}

}
