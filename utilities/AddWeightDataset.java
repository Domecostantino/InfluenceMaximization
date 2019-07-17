package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;



/*
 * Questa classe associa un peso ad ogni arco del grafo generato con RStudio, restituendo in output un nuovo
 * file nella forma <nomeFileInput+"Weighted.txt">. Il peso sugli archi viene settato utilizzando la classe Random di
 * Java e in modo che la somma dei pesi degli archi che entrano in un determinato nodo sia <=1.
 * 
 */
public class AddWeightDataset {
	public static void main(String[] args) throws IOException {
		String name = "gnu";

		File fout = new File(name + "Weighted.txt");
		BufferedReader br = new BufferedReader(new FileReader(name + ".txt"));
		PrintWriter pw = new PrintWriter(new FileWriter(fout));
		HashMap<Integer, Double> valIn = new HashMap<>(); // serve per evitare che la somma dei pesi degli archi
															// entranti in un nodo sia <=1
		
		Random r = new Random();
		// scrive l'header
		String line = br.readLine();
		pw.println(line);
		
		for (int j = 0; j < Integer.parseInt(line); j++) {
			valIn.put(j, (double) 0);
		}

		line = br.readLine();
		
		//per ogni arco
		while (line != null) {
			StringTokenizer st = new StringTokenizer(line);
			int source = Integer.parseInt(st.nextToken());
			int dest = Integer.parseInt(st.nextToken());

			//assegnamento del peso all'arco
			double tries = 1.0;
			while (true) {
				double weight = r.nextDouble() * 0.5 / tries;
				if (valIn.get(dest) + weight < 1 && weight > 0.001) {
					valIn.put(dest, valIn.get(dest) + weight);
					pw.println(source + " " + dest + " " + String.format(Locale.US, "%.4f", weight));
					pw.flush();
					break;
				}
				tries += 1.0;
			}
			line = br.readLine();

		}

		br.close();
		pw.close();
	}
}
