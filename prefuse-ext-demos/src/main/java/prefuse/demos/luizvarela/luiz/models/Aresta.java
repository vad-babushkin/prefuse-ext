package prefuse.demos.luizvarela.luiz.models;

public class Aresta {
	
	private final String id; 
	  private final Vertice source;
	  private final Vertice destination;
	  private final int weight; 
	  
	  public Aresta(String id, Vertice source, Vertice destination, int weight) {
	    this.id = id;
	    this.source = source;
	    this.destination = destination;
	    this.weight = weight;
	  }
	  
	  public String getId() {
	    return id;
	  }
	  public Vertice getDestination() {
	    return destination;
	  }

	  public Vertice getSource() {
	    return source;
	  }
	  public int getWeight() {
	    return weight;
	  }
	  
	  @Override
	  public String toString() {
	    return source + " " + destination;
	  }
}
