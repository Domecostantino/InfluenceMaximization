package simpath;

import java.util.HashMap;
import java.util.LinkedList;

public class Node implements Comparable<Node> {
	private final int id; // univoco e progressivo
	private double spread;
	private HashMap<Node, Double> outerSpread = new HashMap<>();
	private double marginalSpread = 0;

	private LinkedList<String> attributes;
	private double diversity; // inizialmente massima (cioe' pari al numero di attributi, un contributo di 1
								// per ogni attributo (vedere metodo setAttributes))
	private double score; // aggregazione di spread e diversity
	private final double alpha; // peso dello spread rispetto alla diversita
	private int graphSize = 0;

	public HashMap<Node, Double> getOuterSpread() {
		return outerSpread;
	}

	public Node(int id, double alpha, int graphSize) {
		this.id = id;
		this.alpha = alpha;
		this.graphSize = graphSize;
	}

	public Node(Node n) {
		this.id = n.id;
		this.spread = n.spread;
		this.attributes = n.attributes;
		this.diversity = n.diversity;
		this.alpha = n.alpha;
		this.graphSize = n.graphSize;
	}

	public int getId() {
		return id;
	}

	public double getAlpha() {
		return alpha;
	}

	@Override
	public int hashCode() {
		return id;
	}

	public synchronized void addOuterSpread(Node n, double addingSpread) {
		if (!outerSpread.containsKey(n))
			outerSpread.put(n, Double.valueOf(addingSpread));
		else
			outerSpread.put(n, addingSpread + outerSpread.get(n));
	}

	public void clearOuterSpread() {
		if (outerSpread != null)
			outerSpread.clear();
	}

	public double getMarginalSpread() {
		return marginalSpread;
	}

	public void clearMarginalSpread() {
		marginalSpread = 0;
	}

	public synchronized void addMarginalSpread(double addingSpread) {
		marginalSpread += addingSpread;
	}

	public void setSpread(double spread) {
		this.spread = spread;
		computeScore();
	}

	public double getSpread() {
		return spread;
	}

	public double getDiversity() {
		return diversity;
	}

	public void setDiversity(double diversity) {
		this.diversity = diversity;
		computeScore();
	}

	public int getGraphSize() {
		return graphSize;
	}

	public String getAttribute(int index) {
		return attributes.get(index);
	}

	public void setAttributes(LinkedList<String> attr) {
		this.attributes = attr;
		this.diversity = attributes.size(); // inizialmente massima
	}

	public LinkedList<String> getAttributes() {
		return attributes;
	}

	private void computeScore() {
		//per seed set più grandi il valore della diversità ha bisogno di un fattore (graphSize/100) per essere rilevante
		score = alpha * spread + (1 - alpha) * diversity;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		Node e = (Node) obj;
		return e.getId() == this.id;
	}

	public String toString() {
		return "" + this.id;
	}

	@Override
	public int compareTo(Node n) {
		if (n == this || this.equals(n))
			return 0;
		if (this.score < n.score)
			return 1;
		else if (this.score > n.score)
			return -1;
		return 0;
	}

}
