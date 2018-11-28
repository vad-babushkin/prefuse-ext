package prefuse.demos.luizvarela.luiz.models;


import java.util.List;

public class Grafo {

	 private final List<Vertice> vertexes;
	  private final List<Aresta> edges;

	  public Grafo(List<Vertice> vertexes, List<Aresta> edges) {
	    this.vertexes = vertexes;
	    this.edges = edges;
	  }

	  public List<Vertice> getVertexes() {
	    return vertexes;
	  }

	  public List<Aresta> getEdges() {
	    return edges;
	  }
	  
}
