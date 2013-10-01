package gbdbserver;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import domain.*;
import messages.*;

class DatabaseThread extends Thread {

		private CommunicationController appClientCom;
		private Controller controller;
		DatabaseThread(Socket inSoc, Controller controller) {
			
			this.controller = controller;
			Socket socket = inSoc;
			try {
				appClientCom = new CommunicationController(socket);

			} catch (IOException e) {
				System.out.println("Nao foi possivel criar os canais de comunicaco.");
				appClientCom = null;
			}
		}
		
		@Override
		public void run(){
			Object message = appClientCom.readObject();
				
			try {
				dispatcher(message);
			} catch (UnknownHostException e) {		
				e.printStackTrace();
			} catch (IOException e) {			
				e.printStackTrace();
			}
				
		}
		
		private void dispatcher(Object message) throws UnknownHostException, IOException{	
			
			if(message instanceof ReservationRequestMessage){
				ReservationRequestMessage request = (ReservationRequestMessage) message;			
				int seat = request.getSeatReservation();
								
				int line = seat/40;
				int column = seat%40;
								
				boolean reserved = controller.reserveSeat(request.getTheaterID(), line, column);
				appClientCom.writeObject(new ReservationReplyMessage(request.getTheaterID(), 
						controller.getTheaterSeats(request.getTheaterID()), reserved));
				
			}else if(message instanceof TheaterSeatsRequestMessage){
				TheaterSeatsRequestMessage request = (TheaterSeatsRequestMessage) message;
				TheaterSeats seats = controller.getTheaterSeats(request.getTheaterID());	
				appClientCom.writeObject(new TheaterSeatsReplyMessage(request.getTheaterID(), seats));
			}else if(message instanceof TheatersListRequestMessage){
				TheatersListRequestMessage request = (TheatersListRequestMessage) message;
				LinkedList<Theater> theaters = controller.getAllTheaters(request.getZone());
				appClientCom.writeObject(new TheatersListReplyMessage(request.getZone(),theaters));
			}
			
			appClientCom.closeConnection();
		}
	}