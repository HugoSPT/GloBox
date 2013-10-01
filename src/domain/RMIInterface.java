package domain;

import java.rmi.*;

import messages.*;

public interface RMIInterface extends Remote{

	/**
	 * Requests to occupy a seat
	 * @param theater_id the theater where to occupy the seat
	 * @param seatReservation the seat to occupy
	 * @return a message containing if the seat was occupied.
	 * if (state == false && seats == null) occurred an error;
	 * @throws RemoteException
	 */
	ReservationReplyMessage reservationRequest(int clientID, int theaterID, int seatReservation) throws RemoteException;
	
	/**
	 * Requests the list of theaters form a determinate zone
	 * @param zone the zone requested
	 * @return a message containing the theaters from zone
	 * if (theaters == null) occurred an error;
	 * @throws RemoteException
	 */
	TheatersListReplyMessage listRequest(int zone) throws RemoteException;
	
	/**
	 * Requests the seats from a theater
	 * @param theaterId the theater requested
	 * @return a TheateSeats message with the theater seats
	 * if (seats == null) occurred an error;
	 * @throws RemoteException
	 */
	TheaterSeatsReplyMessage seatsRequest(int clientID, int theaterId) throws RemoteException;
	
	/**
	 * Requests the reserved seat to be changed
	 * @param theaterID the theater where the seat is
	 * @param reservation the seat to be changes to
	 * @return if it was changed
	 * @throws RemoteException
	 */
	boolean changeReservationRequest(int clientID, int theaterID, int oldReservation, int newReservation) throws RemoteException;

	/**
	 * 
	 * @param clientID
	 * @throws RemoteException
	 */
	void cancelRequest(int clientID) throws RemoteException;
	
	void interrupt(long milisec) throws RemoteException, NotBoundException, InterruptedException;


}
