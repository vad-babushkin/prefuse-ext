package prefuse.demos.projektlp;

public class NetworkData 
{
	double network_load;
	double network_blocking;
	
	public NetworkData()
	{
		network_load = 0.0;
		network_blocking = 0.0;
	}
	
	public void setData(double nl, double nb)
	{
		this.network_load = nl;
		this.network_blocking = nb;
	}

	public double getNetwork_load() {
		return network_load;
	}

	public double getNetwork_blocking() {
		return network_blocking;
	}
	
	
}
