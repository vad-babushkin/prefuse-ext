package profusians.demos.treefuse;

import prefuse.data.Tree;
import prefuse.data.io.TreeMLReader;
import prefuse.data.io.TreeMLWriter;

public class FileManager {

    private TreeMLWriter writer = new TreeMLWriter();

    private TreeMLReader reader = new TreeMLReader();

    public FileManager() {

    }

    public Tree readTree(String fileName) {
	try {
	    return (Tree) reader.readGraph(fileName);
	} catch (Exception e) {
	    System.out.println("Couldn't read file " + fileName + ": " + e);
	    return null;
	}

    }

    public void writeTree(Tree t, String fileName) {
	try {
	    writer.writeGraph(t, fileName);
	} catch (Exception e) {
	    System.out.println("Couldn't write file " + fileName + ": " + e);
	}
    }

}
