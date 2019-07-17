package simpath;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import utilities.AttributeSetting;

/*
 * La classe ha lo scopo di interfacciarsi con l'utente: prendere in input i possibili valori degli attributi categorici dei
 * nodi e la distribuzione di probabilita' scelta dall'utente. Legge il grafo da un file generato da RStudio e arricchito dei
 * pesi dalla classe di utilita' "AddWeightDataset_InDegree" e lancia la computazione di Simpath stampando a video il risultato.
 * 
 */
public class Launcher {
	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		double alpha = -1.0;
		int numAttr = 0;

		String nameDataSet = "pa_R2";

		// Fase di interfacciamento con l'utente
		System.out.print("Inserisci il numero di attributi che ha ogni nodo: ");
		numAttr = Integer.parseInt(br.readLine());
		// legge i valori dei pesi della combinazione lineare che definira' la
		// diversita'
		double[] weightsDivAttributes = new double[numAttr];

		while (true) {
			System.out.print("Inserisci il valore di pesi da associare ad ogni attributo, separati da spazi"
					+ "(la somma deve essere uguale a 1): ");
			LinkedList<Double> weights = new LinkedList<>();
			String line = br.readLine();
			StringTokenizer st = new StringTokenizer(line);
			String token = st.nextToken();
			while (true) {
				weights.add(Double.parseDouble(token));
				try {
					token = st.nextToken();
				} catch (NoSuchElementException e) {
					break;
				}
			}
			double check = 0D;
			int elem = 0;
			for (Double d : weights) {
				check += d;
				elem++;
			}
			if (check == 1.0 && elem == weightsDivAttributes.length) {
				System.out.println();
				for (int i = 0; i < weightsDivAttributes.length; i++) {
					weightsDivAttributes[i] = weights.get(i);
				}
				break;
			}
		}

		// lettura di alpha
		while (true) {
			System.out.print("Inserisci il valore di alpha (compreso tra 0 e 1, maggiore se si vuole dare più "
					+ "peso allo spread rispetto alla diversità: ");
			alpha = Double.parseDouble(br.readLine());
			if (alpha <= 1 && alpha >= 0)
				break;
		}
		// lettura del grafo
		Graph g = new Graph(alpha);
		g.buildGraph(nameDataSet + "Weighted.txt");
		// g.printGraph();

		System.out.print(
				"Inserisci il nome del file dal quale leggere gli attributi oppure invio se vuoi settarli ora: ");
		String line = br.readLine();
		if (line.equals("")) {
			AttributeSetting as = new AttributeSetting(numAttr, g.getNodeSet().size(), nameDataSet);
			as.setAttributes();
			readAttributes(g, nameDataSet + "Attr.txt", numAttr);
		} else {
			readAttributes(g, line, numAttr);
		}

		System.out.println("\ntest Simpath");
		System.out.print(
				"Inserisci il valore di threshold relativo alla lunghezza dei path considerati (si consiglia non minore di 0.001): ");
		double threshold = Double.parseDouble(br.readLine());
		System.out.print("Inserisci la dimensione del seed set desiderato: ");
		int seedSetSize = Integer.parseInt(br.readLine());
		System.out.println("Vuoi che l'esecuzione sia multithread (S/N): ");
		String input = br.readLine();
		boolean multiT=false;
		if(input.toUpperCase().equals("S")) 
			multiT=true;
		
		int topL = 5;
		Simpath s = new Simpath(g, threshold, topL, seedSetSize, numAttr, weightsDivAttributes,multiT);
		long time = System.currentTimeMillis();
		LinkedList<Node> res = s.simpath();
		long timestamp = System.currentTimeMillis() - time;
		System.out.println("valore alpha (peso dello spread rispetto alla diversita': " + alpha);
		System.out.println("seed set ottenuto:\n" + res);
		System.out.println("\nTempo di esecuzione: " + timestamp);

	}

	private static void readAttributes(Graph g, String name, int numAttr) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(name));
		BufferedReader brCons = new BufferedReader(new InputStreamReader(System.in));

		String line = br.readLine();
		int header = Integer.parseInt(line);

		while (true) {
			if (numAttr == header) {
				break;
			} else {
				System.out.println(
						"ERRORE il numero di parametri dichiarato è diverso da quello del file, inserisci il nuovo filename: ");
				line = brCons.readLine();
				br = new BufferedReader(new FileReader(line));
				line = br.readLine();
				header = Integer.parseInt(line);
			}
		}

		line = br.readLine();
		StringTokenizer st;
		while (line != null) {
			st = new StringTokenizer(line);
			int n = Integer.parseInt(st.nextToken());
			LinkedList<String> attrs = new LinkedList<>();
			String token = st.nextToken();
			while (true) {
				attrs.add(token);
				try {
					token = st.nextToken();
				} catch (NoSuchElementException e) {
					break;
				}
			}
			g.getNodeSet().get(n).setAttributes(attrs);
			line = br.readLine();
		}
		br.close();

	}
}
