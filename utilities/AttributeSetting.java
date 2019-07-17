package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class AttributeSetting {
	int numAttr;
	int numNodes;
	String file;
	
	
	public AttributeSetting(int numAttr, int numNodes, String input) {
		this.numAttr = numAttr;
		this.numNodes = numNodes;
		this.file = input;
	}
	
	public void setAttributes() throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		char distrProb = 0;
		File fOut = new File(file+"Attr.txt");
		PrintWriter pw = new PrintWriter(new FileWriter(fOut));


		@SuppressWarnings("unchecked")
		LinkedList<String>[] attributes = new LinkedList[numAttr];

		String line = "";
		while (true) {
			System.out.print("Vuoi che le probabilità di ogni valore di ogni attributo venga settato: \n"
					+ "manualmente (M), con una distribuzione prob. uniforme (U),"
					+ " oppure con una distribuzione di prob. esponenziale negativa (E): ");
			line = br.readLine();
			if (Character.toUpperCase(line.charAt(0)) == 'U' || Character.toUpperCase(line.charAt(0)) == 'E'
					|| Character.toUpperCase(line.charAt(0)) == 'M') {
				distrProb = line.charAt(0);
				break;
			}
		}

		double lambda = 0D;
		if (Character.toUpperCase(distrProb) == 'E') {
			System.out.print("Inserisci il valore di lambda dell'esponenziale: ");
			lambda = Double.parseDouble(br.readLine());
		}

		@SuppressWarnings("unchecked")
		LinkedList<Double>[] manualIntervals = new LinkedList[numAttr];

		// per ogni attributo
		for (int i = 0; i < numAttr; i++) {

			// Lettura dei valori del singolo attributo
			System.out.print("\nPer l'attributo " + (i + 1) + " inserire i possibili valori separati da spazi: ");
			LinkedList<String> values = new LinkedList<>();
			line = br.readLine();
			StringTokenizer st = new StringTokenizer(line);
			String token = st.nextToken();
			while (true) {
				values.add(token);
				try {
					token = st.nextToken();
				} catch (NoSuchElementException e) {
					break;
				}
			}
			attributes[i] = values;

			if (Character.toUpperCase(distrProb) == 'M') {
				while (true) {
					System.out.print("Inserisci il pesi da associare ad ogni valore dell'attributo " + (i + 1)
							+ ", separati da spazi" + " (la somma deve essere uguale a 1): ");
					LinkedList<Double> weights = new LinkedList<>();
					line = br.readLine();
					st = new StringTokenizer(line);
					token = st.nextToken();
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
					if (check == 1.0 && elem == values.size()) {
						manualIntervals[i] = weights;
						break;
					}
				}
			}

		}

		LinkedList<String>[] NodeAttributes = null;

		switch (Character.toUpperCase(distrProb)) {
		case 'M': {
			NodeAttributes = Probabilities.setManual(numNodes, attributes, manualIntervals);
			break;
		}
		case 'U': {
			NodeAttributes = Probabilities.setUniform(numNodes, attributes);
			break;
		}
		case 'E': {

			NodeAttributes = Probabilities.setExponential(numNodes, attributes, lambda);
			// for (Node n : g.getNodeSet()) {
			// System.out.println(n.getAttributes());
			// }
			break;
		}
		}
		
		pw.println(numAttr);
		pw.flush();
		//print degli attributi
		for(int i=0; i<numNodes; i++) {
			pw.print(i+" ");
			for (String value : NodeAttributes[i]) {
				pw.print(value+" ");
				pw.flush();
			}
			pw.println();
			pw.flush();
		}

		pw.close();
	}

}
