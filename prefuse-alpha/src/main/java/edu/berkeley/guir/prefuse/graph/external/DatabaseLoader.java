//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse.graph.external;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.graph.Edge;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class DatabaseLoader extends GraphLoader {
	private final String[] m_columns;
	protected String m_neighborQuery;
	protected String m_childrenQuery;
	protected String m_parentQuery;
	private Connection m_db;
	private PreparedStatement m_ns;
	private PreparedStatement m_cs;
	private PreparedStatement m_ps;

	public DatabaseLoader(ItemRegistry var1, String[] var2) {
		super(var1, var2[0]);
		this.m_columns = var2;
	}

	public String[] getColumns() {
		return this.m_columns;
	}

	public void connect(String var1, String var2, String var3, String var4) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class.forName(var1).newInstance();
		this.m_db = DriverManager.getConnection(var2, var3, var4);
		if (this.m_neighborQuery != null) {
			this.m_ns = this.prepare(this.m_neighborQuery);
		}

		if (this.m_childrenQuery != null) {
			this.m_cs = this.prepare(this.m_childrenQuery);
		}

		if (this.m_parentQuery != null) {
			this.m_ps = this.prepare(this.m_parentQuery);
		}

	}

	public Connection getConnection() {
		return this.m_db;
	}

	private PreparedStatement prepare(String var1) throws SQLException {
		if (var1 == null) {
			throw new IllegalArgumentException("Input query must be non-null");
		} else if (this.m_db == null) {
			throw new IllegalStateException("Connection to database not yet established! Make sure connect() is called first.");
		} else {
			return this.m_db.prepareStatement(var1);
		}
	}

	public void setNeighborQuery(String var1) throws SQLException {
		if (this.m_db != null) {
			this.m_ns = this.prepare(var1);
		}

		this.m_neighborQuery = var1;
	}

	public String getNeighborQuery() {
		return this.m_neighborQuery;
	}

	public void setChildrenQuery(String var1) throws SQLException {
		if (this.m_db != null) {
			this.m_cs = this.prepare(var1);
		}

		this.m_childrenQuery = var1;
	}

	public String getChildrenQuery() {
		return this.m_childrenQuery;
	}

	public void setParentQuery(String var1) throws SQLException {
		if (this.m_db != null) {
			this.m_ps = this.prepare(var1);
		}

		this.m_parentQuery = var1;
	}

	public String getParentQuery() {
		return this.m_parentQuery;
	}

	protected abstract void prepareNeighborQuery(PreparedStatement var1, ExternalNode var2);

	protected abstract void prepareChildrenQuery(PreparedStatement var1, ExternalTreeNode var2);

	protected abstract void prepareParentQuery(PreparedStatement var1, ExternalTreeNode var2);

	protected void getNeighbors(ExternalNode var1) {
		this.prepareNeighborQuery(this.m_ns, var1);
		this.loadNodes(0, this.m_ns, var1);
	}

	protected void getChildren(ExternalTreeNode var1) {
		this.prepareChildrenQuery(this.m_cs, var1);
		this.loadNodes(1, this.m_cs, var1);
	}

	protected void getParent(ExternalTreeNode var1) {
		this.prepareParentQuery(this.m_ps, var1);
		this.loadNodes(2, this.m_ps, var1);
	}

	private void loadNodes(int var1, PreparedStatement var2, ExternalEntity var3) {
		try {
			ResultSet var4 = var2.executeQuery();

			while(var4.next()) {
				this.loadNode(var1, var4, var3);
			}
		} catch (SQLException var5) {
			var5.printStackTrace();
		}

	}

	public ExternalEntity loadNode(int var1, ResultSet var2, ExternalEntity var3) throws SQLException {
		Object var4 = var1 == 0 ? new ExternalNode() : new ExternalTreeNode();

		for(int var5 = 0; var5 < this.m_columns.length; ++var5) {
			String var6 = var2.getString(this.m_columns[var5]);
			if (var6 != null) {
				var6 = var6.replaceAll("\r", "");
			}

			((ExternalEntity)var4).setAttribute(this.m_columns[var5], var6);
		}

		super.foundNode(var1, var3, (ExternalEntity)var4, (Edge)null);
		return (ExternalEntity)var4;
	}
}
