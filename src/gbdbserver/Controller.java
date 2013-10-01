/**
 * The class responsible to control the theater and its seats.
 * Its main purpose is to manage reading and writing procedures and managing
 * the theaters and seats.
 */
package gbdbserver;

import domain.*;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

/**
 * @author Andre Matias, Hugo Sousa & Ruben Campos
 *
 */
public class Controller {

	private File NorthTheatersFile, CenterTheatersFile, SouthTheatersFile;
	private static String workingDir = System.getProperty("user.dir")+File.separator+"files";

	/**
	 * Constructs a new Controller for the theaters and seats
	 * The files to be manipulated are created if not already present.
	 */
	public Controller() {

		NorthTheatersFile = new File(workingDir + File.separator + "NorthTheaters.txt");
		CenterTheatersFile = new File(workingDir + File.separator + "CenterTheaters.txt");
		SouthTheatersFile = new File(workingDir + File.separator + "SouthTheaters.txt");

	}
	/**
	 * Retrives the theaterSeats of the theater with the given id. First searches
	 * the DB cache for it, then, if couln't be found, on disk. If both fail to 
	 * find the theaterSeats, returns null.
	 * @param id the theater identification
	 * @return the theater sought, or null, if it couldn't be retrieved.
	 * @throws IOException
	 */
	public TheaterSeats getTheaterSeats(int id) throws IOException{

		TheaterSeats seats = TheaterSeatsCatalog.getTheaterSeats(id); 
		//if it isn't on cache, then we must seek on the disk
		if(seats == null){
			seats = loadSeatsFromDisk(new File(workingDir + File.separator +id +".txt"));
			if(seats != null){
				TheaterSeatsCatalog.addTheaterSeats(seats);//Updates the cache
				return seats;
			}
			//wasn't on disk either.
		}
		//returns null because it wasn't found on disk.
		return seats;			
	}

	/**
	 * Returns all the Theaters in the System. Depending on the zone,
	 * it returns all the theaters in either North, Center or South file.
	 * @param zone the zone of which theaters will be returned:
	 * @return a LinkedList of theaters, as requested by zone.
	 */
	public LinkedList<Theater> getAllTheaters(int zone){
		LinkedList<Theater> result;



		switch(zone){
		case 1: 
			result = TheaterCatalog.getAllTheaters(zone);
			if(result == null)
				result = loadTheatersFromDisk(NorthTheatersFile);

			TheaterCatalog.addTheater(result);
			return result;
		case 2:
			result = TheaterCatalog.getAllTheaters(zone);
			if(result == null)
				result = loadTheatersFromDisk(CenterTheatersFile);

				TheaterCatalog.addTheater(result);
				
			return result;
		case 3: 
			result = TheaterCatalog.getAllTheaters(zone);
			if(result == null)
				result = loadTheatersFromDisk(SouthTheatersFile);

			TheaterCatalog.addTheater(result);
			return result;
		default: return null; 
		}

	}

	/**
	 * Loads the theaters in a file, according to the file given.
	 * @param file the file to be read.
	 * @return a LinkedList of the theaters in that file.
	 */
	private LinkedList<Theater> loadTheatersFromDisk(File name){

		FileInputStream fis;
		ObjectInputStream ois;
		Theater theater = null;
		LinkedList<Theater> result = new LinkedList<Theater>();

		try {
			fis = new FileInputStream(name);
			ois = new ObjectInputStream(fis);

			do{
				try{
					theater = (Theater) ois.readObject();

					result.add(theater);

				}catch (EOFException e) {
					break;
				} catch (ClassNotFoundException e) {
					return null;
				}

			}while(theater != null);

			ois.close();
			fis.close();

			return result;

		} catch (FileNotFoundException e1) {
			return null;
		} catch (IOException e) {
			return null;
		}

	}

	/**
	 * Loads the theaterSeats in a file, according to the file given.
	 * @param file the file to be read.
	 * @return the theaterSeats in that file.
	 */
	private TheaterSeats loadSeatsFromDisk(File name){

		FileInputStream fis;
		ObjectInputStream ois;
		TheaterSeats theaterSeats = null;
		try {
			fis = new FileInputStream(name);
			ois = new ObjectInputStream(fis);

			try{
				theaterSeats = (TheaterSeats) ois.readObject();
			}catch (EOFException e) {
				//carry on!
			}

			ois.close();
			fis.close();

			return theaterSeats;

		} catch (FileNotFoundException e) {
			System.out.println("loadSeats() - File Not Found.");
			return null;
		} catch (IOException e) {
			System.out.println("loadSeats() - Fatal Error.");
			return null;
		} catch (ClassNotFoundException e) {
			System.out.println("loadSeats() - No TheaterSeats Found.");
			return null;
		}

	}

	/**
	 * Reserves the seat at the given column and row. The seat is in the theater
	 * with the given id.
	 * @param theaterId the TheaterSeats identification
	 * @param line the row of the TheaterSeats
	 * @param column the column of the TheaterSeats
	 * @return true is the reservation could be made. False otherwise.
	 * The reservation is made only if the status of the seat is L.
	 */
	public boolean reserveSeat(int theaterId, int line, int column){

		if(line == -1 || column == -1)
			return false;

		TheaterSeats seats = TheaterSeatsCatalog.getTheaterSeats(theaterId); 
		//if it isn't on cache, then we must seek on the disk
		if(seats == null){

			seats = loadSeatsFromDisk(new File(workingDir + File.separator + theaterId +".txt"));
			//Was on the disk
			if(seats != null){
				TheaterSeatsCatalog.addTheaterSeats(seats); //Add it to the cache
				if(seats.getSeatStatus(line, column) != 'O'){
					TheaterSeatsCatalog.setSeatStatus(theaterId, line, column, 'O');
					writeToDisk(seats);

					return true;
				}
				return false; //couldn't be reserved.
			}
			return false;//couldn't be found on disk, either.
		}
		//It's on cache, so we check right from the cache
		if(seats.getSeatStatus(line, column) != 'O'){
			seats.setSeatStatus(line, column, 'O'); //make the reservation
			writeToDisk(seats); //write the new state of the theater to disk
			TheaterSeatsCatalog.addTheaterSeats(seats); //Update the cache
			return true;
		}
		return false;
	}
	/**
	 * Writes to the file with the id found on the Object seats.
	 * @param status the new status of the seat
	 * @param line the line of the seat (0 to 25)
	 * @param column the column of the seat (0 to 39)
	 * @param seats the Object theaterSeats to be manipulated
	 * @return true if the write was successful, false otherwise
	 */
	private synchronized boolean writeToDisk(TheaterSeats seats){

		FileOutputStream fos; 
		ObjectOutputStream oos; 

		File theFile = new File(workingDir + File.separator + seats.getId() +".txt");

		try {
			fos = new FileOutputStream(theFile);
			oos = new ObjectOutputStream(fos);

			oos.writeObject(seats);

			oos.flush();
			oos.close();
			fos.close();

			return true;
		} catch (FileNotFoundException e) {
			System.out.println("WriteFile() - File Not Found.");
			return false;
		} catch (IOException e) {
			System.out.println("WriteFile() - Fatal Error.");
			return false;
		}
	}

}
