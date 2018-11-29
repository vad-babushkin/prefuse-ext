/**
 *ZoneLocation.java
 *Han Dong
 *July 16, 2009
 *
 * Class that holds info for x and y coordinates of a node
 */
package prefuse.demos.projektlp;

public class NodeLocation
{
	private int x;
	private int y;
	
	public NodeLocation(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
