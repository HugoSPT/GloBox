package gbdbserver;
/**
 * The Catalog for TheaterSeats. It contains all instances of TheaterSeats
 * existent on the system. It is responsible to create, manage and 
 * remove TheaterSeats.
 */
import domain.*;
import java.util.LinkedList;

/**
 * @author Andre Matias, Hugo Sousa & Ruben Campos
 *
 */
public class TheaterSeatsCatalog {

	private static int cacheLimit = 100;
	private static LinkedList<TheaterSeats> seats = new LinkedList<TheaterSeats>();

	/**
	 * Add a new TheaterSeats to the theaters list.
	 * @param theater the theater seats to insert on the list.
	 */
	public synchronized static void addTheaterSeats(TheaterSeats theSeats){
		if(!seats.contains(theSeats)){
			if(seats.size() < cacheLimit)
				seats.add(theSeats);
			else{
				for(int i=0;i<(cacheLimit/10);i++)
					seats.removeFirst(); //Removes the oldest seat in the list
				seats.add(theSeats);
			}			
		}
		else{
			seats.remove(theSeats);
			seats.add(theSeats);
		}
	}

	/**
	 * The cache limit for the movies.
	 * @return the cacheLimit
	 */
	public static int getCacheLimit() {
		return cacheLimit;
	}

	/**
	 * Resize the cache for the movies.
	 * @param cacheLimit the cacheLimit to set
	 */
	public synchronized static void setCacheLimit(int newCacheLimit) {
		TheaterSeatsCatalog.cacheLimit = newCacheLimit;
		if(seats.size() > cacheLimit){
			LinkedList<TheaterSeats> aux = new LinkedList<TheaterSeats>();
			for(int i=seats.size()-newCacheLimit; i < seats.size();i++)
				aux.add((TheaterSeats) seats.toArray()[i]);
			seats = aux; 
		}
	}

	/**
	 * Remove the given movie from the list.
	 * @param theSeats the movie to be removed.
	 * @result true if the movie was successfully removed,
	 * false otherwise.
	 */
	public synchronized static boolean removeTheaterSeats(TheaterSeats theSeats){
		return seats.remove(theSeats) ? true : false;
	}

	/**
	 * Remove the movie with the given id from the list.
	 * @param id the id of the movie to be removed.
	 * @result true if the movie was successfully removed,
	 * false otherwise.
	 */
	public synchronized static boolean removeTheaterSeats(int id){
		for(TheaterSeats m: seats)
			if(m.getId() == id)
				return (seats.remove(m));
		return false;
	}

	/**
	 * Get the seats that has the given id.
	 * @param id id of the seats sought.
	 * @return A TheaterSeats object correspondent to the given id. 
	 * If not found returns null.
	 */
	public synchronized static TheaterSeats getTheaterSeats(int id){
		boolean found = false;
		TheaterSeats t = null;
		for(int i = 0; i < seats.size(); i++){
			t = seats.get(i);
			if(t != null && t.getId() == id){
				found = true;
				break;

			}
		}
		if(found){
			seats.remove(t);
			seats.add(t);
			return t;
		}
		return null;	
	}

	/**
	 * Get all movies in the Catalog.
	 * @return all movies in the Catalog. If there are no movies, 
	 * returns null.
	 */
	public static LinkedList<TheaterSeats> getAllTheaterSeats(){
		if(seats.size() == 0)
			return null;
		return seats;
	}

	/**
	 * Tells whether the cache is empty. 
	 * @return true if it's empty, false otherwise. 
	 */
	public static boolean isEmpty(){
		return seats.isEmpty();
	}

	/**
	 * Changes the status of the seat, with location at (row, column),
	 * to the given status.
	 * @param id the TheaterSeats Object identification.
	 * @param row the row of the TheaterSeats.
	 * @param column the column of the TheaterSeats.
	 * @param status the status to be changed to.
	 */
	public synchronized static void setSeatStatus(int id, int row, int column, char status){
		TheaterSeats aux;
		for(int i = 0; i < seats.size(); i++){
			aux = seats.get(i);
			if(aux.getId() == id){	
				aux.setSeatStatus(row, column, status);
				addTheaterSeats(aux);
			}
		}
	}
}

