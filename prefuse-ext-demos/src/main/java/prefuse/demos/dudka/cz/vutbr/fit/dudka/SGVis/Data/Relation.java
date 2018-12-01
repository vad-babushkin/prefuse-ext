package prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Data;

import java.net.URL;

/**
 * Simple structured type containing data for one relation.
 * This is an value type. hashCode and equals methods are overriden.
 */
public class Relation {
	public URL from;			///< Relation's source
	public URL to;				///< Relation's target
	public String edgeType;		///< Type of relation represented as string
	/**
	 * Simple constructor.
	 * @param from Relation's source
	 * @param to Relation's target
	 * @param edgeType Type of relation represented as string
	 */
	public Relation(URL from, URL to, String edgeType) {
		this.from = from;
		this.to = to;
		this.edgeType = edgeType;
	}
	/**
	 * Overriden method for debug purposes only.
	 */
	public String toString() {
		return
			edgeType + ": " +
			"\"" + from.toString() +"\" -> " +
			"\"" + to.toString() +"\"";
	}
	public boolean equals(Object obj) {
		Relation rel = (Relation)obj;
		return 
			this.from.equals(rel.from) &&
			this.to.equals(rel.to) &&
			this.edgeType.equals(rel.edgeType);
	}
	public int hashCode() {
		String all =
			from.toString() +
			to.toString() +
			edgeType;
		return all.hashCode();
	}
}
