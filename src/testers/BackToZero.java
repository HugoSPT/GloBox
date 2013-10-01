package testers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

import domain.Theater;
import domain.TheaterSeats;

public class BackToZero {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File f;
		ObjectOutputStream out;
		char [][] seats = new char[26][40];
		
		for(int i =0 ; i < 26; i++)
			for(int j = 0; j < 40; j++)
				seats[i][j] = 'L';
		
		for(int i = 1; i <= 1500; i++){
			f = new File(i+".txt");
			if(!f.exists())
				f.createNewFile();
			out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
			out.writeObject(new TheaterSeats(i, seats));
			out.close();
			
		}
		/**
		File f1 = new File("NorthTheaters.txt");
		if(!f1.exists())
			f1.createNewFile();
		File f2 = new File("CenterTheaters.txt");
		if(!f2.exists())
			f2.createNewFile();
		File f3 = new File("SouthTheaters.txt");
		if(!f3.exists())
			f3.createNewFile();
		ObjectOutputStream out1 = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f1)));
		ObjectOutputStream out2 = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f2)));
		ObjectOutputStream out3 = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f3)));
		Random r = new Random();
		String alf = "ABCDEFGHIJKLMNOPQRSTUVXYWZ";
		for(int i = 1; i <= 1500; i++){
		
			int zone = r.nextInt(3)+1;
			String name = "";
			String movie;
			
			if (i < 10)
				name = ""+alf.charAt(i-1);
			else if(i < 100)
				name = ""+alf.charAt(i/10)+""+alf.charAt(i%10);
			else if(i < 1000)
				name = ""+alf.charAt(i/100)+""+alf.charAt(i%100/10)+""+alf.charAt(i%10);
			else if(i < 1501)
				name = ""+alf.charAt(i/1000)+""+alf.charAt(i%1000/100)+""+alf.charAt(i%100/10)+""+alf.charAt(i%10);
			
			movie = alf.charAt(r.nextInt(4))+""+alf.charAt(r.nextInt(4))+""+alf.charAt(r.nextInt(4))+""+alf.charAt(r.nextInt(4));
			
			switch(zone){
				case 1: out1.writeObject(new Theater(i, name, movie, 1)); System.out.println(i+"Nort");break;
				case 2: out2.writeObject(new Theater(i, name, movie, 2)); System.out.println(i+"cent");break;
				case 3: out3.writeObject(new Theater(i, name, movie, 3)); System.out.println(i+"sul");break;
			}
		}
		out1.close();
		out2.close();
		out3.close();
*/
	}
}
