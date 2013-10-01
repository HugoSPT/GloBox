package messages;

import java.util.LinkedList;
import domain.*;

public class TheatersListReplyMessage extends Message {

	private LinkedList<Theater> listTheaters;
	private int zone;
	
	public TheatersListReplyMessage(int zone, LinkedList<Theater> listTheaters) {
		this.listTheaters = listTheaters;
		this.zone = zone;
	}
	
	public LinkedList<Theater> getListTheaters(){
		return this.listTheaters;
	}
	
	public Theater getTheaterByIndex(int index){
		return this.listTheaters.get(index);
	}

	public int getZone() {
		return this.zone;
	}
}
