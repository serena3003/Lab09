package it.polito.tdp.borders.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.borders.db.BordersDAO;

public class Model {

	private BordersDAO dao;
	private Graph<Country, DefaultEdge> grafo;
	private Map<Integer, Country> idMap;
	
	public Model() {
		dao = new BordersDAO();
		grafo = new SimpleGraph<>(DefaultEdge.class);
	}

	public void creaGrafo(int anno) {
		
		//creo idMap per gli stati
		List<Country> countries = dao.loadAllCountries();
		this.idMap = new HashMap<>();
		for(Country c : countries) {
			idMap.put(c.getId(), c);
		}
		
		//aggiungo i vertici e archi
		List<Border> borders = dao.getCountryPairs(anno, idMap);
		for(Border b : borders) {
			grafo.addVertex(b.getState1());
			grafo.addVertex(b.getState2());
			grafo.addEdge(b.getState1(), b.getState2());
		}
		
		System.out.println("Numero vertici: " + grafo.vertexSet().size() + "\n Numero di archi: " + grafo.edgeSet().size());
	}

	public Map<Integer, Country> getIdMap() {
		return idMap;
	}

	public String calcolaVicini() {
		String res = "Lista di vicini dei vertici: \n";
		for(Country c : grafo.vertexSet()) {
			int deg = grafo.degreeOf(c);
			res = res + c.getAbb() + " - " + deg + " vicini \n";
			}
		return res;
		
	}
	
	public int getNumberOfConnectedComponents() {
		if (grafo == null) {
			throw new RuntimeException("Grafo non esistente");
		}

		ConnectivityInspector<Country, DefaultEdge> ci = new ConnectivityInspector<Country, DefaultEdge>(grafo);
		return ci.connectedSets().size();
	}

	public List<Country> getCountryList() {
		return dao.loadAllCountries();
	}

	public List<Country> cercaVicini(Country c) {
		
		if (!grafo.vertexSet().contains(c)) {
			throw new RuntimeException("Lo Stato selezionato non e' presente nel grafo");
		}
		
		List<Country> res = new ArrayList<Country>();
		
		//creo un iteratore per la visita in AMPIEZZA del grafo
		GraphIterator<Country, DefaultEdge> it = new BreadthFirstIterator<>(this.grafo, c);
		while(it.hasNext()) {
			res.add(it.next());
		}
		
		return res;
	}

	public List<Country> cercaViciniIter(Country source) {
		
		List<Country> res = new ArrayList<Country>();
		res.add(source);
		cerca(source, res);
		return res;
	}
	
	public void cerca(Country c, List<Country> res) {
		Set<DefaultEdge> edges = grafo.edgesOf(c);
		for(DefaultEdge e : edges) {
			if(res.contains(grafo.getEdgeSource(e)) && !res.contains(grafo.getEdgeTarget(e))) {
				Country t =grafo.getEdgeTarget(e);
				res.add(t);
				cerca(t,res);
			}
			else if(res.contains(grafo.getEdgeTarget(e)) && !res.contains(grafo.getEdgeSource(e))) {
				Country t =grafo.getEdgeSource(e);
				res.add(t);
				cerca(t,res);
			}

		}
	}

}
