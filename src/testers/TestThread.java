package testers;

import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;
import java.util.Random;

import domain.RMIInterface;

import gbclient.GBClient;
import gbdbserver.GBBDServer;

public class TestThread extends Thread {

	//CONSTANTS
	//Max Number of Random Clients
	private static final int MAX_CLIENTS = 1000000;
	private static final Random RANDOM = new Random(System.currentTimeMillis());

	//The type of the Generator: 1- Requests \ 2- Failures
	private int type;

	//Metrics Variables
	//Controlls the life of the thread
	private boolean end;
	//Counts the number of requests made by the thread
	public int numberRequests;
	//Counts the number of clients created by the thread
	public int numberClients;
	//Counts the discarded requests
	public int discarted;
	//The starting time of the thread tester
	private long startTime;
	//If the servers are on or down
	public boolean serverOn;
	
	//PARAMETERS
	//Origin: -1 -> Random clients | X -> 1 Client with the id X
	public int origin;
	//Target: -1 -> Random Theaters | X -> [1, 1500] The theater id to be requested
	public int target;
	//Operations: 1- Only Queries (Request List, Request Seats, Cancel Reserve) |
	// 2- Queries and Reservations (Write to disk)
	public int operations;
	//Rate: the rate of requests for second [1,MAX_INT]
	public int rate;
	//duration of the thread in milisec: -1 - only ends when the Tester calls end();
	public long duration;
	//duration of a failure
	public long failureDuration;

	//AUX
	//writes the results to disk
	private FileWriter fis;
	//comunicate with server
	RMIInterface rmiServer;
	private String address;
	private int port;
	private GBBDServer server;

	TestThread(int type, FileWriter fis){
		this.type = type;
		this.end = false;
		this.numberRequests = 0;
		this.numberClients = 0;
		this.discarted = 0;
		this.fis = fis;
	}

	/**
	 * 
	 * @param origin
	 * @param target
	 * @param operations
	 * @param rate
	 * @param duration in sec
	 * @throws IOException 
	 */
	public void setParam(int origin, int target, int operations, int rate, int duration) throws IOException{
		this.origin = origin;
		this.target = target;
		this.operations = operations;
		this.rate = rate;
		this.duration = duration*1000;
		
		this.fis.write("Generator ID: " + this.getId()+"\n");
		this.fis.append("Type: Request generator \n");
		this.fis.append("Origin: " + this.origin + "\n");
		this.fis.append("Target: " + this.target + "\n");
		this.fis.append("Operations: " + this.operations + "\n");
		this.fis.append("Rate: " + this.rate + "\n");
		this.fis.append("Duration: " + duration + "\n");
		this.fis.append("Results:\n");
	}

	/**
	 * Initialize the params of the failure generator
	 * @param target
	 * @param operations number of failures to induce
	 * @param duration in sec
	 * @param duration 
	 * @throws NotBoundException 
	 * @throws IOException 
	 */
	public void setParam(int target, int operations, int duration, String address, int port, GBBDServer server) throws NotBoundException, IOException {
		
		System.out.println("Set param");
		
		this.target = target;
		this.operations = operations;
		this.duration = duration*1000;
		this.address = address;
		this.port = port;
		this.server = server;
		
		if(target==1){
			Registry registry=LocateRegistry.getRegistry(address, port);
			rmiServer = (RMIInterface)(registry.lookup("GBAppServer"));
		}
		
		this.fis.write("Generator ID: " + this.getId()+"\n");
		this.fis.append("Type: Failure generator \n");
		this.fis.append("Target: " + ((target == 1) ? "App Server" : "DB Server") + "\n");
		this.fis.append("Total Failures: " + this.operations + "\n");
		this.fis.append("Duration: " + this.duration + "\n");
		this.fis.append("---------------------------Results:\n");
	}

