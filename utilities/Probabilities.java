package utilities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import simpath.Node;

/*
 * Questa classe ha il compito di fornire dei metodi di utilità che permettano di settare, per un insieme di nodi,
 * i valori degli attributi presi in input, in accordo alla ditribuzione di probabilità desiderata. Il guess del
 * singolo valore si avvale della classe Random di Java.
 * 
 */
public class Probabilities {

	public static void setUniform(ArrayList<Node> nodeSet, LinkedList<String>[] attributes) {
		Random r = new Random();
		// per ogni nodo
		for (Node node : nodeSet) {

			// per ogni attributo
			LinkedList<String> values = new LinkedList<>();
			for (int i = 0; i < attributes.length; i++) {
				double guess = r.nextDouble();
				int numVals = attributes[i].size();

				// per ogni valore
				for (int j = 1; j <= numVals; j++) {
					if (guess <= (j / (double) numVals)) {
						values.add(attributes[i].get(j - 1));
						break;
					}
				}
			}
			node.setAttributes(values);
		}
	}

	public static void setExponential(ArrayList<Node> nodeSet, LinkedList<String>[] attributes, double lambda) {
		Random r = new Random();
		LinkedList<double[]> allIntervals = new LinkedList<>();

		// calcolo degli intevalli in cui può ricadere il guess
		for (int i = 0; i < attributes.length; i++) {
			double[] intervals = new double[attributes[i].size()];
			double norm = 1 - Math.exp(-lambda * attributes[i].size());

			int numVals = attributes[i].size();
			for (int j = 1; j <= numVals; j++) {
				double interval = 1 - Math.exp(-lambda * j);
				interval = interval / norm; // passo di normalizzazione
				intervals[j - 1] = interval;
			}
			allIntervals.add(intervals);
		}

		// per ogni nodo
		for (Node node : nodeSet) {

			// per ogni attributo
			LinkedList<String> values = new LinkedList<>();
			for (int i = 0; i < attributes.length; i++) {
				double guess = r.nextDouble();

				int numVals = attributes[i].size();
				// per ogni valore
				for (int j = 0; j < numVals; j++) {
					if (guess <= allIntervals.get(i)[j]) {
						values.add(attributes[i].get(j));
						break;
					}
				}
			}
			node.setAttributes(values);
		}

	}

	public static void setManual(ArrayList<Node> nodeSet, LinkedList<String>[] attributes,
			LinkedList<Double>[] manualIntervals) {
		Random r = new Random();
		// per ogni nodo
		for (Node node : nodeSet) {

			// per ogni attributo
			LinkedList<String> values = new LinkedList<>();
			for (int i = 0; i < attributes.length; i++) {
				double guess = r.nextDouble();
				int numVals = attributes[i].size();

				// per ogni valore
				double cumulative = manualIntervals[i].get(0);
				for (int j = 1; j <= numVals; j++) {
					if (guess <= cumulative) {
						values.add(attributes[i].get(j - 1));
						break;
					}
					cumulative += manualIntervals[i].get(j);
				}
			}
			node.setAttributes(values);
		}

	}

	@SuppressWarnings("unchecked")
	public static LinkedList<String>[] setManual(int numNodes, LinkedList<String>[] attributes,
			LinkedList<Double>[] manualIntervals) {
		Random r = new Random();

		LinkedList<String>[] ret = new LinkedList[numNodes];
		// per ogni nodo
		for (int k = 0; k < numNodes; k++) {
			// per ogni attributo
			LinkedList<String> values = new LinkedList<>();
			for (int i = 0; i < attributes.length; i++) {
				double guess = r.nextDouble();
				int numVals = attributes[i].size();

				// per ogni valore
				double cumulative = manualIntervals[i].get(0);
				for (int j = 1; j <= numVals; j++) {
					if (guess <= cumulative) {
						values.add(attributes[i].get(j - 1));
						break;
					}
					cumulative += manualIntervals[i].get(j);
				}
			}
			ret[k] = values;
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public static LinkedList<String>[] setUniform(int numNodes, LinkedList<String>[] attributes) {
		Random r = new Random();

		LinkedList<String>[] ret = new LinkedList[numNodes];
		// per ogni nodo
		for (int k = 0; k < numNodes; k++) {

			// per ogni attributo
			LinkedList<String> values = new LinkedList<>();
			for (int i = 0; i < attributes.length; i++) {
				double guess = r.nextDouble();
				int numVals = attributes[i].size();

				// per ogni valore
				for (int j = 1; j <= numVals; j++) {
					if (guess <= (j / (double) numVals)) {
						values.add(attributes[i].get(j - 1));
						break;
					}
				}

			}
			ret[k] = values;
		}
		return ret;

	}

	@SuppressWarnings("unchecked")
	public static LinkedList<String>[] setExponential(int numNodes, LinkedList<String>[] attributes, double lambda) {
		Random r = new Random();
		LinkedList<double[]> allIntervals = new LinkedList<>();

		// calcolo degli intevalli in cui può ricadere il guess
		for (int i = 0; i < attributes.length; i++) {
			double[] intervals = new double[attributes[i].size()];
			double norm = 1 - Math.exp(-lambda * attributes[i].size());

			int numVals = attributes[i].size();
			for (int j = 1; j <= numVals; j++) {
				double interval = 1 - Math.exp(-lambda * j);
				interval = interval / norm; // passo di normalizzazione
				intervals[j - 1] = interval;
			}
			allIntervals.add(intervals);
		}

		LinkedList<String>[] ret = new LinkedList[numNodes];
		// per ogni nodo
		for (int k = 0; k < numNodes; k++) {

			// per ogni attributo
			LinkedList<String> values = new LinkedList<>();
			for (int i = 0; i < attributes.length; i++) {
				double guess = r.nextDouble();

				int numVals = attributes[i].size();
				// per ogni valore
				for (int j = 0; j < numVals; j++) {
					if (guess <= allIntervals.get(i)[j]) {
						values.add(attributes[i].get(j));
						break;
					}
				}
			}
			ret[k] = values;
		}
		return ret;

	}

}
