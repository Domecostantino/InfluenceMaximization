package simpath;

import java.util.HashMap;
import java.util.LinkedList;

public class SeedSet {
	private LinkedList<Node> set = new LinkedList<>();
	// attributeValues ci permette di tenere conto di quali valore degli attributi
	// che si sta cercando di diversificare sono presenti e in che quantita.
	// Si usa una mappa per ogni attributo.
	private HashMap<String, Integer>[] attributeValues;
	private double diversity = 0; // inizialmente zero

	@SuppressWarnings("unchecked")
	public SeedSet(int numAttributes) {
		attributeValues = new HashMap[numAttributes];
		for (int i = 0; i < numAttributes; i++) {
			attributeValues[i] = new HashMap<String, Integer>();
		}
	}

	public void addNode(Node n) {
		set.add(n);
		for (int i = 0; i < attributeValues.length; i++) {
			if (!attributeValues[i].containsKey(n.getAttribute(i)))
				attributeValues[i].put(n.getAttribute(i), 1);
			else
				attributeValues[i].put(n.getAttribute(i), attributeValues[i].get(n.getAttribute(i)) + 1);
			updateDiversity(n.getAttribute(i), i);
		}

	}

	public double getDiversity() {
		return diversity;
	}

	public int size() {
		return set.size();
	}

	public LinkedList<Node> getSet() {
		return set;
	}

	// permette di aggiornare la diversita in accordo al contributo 1/a
	// con a numero di occorrenze di quel valore di attributo gia presenti
	private void updateDiversity(String attribute, int numAttribute) {
		int occurences = attributeValues[numAttribute].get(attribute);
		double contributo = (double) 1 / (occurences * occurences);
		// System.out.println(contributo);
		diversity += contributo;
	}

	public double getMarginalDiversity(String attribute, int numAttribute) {
		if (!attributeValues[numAttribute].containsKey(attribute))
			return 1;
		int occurences = attributeValues[numAttribute].get(attribute);
		double mg = (double) 1 / (occurences * occurences);
		return mg;
	}
}
