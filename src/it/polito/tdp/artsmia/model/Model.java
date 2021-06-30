package it.polito.tdp.artsmia.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {
	
	private ArtsmiaDAO dao;
	private Map<Integer, Artist> idMap;
	private Graph<Artist, DefaultWeightedEdge> grafo;
	
	private List<Artist> soluzione;
	
	public Model() {
		dao = new ArtsmiaDAO();
	}
	
	public List<String> getAllRoles() {
		return dao.listRoles();
	}
	
	public void creaGrafo(String role) {
		
		idMap = new HashMap<>();
		dao.getVertici(role, idMap);
		
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(grafo, idMap.values());
		
		for(Arco a : dao.getArchi(role, idMap))
			if(!grafo.containsEdge(a.getA1(), a.getA2()) && !grafo.containsEdge(a.getA2(), a.getA1()))
				Graphs.addEdgeWithVertices(grafo, a.getA1(), a.getA2(), a.getPeso());
		
		System.out.println("Vertici: " + grafo.vertexSet().size() + "\nArchi: " + grafo.edgeSet().size());
	}
	
	public List<Arco> getConnessi() {
		List<Arco> res = new ArrayList<Arco>();
		
		for(DefaultWeightedEdge e : grafo.edgeSet()) 
			res.add(new Arco(grafo.getEdgeSource(e), grafo.getEdgeTarget(e), grafo.getEdgeWeight(e)));
		
		res.sort((Arco a1, Arco a2) -> -(Double.compare(a1.getPeso(), a2.getPeso())));
		return res;
	}
	
	public void percorsoMassimo(int id) {
		
		if(idMap.get(id) == null) {
			errore();
			return;
		}
		
		soluzione = new ArrayList<>();
		List<Artist> parziale = new ArrayList<>();
		parziale.add(idMap.get(id));
		
		cerca(parziale);
		
		System.out.println(soluzione);
		System.out.println(soluzione.size());
	}
	
	private void cerca(List<Artist> parziale) {
		if(parziale.size() > soluzione.size()) {
			soluzione = new ArrayList<>(parziale);
			//System.out.println(soluzione.size());
			
		}
		
		List<Artist> adiacenti = Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1));
		if(adiacenti.isEmpty())
			return;
		
		for(Artist a : adiacenti) {
			if(!parziale.contains(a) && stessoPeso(parziale, a)) {
				parziale.add(a);
				cerca(parziale);
				parziale.remove(parziale.size()-1);
			} 
		}
		
	}

	private boolean stessoPeso(List<Artist> parziale, Artist a) {
		if(parziale.size() < 2)
			return true;
		if(grafo.getEdgeWeight(grafo.getEdge(a, parziale.get(parziale.size()-1))) == grafo.getEdgeWeight(grafo.getEdge(parziale.get(parziale.size()-1), parziale.get(parziale.size()-2))))
			return true;
			
		return false;
	}

	private String errore() {
		return "Artista non presente!";
	}
	
	
	
	private List<Artist> percorsoOttimo;
	private double costoOttimo;
	
	public List<Artist> getCammino(int idArtista){
		this.percorsoOttimo = new ArrayList<>();
		this.costoOttimo = 0;
		Artist partenza = idMap.get(idArtista);
		//System.out.println(idMap);
		if(partenza != null) {
			List<Artist> parziale = new ArrayList<>();
			parziale.add(partenza);
			cerca2(parziale,0, partenza);
		}
		
		System.out.println(percorsoOttimo.size());
		return percorsoOttimo;
	}

	private void cerca2(List<Artist> parziale, double L, Artist partenza) {

		//System.out.println(Graphs.neighborListOf(grafo, partenza));
		if(parziale.size() == 1) {
			for(Artist a : Graphs.neighborListOf(grafo, partenza)) {
				DefaultWeightedEdge e = grafo.getEdge(partenza, a);
				parziale.add(a);
				cerca2(parziale, grafo.getEdgeWeight(e),a);
				parziale.remove(a);
			}
		}else {
			for(Artist a : Graphs.neighborListOf(grafo, partenza)) {
				DefaultWeightedEdge e = grafo.getEdge(partenza, a);
				if(!parziale.contains(partenza) && grafo.getEdgeWeight(e) == L) {
					parziale.add(a);
					cerca2(parziale, L, a);
					parziale.remove(a);
				}
			}
		}
		if(parziale.size()>percorsoOttimo.size()) {
			percorsoOttimo = new ArrayList<>(parziale);
			costoOttimo = L;
		}
		
	} 
	
	public void adiacentiDi(int id) {
		System.out.println(Graphs.neighborListOf(grafo, idMap.get(id)).size());
	}
	
}
