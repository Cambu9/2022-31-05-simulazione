package it.polito.tdp.nyc.model;

import java.util.Objects;

public class CityDistance {

	private City citta;
	private Double distanza;
	private String nome;
	
	public CityDistance(City citta, Double distanza, String nome) {
		super();
		this.citta = citta;
		this.distanza = distanza;
	}

	public Double getDistanza() {
		return distanza;
	}

	public void setDistanza(Double distanza) {
		this.distanza = distanza;
	}

	public City getCitta() {
		return citta;
	}

	public void setCitta(City citta) {
		this.citta = citta;
	}
	
	
	public String getNome() {
		return this.citta.getName();
	}
	
}
