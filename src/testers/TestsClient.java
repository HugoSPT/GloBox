package testers;

import gbdbserver.GBBDServer;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.Scanner;


public class TestsClient {

	/* File format:
	 * Line 0: Number of generators
	 * Line 1: Type of generator (1- Requests, 2- Failures)
	 * IF Requests Generator
*Line 2: where to connect (1-Client, 2-AppServer, 3-DB)
	 * Line 2: Origin (-1 - Random Clients, X - Client ID)
	 * Line 3: Target (-1 - Random Theaters, X - Theater ID)
	 * Line 4: Operations (1 - Read Only, 2- Read and Write)
	 * Line 5: Requests Rate (per sec)
	 * Line 6: Duration of the execution (in sec)
	 * Line 7: Where to store the results.
	 * IF Failures Generator
	 * Line 2: Target (1- App Server, 2- DB)
	 * Line 3: Number (the total number of failures to induce, -1 - random)
	 * Line 4: Duration of the failure
	 * Line 5: address of the target
	 * Line 6: port of the target
	 * Line 7: where to store the results
	 * Lines 1 to 7 repeat for each generator a number of times in Line 0.
	 */

	public static void main(String [] args) throws IOException{
		TestThread [] threads = null;
		GBBDServer server = null;
		
		Scanner leitor = new Scanner(System.in);
		//System.out.println("Where to store the results: ");
		//FileWriter fis = new FileWriter(leitor.nextLine());


		System.out.println("Where to read the params? (1- keyboard, 2- file) ");
		int src = Integer.parseInt(leitor.nextLine());
		String filename = "";
		if(src == 2){
			System.out.print("Config file: ");
			filename = leitor.nextLine();
			
		}
		Scanner fif = (src == 2) ? new Scanner(new FileReader(filename)) : leitor;
		
		if(src == 1)
			System.out.print("How many generators: ");
		int generators = Integer.parseInt(fif.nextLine());
		threads = new TestThread[generators];
		for(int i = 0; i < generators; i++){
			if(src == 1)
				System.out.print("What kind of Generator: (1- Requests, 2- Failure) ");
			int type = Integer.parseInt(fif.nextLine());
			threads[i] = (type == 1) ? createRequestGenerator(fif, src) : createFailureGenerator(fif, src, server);
		}
		
		fif.close();
		
		for (int i = 0; i < threads.length; i++){
			if(i != 0 && threads[i-1].isAlive())
				i--;
			else{
				System.gc();
				threads[i].start();
			}
		}

		//long start = System.currentTimeMillis();
		//while(System.currentTimeMillis() - start < maxDuration+10000);
		boolean running = true;
		do{
			running = false;
			for(TestThread t : threads)
				if(t.isAlive())
					running = true;
		}while(running);

	}

	public static TestThread createRequestGenerator(Scanner canal, int src){
		if(src == 1)
			System.out.print("Origin: ");
		int origin = Integer.parseInt(canal.nextLine());

		if(src == 1)
			System.out.print("Target: ");
		int target = Integer.parseInt(canal.nextLine());

		if(src == 1)
			System.out.print("Operations: ");
		int operations = Integer.parseInt(canal.nextLine());

		if(src == 1)
			System.out.print("Rate: ");
		int rate = Integer.parseInt(canal.nextLine());

		if(src == 1)
			System.out.print("Duration: ");
		int duration = Integer.parseInt(canal.nextLine());

		if(src == 1)
			System.out.print("Results File:");
		String results = canal.nextLine();

		TestThread t = null;
		try {
			t = new TestThread(1, new FileWriter(results));
			t.setParam(origin, target, operations, rate, duration);
		} catch (IOException e) {
			return null;
		}

		
		return t;

	}

	public static TestThread createFailureGenerator(Scanner canal, int src, GBBDServer server){
		if(src == 1)
			System.out.print("Target: ");
		int target = Integer.parseInt(canal.nextLine());
		
		if(src == 1)
			System.out.print("Operations: ");
		int operations = Integer.parseInt(canal.nextLine());
		
		if(src == 1)
			System.out.print("Duration of failure: ");
		int duration = Integer.parseInt(canal.nextLine());
		
		if(src == 1)
			System.out.print("Server Address: ");
		String address = canal.nextLine();
		
		if(src == 1)
			System.out.print("Server port: ");
		int port = Integer.parseInt(canal.nextLine());
		
		if(src == 1)
			System.out.print("Results File: ");
		String result = canal.nextLine();
				
		try {
			
			System.out.println("Leu do ficheiro vai threadar");
			
			if(target == 2 && server == null){
				server = new GBBDServer();
				new Thread(){ GBBDServer server; public void initS(GBBDServer server) { this.server = server; this.start();}@Override public void run() { server.startServer();}}.initS(server);

			}
			TestThread t = new TestThread(2, new FileWriter(result));
			t.setParam(target, operations, duration, address, port, server);
			return t;
		} catch (NotBoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		
	}


}
