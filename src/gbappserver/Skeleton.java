package gbappserver;

import messages.*;
import domain.CommunicationController;

/**
 * Class that communicates with the DBServer
 * @author André Matias, Hugo Sousa, Ruben Campos
 */
public class Skeleton {

	/**
	 * Makes a reservation/occupied state to seats 
	 * @param message the message to database that specifies which theater and seat
	 * @param databaseCom communication between app server and database
	 * @requires databaseCom != null;
	 * @return the result of reservation or null if an error occurred
	 */
	public static ReservationReplyMessage reservation(ReservationRequestMessage message, CommunicationController databaseCom){
		databaseCom.writeObject(message);
		Object objReply =  databaseCom.readObject();
		
		ReservationReplyMessage reply = null;
		if(objReply instanceof ReservationReplyMessage)
			reply = (ReservationReplyMessage) objReply;

		return reply;
	}

	/**
	 * Requests the database information about a theater
	 * @param message the message to database
	 * @param databaseCom communication between app server and database
	 * @requires databaseCom != null;
	 * @return informations about this theater or null if an error occurred
	 */
	public static TheaterSeatsReplyMessage getTheater(TheaterSeatsRequestMessage message, CommunicationController databaseCom){
		databaseCom.writeObject(message);
		Object objReply =  databaseCom.readObject();
		
		TheaterSeatsReplyMessage reply = null;
		if(objReply instanceof TheaterSeatsReplyMessage)
			reply = (TheaterSeatsReplyMessage) objReply;
		
		return reply;
	}

	/**
	 * Requests the database for a list of theaters
	 * @param message the message to the database containing the theaters requested
	 * @param databaseCom communication between app server and database
	 * @requires databaseCom != null; 
	 * @return list of theaters requested or null if an error occurred
	 */
	public static TheatersListReplyMessage getTheatersList (TheatersListRequestMessage message, 
			CommunicationController databaseCom){

		databaseCom.writeObject(message);
		Object objReply =  databaseCom.readObject();
		
		if(objReply == null)
			return null;
	
		TheatersListReplyMessage reply = null;
		if(objReply instanceof TheatersListReplyMessage)
			reply = (TheatersListReplyMessage) objReply;
				
		return reply;

	}

}
