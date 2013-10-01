package testers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import domain.Theater;

public class WritesTest {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		
		/*ObjectOutputStream 	out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("test.txt")));
		out.writeObject(new Theater(1, "NAME", "THEATER", 1));
		out.writeObject(new Theater(2, "NAME2", "THEATER2", 2));
		out.close();*/
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream("test.txt")));
		Object o = in.readObject();
		Theater t;
		LinkedList<Theater> ts = new LinkedList<Theater>();
		while( o != null){
			
			t = (Theater) o;
			ts.add(t);
			try {
				o = in.readObject();
			} catch (EOFException e) {
				break;
			}
			System.out.println("--");
			if( o == null)
				System.out.println("Null");
		}
		System.out.println("t size: " + ts.size() +", " + ts.getFirst().toString());
		
		in.close();
	}

}
