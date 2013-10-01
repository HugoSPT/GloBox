/**
 * The Catalog for Theaters. It contains all instances of Theaters
 * existent on the system. It is responsible to create, manage and 
 * remove Theaters.
 */

package gbdbserver;

import java.util.LinkedList;
import domain.*;

/**
 * @author Andre Matias, Hugo Sousa & Ruben Campos
 *
 */
public class TheaterCatalog {

	private static int cacheLimit = 100;
	private static LinkedList<Theater> theaters = new LinkedList<Theater>();

	/**
	 * Add a new theater to the theaters list
	 * @param theater the theater to insert on the list
	 */
	public synchronized static void addTheater(Theater theater){
		if(!theaters.contains(theater)){
			if(theaters.size() < cacheLimit)
				theaters.add(theater);
			else{
				for(int i=0;i<(cacheLimit/10);i++)
					theaters.removeFirst(); //Removes the oldest theater in the list
				theaters.add(theater);
			}
		}
		theaters.remove(theater);
		theaters.add(theater);
	}

	/**
	 * Adds all the given theaters to the theaters list
	 * @param theaters the theater List to insert on the list.
	 */
	public synchronized static void addTheater(LinkedList<Theater> TheTheaters){
		
		for(Theater theater: TheTheaters){
			if(!theaters.contains(theater)){
				if(theaters.size() < cacheLimit)
					theaters.add(theater);
				else{
					for(int i=0;i<(cacheLimit/10);i++)
						if(theaters != null)
							theaters.removeFirst(); //Removes the oldest theater in the list
					
					theaters.add(theater);
				}
			}
			else{
				theaters.remove(theater);
				theaters.add(theater);
			}
		}
	}

	/**
	 * The cache limit for the theaters.
	 * @return the cacheLimit
	 */
	public static int getCacheLimit() {
		return cacheLimit;
	}

	/**
	 * Resize the cache for the theaters.
	 * @param newCacheLimit the cacheLimit to set
	 */
	public synchronized static void setCacheLimit(int newCacheLimit) {
		TheaterCatalog.cacheLimit = newCacheLimit;
		if(theaters.size() > cacheLimit){
			LinkedList<Theater> aux = new LinkedList<Theater>();
			for(int i=theaters.size()-newCacheLimit; i < theaters.size();i++)
				aux.add((Theater) theaters.toArray()[i]);
			theaters = aux; 
		}
	}

	/**
	 * Remove the given theater from the list
	 * @param theater the theater to be removed
	 * @result true if the theater was successfully removed,
	 * false otherwise.
	 */
	public synchronized static boolean removeTheater(Theater theater){
		return theaters.remove(theater) ? true : false;
	}

	/**
	 * Remove the theater with the given id from the list
	 * @param id the id of the theater to be removed
	 * @result true if the theater was successfully removed,
	 * false otherwise.
	 */
	public synchronized static boolean removeTheater(int id){
		for(Theater t: theaters)
			if(t.getId() == id)
				return (theaters.remove(t));
		return false;
	}

	/**
	 * Get the theater that has the given id
	 * @param id id of the theater sought
	 * @return A Theater object correspondent to the given id. 
	 * If not found returns null.
	 */
	public synchronized static Theater getTheater(int id){
		for(Theater t: theaters){
			if(t.getId() == id){
				theaters.remove(t); 
				theaters.add(t); //this will ensure the cache stays updated
				return t;
			}
		}
		return null;
	}

	/**
	 * Get all theaters in the Catalog
	 * @return all theaters in the Catalog. If there are no theaters, 
	 * returns null.
	 */
	public synchronized static LinkedList<Theater> getAllTheaters(int zone){
		if(theaters.size() == 0)
			return null;
		LinkedList<Theater> result = new LinkedList<Theater>();
		for(Theater t : theaters)
			if(t.getZone() == zone)
				result.add(t);
		if(result.size() == 0)
			return null;
		return result;
	}

	/**
	 * Tells whether the cache is empty 
	 * @return true if it's empty, false otherwise 
	 */
	public static boolean isEmpty(){
		return theaters.isEmpty();
	}
}
