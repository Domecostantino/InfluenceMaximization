package utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

public class CreateDataset {
	
	
	/*
	 * permette di creare un dataset ad hoc che condivide le proprieta richieste
	 * con un insieme di archi pesati e nodi con un attributo categorico
	 * 
	 * CLASSE UTILIZZATA NELLA FASE DI TEST E POI MESSA DA PARTE DATO CHE IL GRAFO VIENE 
	 * GENERATO CON RSTUDIO E ARRICCHITO DEL PESO CON LE CLASSI "ADDWEIGHT" E DEGLI ATTRIBUTI
	 * CON PROBABILITIES
	 * 
	 */
	public static void main(String[] args) throws IOException {

		String name = "a";
		File fout = new File(name + ".txt");
		PrintWriter pw = new PrintWriter(new FileWriter(fout));
		int nodes = 120;
		pw.println(nodes);
		pw.flush();
		int maxArcs = 15;
		HashSet<Integer> done = new HashSet<>();
		HashMap<Integer, Double> valIn = new HashMap<>(); // serve per evitare che la somma dei pesi degli archi
															// entranti in un nodo sia <=1
		for (int j = 0; j < nodes; j++) {
			valIn.put(j, (double) 0);
		}

		// creazione file archi
		int i = 0;
		while (i < nodes) {
			int arcs = (int) Math.round(Math.random() * maxArcs);
			int a = 0;
			while (a < arcs) {
				int dest = (int) Math.floor(Math.random() * nodes);
				if (dest != i && !done.contains(dest)) {
					double w = Math.random() * 0.5;
					if (valIn.get(dest) + w < 0.99 && w > 0.01) {
						valIn.put(dest, valIn.get(dest) + w);
						pw.println(i + "\t" + dest + "\t" + String.format(Locale.US, "%.2f", w));
						pw.flush();
						done.add(dest);
					}
				}
				a++;
			}
			done.clear();
			i++;
		}
		pw.close();

		// creazione file nodi
		File foutNode = new File(name + "Node.txt");
		String[] attributes = { "studente", "professore", "ricercatore", "segretario" };
		double[] cumProbabilities = { 0.55, 0.8, 0.95, 1 };
		pw = new PrintWriter(new FileWriter(foutNode));
		pw.println(nodes);
		pw.flush();

		//associa un valore dell'attributo ad ogni nodo
		i = 0;
		while (i < nodes) {
			pw.print(i + "\t");
			pw.flush();
			String attr = "";
			double r = Math.random();
			if (r < cumProbabilities[0]) {
				attr = attributes[0];
			} else if (r < cumProbabilities[1]) {
				attr = attributes[1];
			} else if (r < cumProbabilities[2]) {
				attr = attributes[2];
			} else {
				attr = attributes[3];
			}
			pw.println(attr);
			pw.flush();
			i++;
		}

		pw.close();

	}
}
