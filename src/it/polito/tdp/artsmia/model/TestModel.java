package it.polito.tdp.artsmia.model;

public class TestModel {

	public static void main(String[] args) {

		Model m = new Model();
		
		System.out.println(m.getAllRoles());
		
		String role = "manufacturer";
		
		m.creaGrafo(role);
		
		//System.out.println(m.getConnessi());
		
		m.percorsoMassimo(4164);
		
	
		//m.adiacentiDi(5);
		//System.out.println("cerca sofi\n");
		//m.getCammino(4233);

	}

}
