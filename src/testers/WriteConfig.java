package testers;

import java.io.FileWriter;
import java.io.IOException;

public class WriteConfig {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		FileWriter f = new FileWriter("Config.txt");
		f.write("3\n");
		f.append("2\n");
		f.append("1\n");
		f.append("2\n");
		f.append("5\n");
		f.append("127.0.0.1\n");
		f.append("5665\n");
		f.append("FGen-sF-2-5.txt\n");
		f.append("1\n");
		f.append("-1\n");
		f.append("-1\n");
		f.append("2\n");
		f.append("-1\n");
		f.append("20\n");
		f.append("RGen-vC-vT-E-20.txt\n");
		f.append("1\n");
		f.append("-1\n");
		f.append("-1\n");
		f.append("1\n");
		f.append("-1\n");
		f.append("10\n");
		f.append("RGen-vC-vT-L-10.txt\n");
		f.close();

	}

}
