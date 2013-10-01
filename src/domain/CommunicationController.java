package domain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CommunicationController {

	ObjectOutputStream outStream;
	ObjectInputStream inStream;
	Socket mySocket;

	public CommunicationController(Socket mySocket) throws IOException{

		this.mySocket = mySocket;
		outStream = new ObjectOutputStream(mySocket.getOutputStream());
		inStream = new ObjectInputStream(mySocket.getInputStream());
	}

	public boolean writeObject(Object obj){
		try {
			outStream.writeObject(obj);
			outStream.flush();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public Object readObject(){
		try {
			return inStream.readObject();
		} catch (IOException e) {
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	public void closeConnection(){
		try {
			mySocket.close();
			inStream.close();
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
