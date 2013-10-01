/**
 * The Theater, which represents the physical place where the show takes place
 */
package domain;

import java.io.Serializable;

/**
 * @author Andre Matias, Hugo Sousa & Ruben Campos
 *
 */
public class TheaterSeats implements Serializable{
	
	private static final long serialVersionUID = 7963613452896879063L;
	
	private int id;
	private char[][] seatStatus;
	/**
	 * @param id
	 * @param seatStatus
	 */
	public TheaterSeats(int id, char[][] seatStatus) {
		this.id = id;
		this.seatStatus = seatStatus;
	}
	/**
	 * @return the seatStatus
	 */
	public char getSeatStatus(int line, int column) {
		return seatStatus[line][column];
	}
	/**
	 * @param seatStatus the seatStatus to set
	 */
	public void setSeatStatus(int line, int column, char seatStatus) {
		this.seatStatus[line][column] = seatStatus;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	public TheaterSeats clone() {
		return new TheaterSeats(this.id, this.seatStatus.clone());
	}
	public int getFirstFreeSeat() {
		for(int i = 0; i < seatStatus.length; i++)
			for(int j = 0; j < seatStatus[i].length; j++)
				if(seatStatus[i][j] == 'L')
					return i*40+j;
		return -1;
	}
	
	public void update(TheaterSeats theaterSeats) {
		for(int i = 0; i < seatStatus.length; i++)
			for(int j = 0; j < seatStatus[i].length; j++)
				if(theaterSeats.getSeatStatus(i, j) == 'O')
					this.seatStatus[i][j] = 'O';
	}

	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < seatStatus.length; i++) {
			for (int j = 0; j < seatStatus[0].length; j++) {
				sb.append(seatStatus[i][j]+" ");
			}
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
