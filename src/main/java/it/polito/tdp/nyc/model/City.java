package it.polito.tdp.nyc.model;

import java.util.Objects;

import com.javadocmd.simplelatlng.LatLng;

public class City {

	private String name;
	private LatLng posizione;
	private int numHotspot;
	
	public City(String name, LatLng posizione, int numHotspot) {
		super();
		this.name = name;
		this.posizione = posizione;
		this.numHotspot = numHotspot;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LatLng getPosizione() {
		return posizione;
	}
	public void setPosizione(LatLng posizione) {
		this.posizione = posizione;
	}
	public int getNumHotspot() {
		return numHotspot;
	}
	public void setNumHotspot(int numHotspot) {
		this.numHotspot = numHotspot;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, numHotspot, posizione);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		City other = (City) obj;
		return Objects.equals(name, other.name) && numHotspot == other.numHotspot
				&& Objects.equals(posizione, other.posizione);
	}

	@Override
	public String toString() {
		return  name + "\n";
	}
	
}