	/**
	 * @requires setParam();
	 */
	@Override
	public void run(){
System.out.println("RUN THREAD?");
		
		if(this.type == 1)
			runRequestGenerator();
		else
			runFailureGenerator();

		if(this.type == 1)
			try {
				fis.append("Thread: "+ this.getId() + "\n");
				fis.append("Clients: " + this.numberClients + "\n");
				fis.append("Requests: " + this.numberRequests + "\n");
				fis.append("Discarted: " + this.discarted + "\n");
				fis.append("Rate: " + this.rateOfResponse() + "\n");
				if(fis != null)
					fis.close();
			} catch (IOException e) {
				System.out.println("Couldnt write the statistics.");
			}
		else
			try{
				fis.append("Thread: "+ this.getId() + "\n");
				fis.append("Target: " + this.target+"\n");
				fis.append("Failures Generated: " + this.operations+"\n");
				if(fis != null)
					fis.close();
			}
		catch (IOException e) {
			System.out.println("Couldnt write the statistics.");
		}

	}

	private void runFailureGenerator() {
		this.startTime = System.currentTimeMillis();
System.out.println("Run failure gen");
		
		if(this.target == 1){
			int interrupts = 0;
			while(this.operations > interrupts){
				
				try {
					Thread.sleep(RANDOM.nextInt(20000)+5000);
				} catch (InterruptedException e2) {
					//carry on
				}
				
				try {
					this.rmiServer.interrupt(this.duration);
					interrupts++;
					Thread.sleep(this.duration);
				} catch (RemoteException e) {
					//SERVER IS BUSY
				} catch (NotBoundException e) {
					Registry registry;
					try {
						registry = LocateRegistry.getRegistry(this.address, this.port);
						rmiServer = (RMIInterface)(registry.lookup("GBAppServer"));
					} catch (RemoteException e1) {
						//DO NOTHING
					} catch (NotBoundException e1) {
						//DO NOTHING
					}
					
				} catch (InterruptedException e) {
					//DO NOTHING
				}
				
				
			}
		}else{
			
			int interrupts = 0;
			while(this.operations > interrupts){
				
				
				try {
					Thread.sleep(RANDOM.nextInt(20000)+5000);

				} catch (InterruptedException e2) {
					//carry on
				}
				
				//TesteConfDB.txt
								
				try {
					System.out.println("Timout!");
					server.sSoc.close();
					interrupts++;
					System.out.println("Chegou");
					Thread.sleep(this.duration);
					server = new GBBDServer();
					new Thread(){ GBBDServer server; public void initS(GBBDServer server) { this.server = server; this.start();}@Override public void run() { server.startServer();}}.initS(server);

				} catch (InterruptedException e) {
					//DO NOTHING
				} catch (IOException e) {
					//DO NOTHING
				}
				
			}
		}

	}

	private void runRequestGenerator() {
		GBClient [] clients = null;
		boolean [] abandon = null;

		//Initializes the clients to use
		clients = new GBClient[(this.origin == -1) ? RANDOM.nextInt(MAX_CLIENTS/20) : 1];
		abandon = new boolean[(this.origin == -1) ? clients.length : 1];
		for(int i = 0; i < clients.length; i++)
			try{
				clients[i] = new GBClient((this.origin == -1) ? RANDOM.nextInt(MAX_CLIENTS) : this.origin);
				this.numberClients++;
				this.serverOn = true;
			} catch (RemoteException e) {
				this.serverOn = false;
				clients[i] = null;
			} catch (NotBoundException e) {
				this.serverOn = false;
				clients[i] = null;
			}

		this.startTime = System.currentTimeMillis();
		
		System.out.println("Init: " + startTime);
		System.out.println("Dur:" + this.duration + " | Clients: " + this.numberClients);
		
		//Do random operations according to the parameters
		// Until the duration expired or the user requests the generator to end
		int operation;
		while(!end && (System.currentTimeMillis()-this.startTime) < this.duration){

			for(int i = 0; i < clients.length; i++){
				if((System.currentTimeMillis()-this.startTime) > this.duration)
					break;
				
				//Client is off
				if(clients[i] == null)
					try {
						clients[i] = new GBClient((this.origin == -1) ? RANDOM.nextInt(MAX_CLIENTS) : this.origin);
						this.numberClients++;
						this.serverOn = true;
					} catch (RemoteException e) {
						this.serverOn = false;
						clients[i] = null;
					} catch (NotBoundException e) {
						this.serverOn = false;
						clients[i] = null;
					}
				else{
					if(this.operations == 1)
						operation = RANDOM.nextInt(5);
					else
						operation = RANDOM.nextInt(7);
					
					if((System.currentTimeMillis()-this.startTime) > this.duration)
						break;
					
					
					if(!abandon[i])
						try {
							doOperation(clients[i], operation, abandon, i);
							this.serverOn = true;
						} catch (RemoteException e) {
							System.out.println("Exception");
							this.serverOn = false;
							this.discarted++;
						}

				}
			}
		}
		System.out.println("end: " + System.currentTimeMillis());

	}

