package it.polito.tdp.nyc.model;

public class Event implements Comparable<Event>{

	public enum EventType{
		INIZIO,
		FINE,
		NUOVO_QUARTIERE
	}
	
	int time;
	private EventType e;
	int tecnico;

	
	public Event(int time, EventType e, int tecnico) {
		super();
		this.time = time;
		this.e = e;
		this.tecnico = tecnico;
	}

	public int getTime() {
		return time;
	}

	public EventType getE() {
		return e;
	}

	public int getTecnico() {
		return tecnico;
	}

	@Override
	public int compareTo(Event o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}