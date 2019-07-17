package simpath;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.StringTokenizer;

public class Graph {

	// grafo memorizzato come lista di adiacenze basata sull'id del nodo.
	// per accedere all'arco (i,j) si accede alla posizione i dell'array
	// e poi si cerca la chiave j nell'hashmap, il valore corrispondente è l'arco
	private ArrayList<HashMap<Node, Edge>> graph = new ArrayList<>();
	private ArrayList<Node> nodeSet = new ArrayList<>();
	private ArrayList<LinkedList<Node>> adj = new ArrayList<>(); // lista di adiacenze non orientata
	private double alpha;
	
	public Graph(double alpha) {
		this.alpha = alpha;
	}

	public ArrayList<LinkedList<Node>> getUnorientedAdjList() {
		return adj;
	}

	public ArrayList<HashMap<Node, Edge>> getGraph() {
		return graph;
	}
	

	public ArrayList<Node> getNodeSet() {
		return nodeSet;
	}

	public void buildGraph(String nomeFile) throws IOException {
		int header = 0;

		BufferedReader br = new BufferedReader(new FileReader(nomeFile));
		String line = br.readLine();
		StringTokenizer st = null;

		header = Integer.parseInt(line);
		for (int i = 0; i < header; i++) {
			graph.add(i, new HashMap<>());
			adj.add(new LinkedList<>());
			//supponiamo che siano presenti tutti i nodi da 0 a header-1
			nodeSet.add(new Node(i,alpha,header));
		}

		line = br.readLine();

		while (line != null) {
			st = new StringTokenizer(line);
			int i = Integer.parseInt(st.nextToken());
			int j = Integer.parseInt(st.nextToken());
			double w = Double.parseDouble(st.nextToken());
			Node nJ = new Node(j,alpha,header);
			Edge e = new Edge(w);
			if (i != j) {
				// supponiamo che non ci siano archi duplicati
				graph.get(i).put(nJ, e);
				

				// aggiungiamo anche alla lista di adiacenza
				Node nIA = new Node(i,alpha,header);
				Node nJA = new Node(j,alpha,header);
				if(!adj.get(i).contains(nJA))
					adj.get(i).add(new Node(j,alpha,header));
				if(!adj.get(j).contains(nIA))
					adj.get(j).add(new Node(i,alpha,header));
			}
			line = br.readLine();
		}
		br.close();
		
//		BufferedReader br2 = new BufferedReader(new FileReader(nomeFileNodes));
//		String l = br2.readLine();
//		l = br2.readLine();
//		while(l!=null) {
//			
//			st = new StringTokenizer(l);
//			
//			int n = Integer.parseInt(st.nextToken());
//			String attr =  st.nextToken();
//			LinkedList<String> a = new LinkedList<>();
//			a.add(attr);
//			nodeSet.get(n).setAttributes(a);
//			
//			l = br2.readLine();
//		}
//		
//		br2.close();
	}

	public void printGraph() {
		for (int i = 0; i < graph.size(); i++) {
			System.out.print(i + ":  ");
			for (Entry<Node, Edge> n : graph.get(i).entrySet()) {
				System.out.print("[<" + i + " , " + n.getKey().getId() + "> , " + n.getValue().getWeight() + "] \t");
			}
			System.out.println();
		}
		

	}
	
	public void printGraphAttr() {
		for (Node n : nodeSet) {
			System.out.println(n.getId()+" "+n.getAttributes());
		}
	}

	

}