	//Possible Operations
	//if this.operations = 1
	//0- Abandon (Remove client from the list or do nothing if only 1 client)
	//1- Cancel a request
	//2- Request a list of theaters
	//3- Request theater seats
	//4- Change Reserved Seat
	//if this.operations = 2
	//0 to 4
	//5- Purchase a reserved seat (if there is no seat it does nothing
	//6- Change Reserved Seat and Purchase
	private void doOperation(GBClient client, int operation, boolean[] abandon, int i) throws RemoteException {
		switch(operation){
		case 0:
			//abandon[i] = true;
			break;
		case 1: 
			//If has timedout or !connected (reconnect)
			if(client.isConnected())
				client.reconnect();

			client.cancelRequest();
			this.numberRequests++;
			break;
		case 2: 
			//If has timedout or !connected (reconnect)
			if(client.isConnected())
				client.reconnect();

			//Pede a lista de teatros de uma das 3 zonas
			this.numberRequests++;
			if(client.getTheatersList(RANDOM.nextInt(3)+1) == null){
				this.discarted++;
			}
			break;
		case 3:
			//If has timedout or !connected (reconnect)
			if(client.isConnected())
				client.reconnect();

			this.numberRequests++;
			if(client.requestTheaterSeats((this.target == -1) ? RANDOM.nextInt(1500)+1 : this.target) == null){
				this.discarted++;
			}

			break;
		case 4: 
			//If has timedout or !connected (reconnect)
			if(client.isConnected())
				client.reconnect();

			this.numberRequests++;
			client.setReservation((this.target == -1) ? RANDOM.nextInt(1500)+1 : this.target, RANDOM.nextInt(26*40));

			break;
		case 5:
			//If has timedout or !connected (reconnect)
			if(client.isConnected())
				client.reconnect();

			//If not requested seat
			if(client.getReservation() == -3){
				this.numberRequests++;
				int theaterID = (this.target == -1) ? RANDOM.nextInt(1500)+1 : this.target;

				if(client.requestTheaterSeats(theaterID) == null){
					this.discarted++;
				}

				this.numberRequests++;
				client.reserve(theaterID);
			}
			//If its full
			else if(client.getReservation() == -1){
				//Se estiver cheio o cleinte abandona? (Enunciado) ou cancela simplesmente?
				abandon[i] = true;
				break;
			}

			//If has a place
			this.numberRequests++;
			client.reserve(client.getReservationTheater());

			break;
		case 6: 
			//If has timedout or !connected (reconnect)
			if(client.isConnected())
				client.reconnect();

			this.numberRequests++;
			if(client.setReservation((this.target == -1) ? RANDOM.nextInt(1500)+1 : this.target, RANDOM.nextInt(26*40))){

				if(client.getReservation() == -1){
					//Se estiver cheio o cleinte abandona? (Enunciado) ou cancela simplesmente?
					//abandon[i] = true;
					break;
				}

				this.numberRequests++;
				client.reserve(client.getReservationTheater());
			}

			break;
		default: break;
		}

	}

	public int rateOfResponse(){
		int running = (int)(System.currentTimeMillis()-this.startTime);
		return (this.numberRequests-this.discarted)/(running/1000);
	}

	//Return the requests handle until server said end.
	public void end(){
		this.end = true;
		//System.out.println("Acabou!: " + this.getId());
		//return this.numberRequests;
	}
}
