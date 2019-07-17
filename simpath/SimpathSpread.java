package simpath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveTask;

public class SimpathSpread {

	// grafo memorizzato come lista di adiacenze basata sull'id del nodo.
	// per accedere all'arco (i,j) si accede alla posizione i dell'array
	// e poi si cerca la chiave j nell'hashmap, il valore corrispondente è l'arco
	private ArrayList<HashMap<Node, Edge>> graph;
	private ArrayList<Node> nodeSet;

	public SimpathSpread(Graph g) {
		graph = g.getGraph();
		nodeSet = g.getNodeSet();
	}

	public double simpathSpread(LinkedList<Node> seedSet, double threshold, LinkedList<Node> U, boolean secondPhase, boolean multiThread) {
		if(multiThread) {
			return multiThread(seedSet, threshold, U, secondPhase);
		}
		return singleThread(seedSet, threshold, U, secondPhase);
	}

	private double singleThread(LinkedList<Node> seedSet, double threshold, LinkedList<Node> U, boolean secondPhase) {
		double spread = 0D;
		HashSet<Node> differenceSet = new HashSet<>(nodeSet);
		// V-S
		differenceSet.removeAll(seedSet);
		for (Node node : seedSet) {
			// V-S + u
			differenceSet.add(node);
			spread = spread + backtrack(node, node, threshold, differenceSet, U, secondPhase);

			// rimuovo u da V-S per la prossima iterazione
			differenceSet.remove(node);
		}
		return spread;
	}

	private double multiThread(LinkedList<Node> seedSet, double threshold, LinkedList<Node> U, boolean secondPhase) {
		double spread = 0D;
		Set<Node> differenceSet = new HashSet<>(nodeSet);
		// V-S
		differenceSet.removeAll(seedSet);
		Collection<RecursiveTask<Double>> tasks = new LinkedList<>();
		Iterator<Node> it = seedSet.iterator();
		while (it.hasNext()) {
			Node node = it.next();
			RecursiveTask<Double> task = new RecursiveTask<Double>() {
				private static final long serialVersionUID = 1L;
				@Override
				protected Double compute() {
					// V-S + u
					Set<Node> diff = new HashSet<>(differenceSet);
					diff.add(node);
					double spread1 = backtrack(node, node, threshold, diff, U, secondPhase);
					return spread1;
				}
			};
			tasks.add(task);
		}
		RecursiveTask.invokeAll(tasks);//aspetta che tutti i task siano completati
		for (RecursiveTask<Double> recursiveTask : tasks) {
			try {
				spread += recursiveTask.get().doubleValue();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		return spread;
	}

	public double backtrack(Node initial, Node node, double threshold, Set<Node> W, LinkedList<Node> U,
			boolean secondPhase) {

		// Q e' lo stack che contiene i nodi del simple path navigato
		Stack<Node> Q = new Stack<>();
		Q.push(node);
		double spread = 1.0;
		double pp = 1.0;
		// D [x] mantiene gli out-neighbor di
		// x che sono stati visti finora. Possiamo tenere traccia
		// dei percorsi esplorati da un nodo in modo efficiente.
		HashMap<Node, HashSet<Node>> D = new HashMap<>();
		while (!Q.isEmpty()) {
			// forward prende l'ultimo elemento di Q e aggiunge nodi in una logica
			// depth-first assicurandosi di non creare cicli e non attraversando segmenti di
			// path gia visitati
			RitornoForward returnForw = forward(initial, Q, D, spread, pp, threshold, W, U, secondPhase);
			spread = returnForw.spd;
			pp = returnForw.pp;
			node = Q.pop();
			D.remove(node);
			if (!Q.isEmpty()) {
				Node v = Q.lastElement();
				pp = pp / getWeight(v, node);
			}
			// se non si posso aggiungere piu nodi si fa backtracking rimuovendo l'ultimo
			// nodo dallo stack
		}

		return spread;
	}

	private RitornoForward forward(Node n, Stack<Node> Q, HashMap<Node, HashSet<Node>> D, double spread, double pp,
			double threshold, Set<Node> W, LinkedList<Node> U, boolean secondPhase) {
		Node x = Q.lastElement();
		if (!D.containsKey(x))
			D.put(x, new HashSet<Node>());
		// ricaviamo il vicinato di x (out-neighbors)
		Set<Node> out_neighbors = graph.get(x.getId()).keySet();
		Node y = checkCondition(out_neighbors, x, Q, D, W);
		while (y != null) {
			if ((pp * getWeight(x, y) < threshold)) {
				D.get(x).add(y);
			} else {
				Q.add(y);
				pp = pp * getWeight(x, y);
				spread = spread + pp;
				D.get(x).add(y);

				// calcolo dello outerSpread dei nodi di U
				for (Node v : U) {
					// aggiorniamo la struttura che tiene traccia dello spread dei nodi non
					// appartenti al cover e che permette di applicare il teorema 2 per il calcolo
					if (!Q.contains(v)) {
						if (secondPhase) {
							v.addMarginalSpread(pp);
						} else {
							double outSpread = pp * getWeight(v, n);
							if (isOutNeighbor(v, n) && outSpread > threshold)
								v.addOuterSpread(n, outSpread);
						}
					}
				}

				x = Q.lastElement();
				if (!D.containsKey(x))
					D.put(x, new HashSet<>());

			}
			out_neighbors = graph.get(x.getId()).keySet();
			y = checkCondition(out_neighbors, x, Q, D, W);
		}
		return new RitornoForward(spread, pp);
	}

	private boolean isOutNeighbor(Node v, Node n) {
		return graph.get(v.getId()).containsKey(n);
	}

	// ritorna un nodo y appartenente all'out-neighboroud di x che non sia gia stato
	// visitato nel path (non appartiene a Q) che non sia un vicino gia visitato di
	// x e che appartenga a W (al sottografo indotto)
	private Node checkCondition(Set<Node> out_neighbors, Node x, Stack<Node> Q, HashMap<Node, HashSet<Node>> D,
			Set<Node> W) {
		for (Node y : out_neighbors) {
			if ((!Q.contains(y)) && (!D.get(x).contains(y)) && W.contains(y)) {
				// System.out.println(Q+" "+D+" "+W+" "+y);
				return y;
			}
		}
		return null;
	}

	private double getWeight(Node x, Node y) {
		// System.out.println(x.getId()+" "+y.getId()+" "+graph.get(x.getId()).get(y));
		return graph.get(x.getId()).get(y).getWeight();
	}

	private class RitornoForward {
		double spd, pp;

		public RitornoForward(double spd, double pp) {
			this.spd = spd;
			this.pp = pp;
		}
	}

}
