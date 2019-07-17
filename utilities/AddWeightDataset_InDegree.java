package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;

/*
 * Questa classe associa un peso ad ogni arco del grafo generato con RStudio, restituendo in output un nuovo
 * file nella forma <nomeFileInput+"Weighted.txt">. Il peso sugli archi viene settato utilizzando la classe Random di
 * Java e in modo che la somma dei pesi degli archi che entrano in un determinato nodo sia <=1. La scelta del peso dipende
 * in maniera inversamente proporzionale all'inDegree del nodo destinazione (in modo che sia maggiore nel caso di inDegre piccolo).
 */
public class AddWeightDataset_InDegree {
	public static void main(String[] args) throws IOException {
		String name = "pa.model5000";
		
		
		//fase preliminare (invertire gli archi di barabasi-albert e ordinarli)
		BufferedReader brINV = new BufferedReader(new FileReader(name + ".txt"));
		File fInv = new File(name+"INV.txt");
		PrintWriter pwINV = new PrintWriter(new FileWriter(fInv));
		StringTokenizer st = null;
		LinkedList<Direction> arcs = new LinkedList<>();
		
		String line = brINV.readLine();
		int header = Integer.parseInt(line);
		
		line = brINV.readLine();
		while (line != null) {
			st = new StringTokenizer(line);
			int i = Integer.parseInt(st.nextToken());
			int j = Integer.parseInt(st.nextToken());
			arcs.add(new Direction(j, i));
			
			line = brINV.readLine();
		}
		
		Comparator<Direction> comp = new Comparator<Direction>() {
			
			@Override
			public int compare(Direction o1, Direction o2) {
				if(o1.source>o2.source)
					return 1;
				if(o1.source==o2.source && o1.dest>o2.dest)
					return 1;
				return -1;
			}
		};
		
		Collections.sort(arcs, comp);
		
		pwINV.println(header);
		for (Direction d : arcs) {
			pwINV.println(d.source+" "+d.dest);
			pwINV.flush();
		}
		pwINV.close();
		brINV.close();
		
		

		// prima fase (calcolo dell'inDegree dei nodi)
		BufferedReader brTMP = new BufferedReader(new FileReader(name + "INV.txt"));
		HashMap<Integer, Integer> inDegreeMap = new HashMap<>();
		

		line = brTMP.readLine();
		header = Integer.parseInt(line);
		for (int i = 0; i < header; i++) {
			inDegreeMap.put(i, 0);
		}
		
		

		line = brTMP.readLine();
		while (line != null) {
			st = new StringTokenizer(line);
			int j = Integer.parseInt(st.nextToken());
			inDegreeMap.put(j, inDegreeMap.get(j) + 1);
			
			
			line = brTMP.readLine();
		}
		brTMP.close();
		
		

		// Seconda fase (generazione dei pesi)
		File fout = new File(name + "Weighted.txt");
		BufferedReader br = new BufferedReader(new FileReader(name + "INV.txt"));
		PrintWriter pw = new PrintWriter(new FileWriter(fout));
		HashMap<Integer, Double> valIn = new HashMap<>(); // serve per evitare che la somma dei pesi degli archi
															// entranti in un nodo sia <=1
		Random r = new Random();
		// scrive l'header
		line = br.readLine();
		pw.println(line);

		for (int j = 0; j < Integer.parseInt(line); j++) {
			valIn.put(j, (double) 0);
		}

		int maxInDegree = 0;
		for (Integer d : inDegreeMap.values()) {
			if (d > maxInDegree)
				maxInDegree = d;
		}

		line = br.readLine();

		// per ogni arco
		while (line != null) {
			st = new StringTokenizer(line);
			int source = Integer.parseInt(st.nextToken());
			int dest = Integer.parseInt(st.nextToken());

			// calcolo del peso
			while (true) {
				double weight = r.nextDouble() * (1 / (double) maxInDegree);
				if (valIn.get(dest) + weight < 1 && weight > 0.001) {
					valIn.put(dest, valIn.get(dest) + weight);
					pw.println(source + " " + dest + " " + String.format(Locale.US, "%.4f", weight));
					pw.flush();
					break;
				}

			}
			line = br.readLine();

		}

		br.close();
		pw.close();
	}
}
