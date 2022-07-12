package it.polito.tdp.nyc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.nyc.db.NYCDao;
import it.polito.tdp.nyc.model.Event.EventType;

public class Simulator {

	
	NYCDao dao = new NYCDao();
	Model m = new Model();
	
	//dati input
	private Graph<City, DefaultWeightedEdge> grafo;
	private List<City> cities;
	private int nOperatori;
	private City quartiere;
	
	//output
	private int durataTot;
	private List<Integer> hotspotRevisionati;
	
	//simulazione
	private int nDaRevisionare;
	private City currentCity;
	private List<City> daRevisionare;
	private int tecniciOccupati;
	
	//coda
	PriorityQueue<Event> queue;
	
	public Simulator(Graph<City, DefaultWeightedEdge> grafo, List<City> cities) {
		this.grafo = grafo;
		this.cities = cities;
	}
	
	public void init(City quartiere, int N) {
		//inizializzo gli input
		this.nOperatori = N;
		this.quartiere = quartiere;
		
		//inizializzo gli output
		this.hotspotRevisionati = new ArrayList<Integer>();
		
		for(int i = 0; i < N; i++) {
			hotspotRevisionati.add(0);
		}
		
		this.durataTot = 0;
		
		//inizializzo il mondo
		this.currentCity = quartiere;
		this.nDaRevisionare = quartiere.getNumHotspot();
		this.tecniciOccupati = 0;
		this.daRevisionare = new ArrayList<>(this.cities);
		this.daRevisionare.remove(quartiere);
		
		//creo la coda
		queue = new PriorityQueue<>();
		
		// caricamento iniziale della coda
		int i = 0;
		while(this.tecniciOccupati<this.nOperatori && this.nDaRevisionare>0) {
			// posso assegnare un tecnico ad un hotspot
			queue.add(new Event( 0, EventType.INIZIO, i )) ;
			this.tecniciOccupati++ ;
			this.nDaRevisionare--;
			i++;
		}
	}
	
	public void run() {
		
		while(!this.queue.isEmpty()) {
			Event e = this.queue.poll();
			this.durataTot = e.getTime();
			processEvent(e);
		}
		
	}
	private void processEvent(Event e) {
		int time = e.getTime();
		EventType type = e.getE();
		int tecnico = e.getTecnico();
		
		switch(type) {
		case INIZIO:
			this.hotspotRevisionati.set(tecnico, this.hotspotRevisionati.get(tecnico)+1);
			
			if(Math.random()<0.1) {
				queue.add(new Event(time+25, EventType.FINE, tecnico)) ;
			} else {
				queue.add(new Event(time+10, EventType.FINE, tecnico)) ;
			}
			break;
			
		case FINE:
			
			this.tecniciOccupati-- ;
			
			if(this.nDaRevisionare>0) {
				// mi sposto su altro h.s.
				int spostamento = (int)(Math.random()*11)+10 ; //casuale da 10 a 20 minuti
				this.tecniciOccupati++;
				this.nDaRevisionare--;
				queue.add(new Event(time+spostamento, EventType.INIZIO, tecnico));
			} else if(this.tecniciOccupati>0) {
				// non fai nulla
			} else if(this.daRevisionare.size()>0){
				// tutti cambiamo quartiere
				City destinazione = piuVicino(this.currentCity, this.daRevisionare);
				
				int spostamento = (int)(this.grafo.getEdgeWeight(this.grafo.getEdge(this.currentCity, destinazione)) / 50.0 *60.0);
				this.currentCity = destinazione ;
				this.daRevisionare.remove(destinazione);
				this.nDaRevisionare = this.currentCity.getNumHotspot();
				
				this.queue.add(new Event(time+spostamento, EventType.NUOVO_QUARTIERE, -1));
			} else {
				// fine simulazione :)
			}
			
			break;
			
		case NUOVO_QUARTIERE:
			
			int i = 0;
			while(this.tecniciOccupati<this.nOperatori && this.nDaRevisionare>0) {
				// posso assegnare un tecnico ad un hotspot
				queue.add(new Event( time, EventType.INIZIO, i )) ;
				this.tecniciOccupati++ ;
				this.nDaRevisionare--;
				i++;
			}

			break;
		}
		
	}

	private City piuVicino(City current, List<City> vicine) {
		double min = 100000.0 ;
		City destinazione = null ;
		for(City v: vicine) {
			double peso = this.grafo.getEdgeWeight(this.grafo.getEdge(current, v)); 
			if(peso<min) {
				min = peso;
				destinazione = v ;
			}
		}
		return destinazione ;
	}

	public int getDurataTot() {
		return durataTot;
	}

	public List<Integer> getHotspotRevisionati() {
		return hotspotRevisionati;
	}
}
