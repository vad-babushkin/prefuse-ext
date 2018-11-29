package prefuse.demos.projektlp;

public class DataSets 
{
	private NetworkSet networkSet;
	private LinkSet linkSet;
	private NodeSet nodeSet;

	public DataSets(String file_loc)
	{
		linkSet = new LinkSet(file_loc);
		networkSet = new NetworkSet(file_loc);
		nodeSet = new NodeSet(file_loc);
	}

	public NetworkSet getNetworkSet() {
		return networkSet;
	}

	public LinkSet getLinkSet() {
		return linkSet;
	}

	public NodeSet getNodeSet() {
		return nodeSet;
	}
	
	
}
