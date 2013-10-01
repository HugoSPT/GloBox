package gbclient;

import java.rmi.*;
import java.rmi.registry.*;
import java.util.LinkedList;
import domain.*;
import messages.*;

public class GBClient
{
	private RMIInterface rmiServer;
	private boolean connected;
	private LinkedList<LinkedList<Theater>> list;
	private int reservation;
	private int reservationTheater;
	private final int ID;
	private final int TIMEOUT = 11000;
	private long lastRequest;

	/**
	 * @throws RemoteException 
	 * @throws NotBoundException 
	 * 
	 */
	public GBClient(int id) throws RemoteException, NotBoundException{
		ID = id;
		String serverAddress = "127.0.0.1";
		int serverPort = 5665;
		list = new LinkedList<LinkedList<Theater>>();
		reservation = -3;
		reservationTheater = -3;
		lastRequest = -1;
		
		Registry registry=LocateRegistry.getRegistry(serverAddress,serverPort);
		rmiServer = (RMIInterface)(registry.lookup("GBAppServer"));

		connected = true;
	}
	
	public int getReservationTheater(){
		return this.reservationTheater;
	}
	
	/**
	 * 
	 * @return -3 if not initialized
	 * @throws RemoteException 
	 */
	public int getReservation() throws RemoteException{
		return this.reservation;
	}
	
	private boolean connectionTimout() throws RemoteException {
		if(this.lastRequest == -1)
			this.lastRequest = System.currentTimeMillis();
		if(System.currentTimeMillis() - lastRequest > TIMEOUT){
			this.connected = false;
			rmiServer.cancelRequest(this.ID);
			return true;
		}
		lastRequest = System.currentTimeMillis();
		
		return false;
	}

	/**
	 * 
	 * @param seat
	 * @throws RemoteException 
	 */
	public boolean setReservation(int theaterID, int seat) throws RemoteException{
		if(this.lastRequest == -1)
			this.lastRequest = System.currentTimeMillis();
		
		if(connectionTimout())
			return false;
		
		this.reservationTheater = theaterID;
		
		if(this.reservation == seat)
			return true;
		
		if(this.reservation == -1)
			return false;
		
		if(rmiServer.changeReservationRequest(this.ID, theaterID, this.reservation, seat)){
			this.reservation = seat;
			return true;
		}
		
		return false;

	}
	
	/**
	 * 
	 * @param theaterID
	 * @return
	 * @throws RemoteException
	 */
	public TheaterSeats requestTheaterSeats(int theaterID) throws RemoteException{
		
		if(this.lastRequest == -1)
			this.lastRequest = System.currentTimeMillis();
		
		if(connectionTimout()){
			System.out.println("Deu timeout");
			return null;
		}

		TheaterSeatsReplyMessage reply;

		reply = rmiServer.seatsRequest(this.ID, theaterID);
		
		this.reservationTheater = theaterID;
		
		if(reply != null){
			reservation = reply.getReservation();
			return reply.getTheaterSeats();
		}
		
		System.out.println("Chegou ao fim a null");
		
		return null;
	}

	/**
	 * 
	 * @param theaterID
	 * @return
	 * @throws RemoteException
	 */
	public boolean reserve(int theaterID) throws RemoteException{
		
		if(this.lastRequest == -1)
			this.lastRequest = System.currentTimeMillis();
		
		if(connectionTimout())
			return false;
		
		ReservationReplyMessage reply;
		reply = rmiServer.reservationRequest(this.ID, theaterID, reservation);
		
		if(reply != null)
			return reply.getState();
		
		return false;
	}

	/**
	 * 
	 * @param zone
	 * @return
	 * @throws RemoteException
	 */
	public LinkedList<Theater> getTheatersList(int zone) throws RemoteException{
		
		if(this.lastRequest == -1)
			this.lastRequest = System.currentTimeMillis();
		
		if(connectionTimout()){
			System.out.println("Connection timout");
			return null;
		}
		
		if(list.size() > 0)
			for(LinkedList<Theater> l : list)
				if(l != null && l.getFirst().getZone() == zone)
					return l;
		
		//Wasn't on cache
		TheatersListReplyMessage reply;
		reply = rmiServer.listRequest(zone);
		
		if(reply != null){
			this.list.add(reply.getListTheaters());
			return reply.getListTheaters();
		}
				
		System.out.println("Chegou ao fim a null");
		
		return null;
	}


	public boolean isConnected() throws RemoteException{
		
		if(this.lastRequest == -1)
			this.lastRequest = System.currentTimeMillis();
		
		if(connectionTimout())
			return false;
		return this.connected;
	}
	
	public void disconect(){
		this.connected = false;
	}

	public void reconnect(){
		this.connected = true;
		this.lastRequest = System.currentTimeMillis();
	}
	
	public void cancelRequest() throws RemoteException {
		
		if(this.lastRequest == -1)
			this.lastRequest = System.currentTimeMillis();

		if(connectionTimout())
			return;
		
		rmiServer.cancelRequest(this.ID);

	}

	public Theater getTheater(int theaterID) throws RemoteException {
		
		if(this.lastRequest == -1)
			this.lastRequest = System.currentTimeMillis();
		
		if(connectionTimout())
			return null;
		
		for(LinkedList<Theater> theaters : list)
			for(Theater t : theaters)
				if(t.getId() == theaterID)
					return t;
		return null;
	}
	
	public int getID(){
		return this.ID;
	}
}
