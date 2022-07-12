package it.polito.tdp.nyc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.nyc.db.NYCDao;

public class Model {
	
	private NYCDao dao;
	private Graph<City, DefaultWeightedEdge> grafo;
	List<City> cities;
	
	// risultati simulazione
	private int durata;
	private List<Integer> revisionati;
	
	public Model() {
		dao = new NYCDao();
	}
	
	public String creaGrafo(String provider) {
		
		cities = dao.getCity(provider);
		
		//creo il grafo
		this.grafo = new SimpleWeightedGraph<City, DefaultWeightedEdge>(DefaultWeightedEdge.class);
	
		//aggiung i vertici
		Graphs.addAllVertices(this.grafo, cities);
		
		//aggiungo gli archi pesati
		for (City c: cities) {
			for(City c2: cities) {
				if(!c.getName().equals(c2.getName())) {
					double peso = LatLngTool.distance(c.getPosizione(), c2.getPosizione(), LengthUnit.KILOMETER);
					Graphs.addEdgeWithVertices(this.grafo, c, c2, peso);
				}
			}
		}
		
		return "Grafo creato!! \n#Vertici: " + getVertici().size() + "\n#Archi: " + this.grafo.edgeSet().size() + "\n"; 
	}
	
	
	public List<String> getProvider(){
		return dao.Provider();
	}
	
	public List<City> getVertici(){
		return cities;
	}
	
	public List<CityDistance> getDistanze (City scelto){
		
		List<CityDistance> distanze = new ArrayList<>();
		List<City> vicini = Graphs.neighborListOf(this.grafo, scelto);
		for(City c: vicini) {
			distanze.add(new CityDistance (c, this.grafo.getEdgeWeight(this.grafo.getEdge(scelto, c)), c.getName()));
		}
		
		Collections.sort(distanze , new Comparator<CityDistance>() {
			@Override
			public int compare(CityDistance o1, CityDistance o2) {
				return o1.getDistanza().compareTo(o2.getDistanza());
			}			
		});
		
		return distanze;
	}
	public void simula(City scelto, int N) {
		Simulator sim = new Simulator(this.grafo, this.cities);
		sim.init(scelto, N);
		sim.run();
		this.durata = sim.getDurataTot();
		this.revisionati = sim.getHotspotRevisionati();
	}

	public int getDurata() {
		return durata;
	}

	public List<Integer> getRevisionati() {
		return revisionati;
	}
}
