package it.polito.tdp.nyc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.nyc.model.Event.EventType;
import it.polito.tdp.nyc.model.Event;

public class Simulator {
	
	// Dati in ingresso
	private Graph<City, DefaultWeightedEdge> grafo;
	private List<City> cities;
	private City partenza;
	private int N ; // numero di tecnici
	
	// Dati in uscita
	private int durata ; // durata in minuti della simulazione
	private List<Integer> revisionati ; // revisionati.get(i) = numero di hotspot revisionati dal tecnico 'i' (i tra 0 e N-1)
	


	// Modello del mondo
	private List<City> daVisitare; // quartieri ancora da visitare (escluso currentCity)
	private City currentCity ; // quartiere in lavorazione
	private int hotSpotRimanenti ; // h.s. ancora da revisionare nel quartiere
	private int tecniciOccupati; // quanti tecnici sono impegnati => quando arriva a 0, cambio quartiere
	
	// Coda degli eventi
	private PriorityQueue<Event> queue ;
	
	public Simulator(
			Graph<City, DefaultWeightedEdge> grafo,
			List<City> cities) {
		this.grafo = grafo ;
		this.cities = cities ;
	}
	
	public void init (City partenza, int N)  {
		this.partenza = partenza ;
		this.N = N ;
		
		// inizializzo gli output
		this.durata = 0 ;
		this.revisionati = new ArrayList<Integer>();
		for(int i=0; i<N; i++)
			revisionati.add(0);
		
		// inizializzo il mondo
		this.currentCity = this.partenza;
		this.daVisitare = new ArrayList<>(this.cities);
		this.daVisitare.remove(this.currentCity);
		this.hotSpotRimanenti = this.currentCity.getnHotSpot();
		this.tecniciOccupati = 0;
		
		// crea la coda
		this.queue = new PriorityQueue<>();
		
		// caricamento iniziale della coda
		int i = 0;
		while(this.tecniciOccupati<this.N && this.hotSpotRimanenti>0) {
			// posso assegnare un tecnico ad un hotspot
			queue.add(new Event( 0, EventType.INIZIO_HS, i )) ;
			this.tecniciOccupati++ ;
			this.hotSpotRimanenti--;
			i++;
		}
		
	}
	
	public void run() {
		
		while(!this.queue.isEmpty()) {
			Event e = this.queue.poll();
			this.durata = e.getTime();
			processEvent(e);
		}
		
	}

	private void processEvent(Event e) {
		int time = e.getTime();
		EventType type = e.getType();
		int tecnico = e.getTecnico();
		
		switch(type) {
		case INIZIO_HS:
			this.revisionati.set(tecnico, this.revisionati.get(tecnico)+1);
			
			if(Math.random()<0.1) {
				queue.add(new Event(time+25, EventType.FINE_HS, tecnico)) ;
			} else {
				queue.add(new Event(time+10, EventType.FINE_HS, tecnico)) ;
			}
			break;
			
		case FINE_HS:
			
			this.tecniciOccupati-- ;
			
			if(this.hotSpotRimanenti>0) {
				// mi sposto su altro h.s.
				int spostamento = (int)(Math.random()*11)+10 ; //casuale da 10 a 20 minuti
				this.tecniciOccupati++;
				this.hotSpotRimanenti--;
				queue.add(new Event(time+spostamento, EventType.INIZIO_HS, tecnico));
			} else if(this.tecniciOccupati>0) {
				// non fai nulla
			} else if(this.daVisitare.size()>0){
				// tutti cambiamo quartiere
				City destinazione = piuVicino(this.currentCity, this.daVisitare);
				
				int spostamento = (int)(this.grafo.getEdgeWeight(this.grafo.getEdge(this.currentCity, destinazione)) / 50.0 *60.0);
				this.currentCity = destinazione ;
				this.daVisitare.remove(destinazione);
				this.hotSpotRimanenti = this.currentCity.getnHotSpot();
				
				this.queue.add(new Event(time+spostamento, EventType.NUOVO_QUARTIERE, -1));
			} else {
				// fine simulazione :)
			}
			
			break;
			
		case NUOVO_QUARTIERE:
			
			int i = 0;
			while(this.tecniciOccupati<this.N && this.hotSpotRimanenti>0) {
				// posso assegnare un tecnico ad un hotspot
				queue.add(new Event( time, EventType.INIZIO_HS, i )) ;
				this.tecniciOccupati++ ;
				this.hotSpotRimanenti--;
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

	public int getDurata() {
		return durata;
	}

	public List<Integer> getRevisionati() {
		return revisionati;
	}
}
