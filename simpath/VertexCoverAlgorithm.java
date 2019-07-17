package simpath;

import java.util.ArrayList;
import java.util.LinkedList;

/*
 * 
 * 
 * Vertex Cover - maximum degree heuristic: considera il grafo non orientato 

1) Initialize the result as {}
2) Consider a set of all edges in given graph.  Let the set be E.
3) Do following while E is not empty
...a) picks a node of maximum degree and adds it into the vertex cover C whenever at least one of its incident edges is not yet covered.
...b) Remove all edges from E which are incident on the node.
4) Return result 

 */
public class VertexCoverAlgorithm {
	private Graph graph;
	private ArrayList<Node> nodeSet;

	public VertexCoverAlgorithm(Graph g) {
		graph = g;
		nodeSet = g.getNodeSet();
	}

	public LinkedList<Node> buildVertexCover() {

		ArrayList<LinkedList<Node>> adj = graph.getUnorientedAdjList();

		LinkedList<Node> cover = new LinkedList<>();
		
		// andiamo via via ad eliminare dalla lista di adiacenze gli archi
		while (!allCovered(adj)) {
			// prendiamo il nodo di grado massimo
			// questo verifica anche che ci sia almeno un arco non ancora coperto che incide
			// sul nodo
			Node maxD = getNodeMaxDegree(adj);
			// aggiungiamo il nodo al vertex cover
			cover.add(maxD);
			// rimuoviamo i nodi incidenti sul nodo appena aggiunto dalla lista di adiacenze
			removeCoveredEdges(adj, maxD);
		}
		return cover;
	}

	private boolean allCovered(ArrayList<LinkedList<Node>> adj) {
		for (int i = 0; i < adj.size(); i++) {
			if (!adj.get(i).isEmpty())
				return false;
		}
		return true;
	}

	private Node getNodeMaxDegree(ArrayList<LinkedList<Node>> adj) {
		int max = 0;
		Node result = null;
		for (int i = 0; i < adj.size(); i++) {
			if (adj.get(i).size() > max) {
				max = adj.get(i).size();
				result = nodeSet.get(i);
			}
		}
		return result;
	}

	private void removeCoveredEdges(ArrayList<LinkedList<Node>> adj, Node maxD) {
		for (Node i : adj.get(maxD.getId())) { // per ogni nodo vicino del nodo di massimo grado
			// eliminiamo l'arco che lo collega a maxD (i,maxD)
			adj.get(i.getId()).remove(maxD);
		}
		// ora eliminiamo tutti gli archi che escono da maxD (maxD,i)
		adj.get(maxD.getId()).clear();
	}

}