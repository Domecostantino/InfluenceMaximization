package simpath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.PriorityQueue;

public class Simpath {
	private ArrayList<HashMap<Node, Edge>> graph;
	private ArrayList<Node> nodeSet;
	private SimpathSpread simSpread;
	private VertexCoverAlgorithm vc;
	private double threshold;
	private int l, k;
	private PriorityQueue<Node> queue;
	private int numAttributes;
	private double[] attributeWeight;
	private boolean multiThread;

	public Simpath(Graph g, double threshold, int l, int k, int numAttributes, double[] attributeWeight, boolean multiT) {
		this.graph = g.getGraph();
		this.simSpread = new SimpathSpread(g);
		vc = new VertexCoverAlgorithm(g);
		this.threshold = threshold;
		this.l = l;
		this.k = k;
		queue = new PriorityQueue<>();
		nodeSet = g.getNodeSet();
		this.numAttributes = numAttributes;
		this.attributeWeight = attributeWeight;
		this.multiThread = multiT;
	}

	public LinkedList<Node> simpath() {

		// calcoliamo vertex cover
		LinkedList<Node> C = vc.buildVertexCover();

		System.out.println("Vertex Cover:");
		System.out.println(C);
		System.out.println("Vertex Cover size: " + C.size() + "\n");

		// per ogni nodo del VC
		for (Node u : C) {
			// prendiamo gli in-neighbors di u che non fanno parte del VC
			LinkedList<Node> U = getInNeighborsNoVC(C, u);
			// calcoliamo sig(u) e sig[V-v](u) (con v ogni elemento appartente a U) con una
			// chiamata di simpath-spread
			LinkedList<Node> singleElem = new LinkedList<>();
			singleElem.add(u);
			double spread_SeedSet = simSpread.simpathSpread(singleElem, threshold, U, false,multiThread);
			u.setSpread(spread_SeedSet);

			// aggiungiamo u alla PQueue
			queue.add(u);

		}

		// per ogni nodo v non nel VC
		for (Node i : nodeSet) {
			if (!C.contains(i)) {
				// calcoliamo lo spread di i mediante il teorema 2 (somma dello spread degli
				// out-neighbors)
				double spread = 1;

				for (Entry<Node, Double> u : i.getOuterSpread().entrySet()) {
					spread = spread + u.getValue() + graph.get(i.getId()).get(u.getKey()).getWeight();
				}
				// aggiungiamo il nodo v in queue in base al valore dello spread
				i.setSpread(spread);
				queue.add(new Node(i));

				// pulizia
				i.getOuterSpread().clear();
			}
		}

		// serve solo per stampare la priority queue

		/*
		 * System.out.println(); LinkedList<Node> tmp = new LinkedList<>();
		 * while(!queue.isEmpty()) { Node n = queue.poll(); System.out.print(n.getId() +
		 * " "); tmp.add(n); } for (Node node : tmp) { queue.add(node); }
		 */

		// a questo punto in queue ci sono tutti i nodi ordinati in base allo spread
		SeedSet S = new SeedSet(numAttributes);
		double spd = 0;
		// creiamo una lista per tenere traccia dei nodi u gia considerati (in questa
		// iterazione)
		HashSet<Node> visited = new HashSet<>();

		// fino a quando la dimensione del set di influencers è minore di k
		while (S.size() < k) {

			LinkedList<Node> U = new LinkedList<>();

			// aggiungiamo i top-l nodi della PQueue a U
			for (int j = 0; j < l; j++) {
				Node n = queue.poll();
				U.add(n);
			}

			// riaggiungiamo i nodi a PQueue
			for (Node n : U) {
				n.clearMarginalSpread();
				queue.add(n);
			}

			// calcoliamo spread[V-x](S) per ogni nodo x di U
			simSpread.simpathSpread(S.getSet(), threshold, U, true, multiThread);

			// per ogni nodo x di U
			for (Node x : U) {
				// se x e' stato gia considerato vuol dire che x ha guadagno marginale massimo
				// rispetto a S
				if (visited.contains(x)) {
					// aggiungiamo x agli influencers e aggiorniamo lo spread
					S.addNode(x);
					
					
					spd = spd + x.getSpread();
					// azzeriamo lista per tenere traccia dei nodi u gia considerati (in questa
					// iterazione)
					visited.clear();
					// rimuoviamo x dalla PQueue
					queue.remove(x);
					break;
				}
				// aggiungiamo x ai visitati
				visited.add(x);

				// calcoliamo V-S
				HashSet<Node> V_Less_S = new HashSet<>(nodeSet);
				V_Less_S.removeAll(S.getSet());

				// richiamiamo il metodo backtrack per calcolare lo spread di x su V-S
				double spreadX_VLessS = simSpread.backtrack(x, x, threshold, V_Less_S, new LinkedList<>(), false);
				// U in questo caso e' una lista vuota

				// calcoliamo lo spread di S+{x} utilizzando gli outerSpread di x già calcolati
				double spreadSPlusX = x.getMarginalSpread() + S.size() + spreadX_VLessS;
				double marginalGain = spreadSPlusX - spd;
				x.setSpread(marginalGain);

				// calcolo della diversita marginale
				double margDiv = 0D;
				for (int i = 0; i < numAttributes; i++) {
					// calcola guadagno marginale relativo ad ogni attributo
					margDiv += (attributeWeight[i] * S.getMarginalDiversity(x.getAttribute(i), i));
					
				}
				x.setDiversity(margDiv);
				//System.out.println(x.getId()+" "+x.getDiversity());
				queue.remove(x);
				queue.add(x);
			}
		}
		System.out.println("spread totale: " + String.format(Locale.US, "%.5f", spd));
		System.out.println("diversity seed set: " + String.format(Locale.US, "%.5f", S.getDiversity()));
		return S.getSet();

	}

	private LinkedList<Node> getInNeighborsNoVC(LinkedList<Node> c, Node u) {
		LinkedList<Node> U = new LinkedList<>();
		for (int i = 0; i < graph.size(); i++) {
			if (graph.get(i).containsKey(u) && !c.contains(new Node(i, u.getAlpha(),u.getGraphSize()))) {
				U.add(nodeSet.get(i));
			}
		}
		return U;
	}

}
