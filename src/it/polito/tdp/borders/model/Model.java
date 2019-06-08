package it.polito.tdp.borders.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

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

}
