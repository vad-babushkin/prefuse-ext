package prefuse.demos.luizvarela.grafo;

public class CreateXML {

	private String[] singer = { "A", "F", "I"};
	private String[] band = { "C", "D" };

	public CreateXML(String graphName) {

		createVertice();
		createEdge();
		createXMLFile(graphName);

	}

	private void createXMLFile(String fileName) {

		GenerateXML.closeXML();
		GenerateXML.saveXML(fileName);

	}

	private void createVertice() {

		for (int i = 0; i < singer.length; i++)
			GenerateXML.gVertice(i * 100 + 1, singer[i]);

		for (int i = 0; i < band.length; i++)
			GenerateXML.gVertice(i * 100 + 5, band[i]);
	}

	private void createEdge() {

		for (int i = 0; i < singer.length; i++) {
			for (int j = 0; j < band.length; j++) {
				GenerateXML.gEdge(i * 100 + 1, j * 100 + 5, i + j *100);

			}
		}
	}

	public static void main(String[] args) {
		new CreateXML("grafo.xml");
		
	}

}
