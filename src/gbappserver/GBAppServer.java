package gbappserver;

import domain.*;
import messages.*;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.util.LinkedList;

public class GBAppServer extends java.rmi.server.UnicastRemoteObject implements RMIInterface{

	private Cache cache;
	private String dbAddress;
	private int dbPort;
	private Registry registry;
	
	public GBAppServer(String dbAddress, int dbPort) throws RemoteException, IOException, NotBoundException, InterruptedException{
		this.dbAddress = dbAddress;
		this.dbPort = dbPort;
		this.cache = new Cache();
		
		int port=5665;
		this.registry = LocateRegistry.createRegistry(port);
		registry.rebind("GBAppServer", this);
		//registry.l
		
		//registry.unbind("GBAppServer");
		//Thread.sleep(10000);
		//registry.rebind("GBAppServer", this);
		
		//Verificar
		//System.setProperty("sun.rmi.transport.conectionTimeout","1000");
		
	}

	static public void main(String args[])
	{
		try{
			new GBAppServer("127.0.0.1", 5666);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	}

	@Override
	public ReservationReplyMessage reservationRequest(int clientID, int theaterID, int seatReservation) throws RemoteException {
		if(seatReservation < 0)
			return new ReservationReplyMessage(theaterID, null, false);
					
		//Creates a communication channel between this and the DBServer
		CommunicationController databaseCC;
		try {
			databaseCC = new CommunicationController(new Socket(this.dbAddress, this.dbPort));
		} catch (UnknownHostException e) {
			return new ReservationReplyMessage(theaterID, null, false);
		} catch (IOException e) {
			return new ReservationReplyMessage(theaterID, null, false);
		}
		
		ReservationReplyMessage reply = Skeleton.reservation(new ReservationRequestMessage(theaterID, seatReservation), databaseCC);
		databaseCC.closeConnection();
		
		//Update cache
		if(reply != null){
			if(reply.getTheaterSeats() == null)
				return null;
			this.cache.free(clientID);
			this.cache.addTheaterSeats(reply.getTheaterSeats());
		}
		
		//If an error occurred the reply == null and it is returned
		return reply;
	}

	@Override
	public TheatersListReplyMessage listRequest(int zone) throws RemoteException{
				
		LinkedList<Theater> listTheaters = cache.getAllTheaters(zone);
		
		TheatersListReplyMessage reply = null;
		if(listTheaters != null && listTheaters.size() > 0){
			reply = new TheatersListReplyMessage(zone, listTheaters);
		}else{
							
			//Creates a communication channel between this and the DBServer
			CommunicationController databaseCC;
			try {
				databaseCC = new CommunicationController(new Socket(this.dbAddress, this.dbPort));
			} catch (UnknownHostException e) {
				return new TheatersListReplyMessage(zone, null);
			} catch (IOException e) {
				return new TheatersListReplyMessage(zone, null);

			}
			
			reply = Skeleton.getTheatersList(new TheatersListRequestMessage(zone), databaseCC);
			databaseCC.closeConnection();
			
			if(reply == null)
				return null;
			
			//If has not occurred an error it puts the zone theaters to cache.
			LinkedList<Theater> theaters = reply.getListTheaters();
			if(theaters != null)
				cache.addZone(theaters, zone);
			
			
		}
		
		//If an error occurred the reply == null and it is returned
		return reply;
	}

	@Override
	public TheaterSeatsReplyMessage seatsRequest(int clientID, int theaterID) throws RemoteException{
		
		TheaterSeats seats = null;
		TheaterSeatsReplyMessage reply = null;
		seats = cache.getTheaterSeats(theaterID);
	
		if(seats == null){
			
			//Creates a communication channel between this and the DBServer
			CommunicationController databaseCC;
			try {
				databaseCC = new CommunicationController(new Socket(this.dbAddress, this.dbPort));
			} catch (UnknownHostException e) {
				return new TheaterSeatsReplyMessage(theaterID, null);
			} catch (IOException e) {
				return new TheaterSeatsReplyMessage(theaterID, null);
			}
			
			reply = Skeleton.getTheater(new TheaterSeatsRequestMessage(theaterID), databaseCC);
			databaseCC.closeConnection();
			
			//If has not occurred an error it puts the theaters seats to cache.
			seats = reply.getTheaterSeats();
			if(seats != null){
				//Updates the seats so they are reserved
				int reservation = seats.getFirstFreeSeat();
				if(reservation != -1)
					seats.setSeatStatus(reservation/40, reservation%40, 'R');
				
				reply.setReservation(reservation);
				cache.addTheaterSeats(seats);
				cache.addReservedSeat(clientID, theaterID, reservation);
			}
			
			

		}
		else{
			//Updates the seats so they are reserved
			int reservation = seats.getFirstFreeSeat();
			if(reservation != -1){
				seats.setSeatStatus(reservation/40, reservation%40, 'R');
				cache.addReservedSeat(clientID, theaterID, reservation);
			}
			reply = new TheaterSeatsReplyMessage(theaterID, seats);
			reply.setReservation(reservation);
		}
		
		//If an error occurred the reply == null and it is returned
		return reply;
	}

	@Override
	public boolean changeReservationRequest(int clientID, int theaterID, int oldReservation, int newReservation) throws RemoteException {
		return cache.setReservation(clientID, theaterID, oldReservation, newReservation);
	}

	@Override
	public void cancelRequest(int clientID) throws RemoteException {
		cache.free(clientID);
	}

	@Override
	public void interrupt(long milisec) throws RemoteException, NotBoundException, InterruptedException {
		Thread.sleep(milisec);
		this.registry.rebind("GBAppServer", this);
	}


}
