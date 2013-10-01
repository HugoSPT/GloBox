package domain;

import java.io.Serializable;

public class Theater implements Serializable {

	private static final long serialVersionUID = 7298299060636925560L;
	
	private int id;
	private String name;
	private String movie;
	private int zone;
	
	public Theater(int id, String name, String movie, int zone) {
		this.id = id;
		this.movie = movie;
		this.name = name;
		this.zone = zone;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the movie
	 */
	public String getMovie() {
		return movie;
	}
	
	public int getZone() {
		return this.zone;
	}
	
	public String toString(){
		return "(" + this.id + " - " + this.name + " - " + this.movie + " - " + this.zone + ")";
	}

}
