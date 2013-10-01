package gbdbserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class GBBDServer {
	
	Controller controller;
	
	public static void main(String[] args) {

		GBBDServer server = new GBBDServer();

		server.startServer();
	}

	public ServerSocket sSoc = null;
	
	public void startServer (){
		controller = new Controller();
		
		try {
			sSoc = new ServerSocket(5666);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}

		while(!sSoc.isClosed()) {
			try {
				Socket inSoc = sSoc.accept();
				DatabaseThread newDatabaseThread = new DatabaseThread(inSoc, controller);
				newDatabaseThread.start();
				//System.out.println(newDatabaseThread);
			}
			catch (IOException e) {			
				//falhou ao aceitar a socket
				System.out.println("Socket Closed");
			}
		
			
		}
	}
	
	/*public void sleep(long milisec) throws InterruptedException{
		System.out.println("DB down");
		Thread.sleep(milisec);
		System.out.println("DB up");
	}*/
}
