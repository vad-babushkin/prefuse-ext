//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefusex.force;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class NBodyForce extends AbstractForce {
	private static String[] pnames = new String[]{"GravitationalConstant", "MinimumDistance", "BarnesHutTheta"};
	public static final float DEFAULT_GRAV_CONSTANT = -0.4F;
	public static final float DEFAULT_MIN_DISTANCE = -1.0F;
	public static final float DEFAULT_THETA = 0.9F;
	public static final int GRAVITATIONAL_CONST = 0;
	public static final int MIN_DISTANCE = 1;
	public static final int BARNES_HUT_THETA = 2;
	private float xMin;
	private float xMax;
	private float yMin;
	private float yMax;
	private NBodyForce.QuadTreeNodeFactory factory;
	private NBodyForce.QuadTreeNode root;

	public NBodyForce() {
		this(-0.4F, -1.0F, 0.9F);
	}

	public NBodyForce(float var1, float var2, float var3) {
		this.factory = new NBodyForce.QuadTreeNodeFactory();
		this.params = new float[]{var1, var2, var3};
		this.root = this.factory.getQuadTreeNode();
	}

	public boolean isItemForce() {
		return true;
	}

	protected String[] getParameterNames() {
		return pnames;
	}

	private void setBounds(int var1, int var2, int var3, int var4) {
		this.xMin = (float)var1;
		this.yMin = (float)var2;
		this.xMax = (float)var3;
		this.yMax = (float)var4;
	}

	public void clear() {
		this.clearHelper(this.root);
		this.root = this.factory.getQuadTreeNode();
	}

	private void clearHelper(NBodyForce.QuadTreeNode var1) {
		for(int var2 = 0; var2 < var1.children.length; ++var2) {
			if (var1.children[var2] != null) {
				this.clearHelper(var1.children[var2]);
			}
		}

		this.factory.reclaim(var1);
	}

	public void init(ForceSimulator var1) {
		this.clear();
		int var2 = 2147483647;
		int var3 = 2147483647;
		int var4 = -2147483648;
		int var5 = -2147483648;
		Iterator var6 = var1.getItems();

		int var8;
		while(var6.hasNext()) {
			ForceItem var7 = (ForceItem)var6.next();
			var8 = Math.round(var7.location[0]);
			int var9 = Math.round(var7.location[1]);
			if (var8 < var2) {
				var2 = var8;
			}

			if (var9 < var3) {
				var3 = var9;
			}

			if (var8 > var4) {
				var4 = var8;
			}

			if (var9 > var5) {
				var5 = var9;
			}
		}

		int var10 = var4 - var2;
		var8 = var5 - var3;
		if (var10 > var8) {
			var5 = var3 + var10;
		} else {
			var4 = var2 + var8;
		}

		this.setBounds(var2, var3, var4, var5);
		var6 = var1.getItems();

		while(var6.hasNext()) {
			ForceItem var11 = (ForceItem)var6.next();
			this.insert(var11);
		}

		this.calcMass(this.root);
	}

	public void insert(ForceItem var1) {
		this.insert(var1, this.root, this.xMin, this.yMin, this.xMax, this.yMax);
	}

	private void insert(ForceItem var1, NBodyForce.QuadTreeNode var2, float var3, float var4, float var5, float var6) {
		if (var2.hasChildren) {
			this.insertHelper(var1, var2, var3, var4, var5, var6);
		} else if (var2.value != null) {
			if (isSameLocation(var2.value, var1)) {
				this.insertHelper(var1, var2, var3, var4, var5, var6);
			} else {
				ForceItem var7 = var2.value;
				var2.value = null;
				this.insertHelper(var7, var2, var3, var4, var5, var6);
				this.insertHelper(var1, var2, var3, var4, var5, var6);
			}
		} else {
			var2.value = var1;
		}

	}

	private static boolean isSameLocation(ForceItem var0, ForceItem var1) {
		float var2 = Math.abs(var0.location[0] - var1.location[0]);
		float var3 = Math.abs(var0.location[1] - var1.location[1]);
		return (double)var2 < 0.01D && (double)var3 < 0.01D;
	}

	private void insertHelper(ForceItem var1, NBodyForce.QuadTreeNode var2, float var3, float var4, float var5, float var6) {
		float var7 = var1.location[0];
		float var8 = var1.location[1];
		float var9 = var3 + (var5 - var3) / 2.0F;
		float var10 = var4 + (var6 - var4) / 2.0F;
		int var11 = (var7 >= var9 ? 1 : 0) + (var8 >= var10 ? 2 : 0);
		if (var2.children[var11] == null) {
			var2.children[var11] = this.factory.getQuadTreeNode();
			var2.hasChildren = true;
		}

		if (var11 != 1 && var11 != 3) {
			var5 = var9;
		} else {
			var3 = var9;
		}

		if (var11 > 1) {
			var4 = var10;
		} else {
			var6 = var10;
		}

		this.insert(var1, var2.children[var11], var3, var4, var5, var6);
	}

	private void calcMass(NBodyForce.QuadTreeNode var1) {
		float var2 = 0.0F;
		float var3 = 0.0F;
		var1.mass = 0.0F;
		if (var1.hasChildren) {
			for(int var4 = 0; var4 < var1.children.length; ++var4) {
				if (var1.children[var4] != null) {
					this.calcMass(var1.children[var4]);
					var1.mass += var1.children[var4].mass;
					var2 += var1.children[var4].mass * var1.children[var4].com[0];
					var3 += var1.children[var4].mass * var1.children[var4].com[1];
				}
			}
		}

		if (var1.value != null) {
			var1.mass += var1.value.mass;
			var2 += var1.value.mass * var1.value.location[0];
			var3 += var1.value.mass * var1.value.location[1];
		}

		var1.com[0] = var2 / var1.mass;
		var1.com[1] = var3 / var1.mass;
	}

	public void getForce(ForceItem var1) {
		this.forceHelper(var1, this.root, this.xMin, this.yMin, this.xMax, this.yMax);
	}

	private void forceHelper(ForceItem var1, NBodyForce.QuadTreeNode var2, float var3, float var4, float var5, float var6) {
		float var7 = var2.com[0] - var1.location[0];
		float var8 = var2.com[1] - var1.location[1];
		float var9 = (float)Math.sqrt((double)(var7 * var7 + var8 * var8));
		boolean var10 = false;
		if ((double)var9 == 0.0D) {
			var7 = ((float)Math.random() - 0.5F) / 50.0F;
			var8 = ((float)Math.random() - 0.5F) / 50.0F;
			var9 = (float)Math.sqrt((double)(var7 * var7 + var8 * var8));
			var10 = true;
		}

		boolean var11 = this.params[1] > 0.0F && var9 > this.params[1];
		float var12;
		if ((var2.hasChildren || var2.value == var1) && (var10 || (var5 - var3) / var9 >= this.params[2])) {
			if (var2.hasChildren) {
				var12 = var3 + (var5 - var3) / 2.0F;
				float var13 = var4 + (var6 - var4) / 2.0F;

				for(int var14 = 0; var14 < var2.children.length; ++var14) {
					if (var2.children[var14] != null) {
						this.forceHelper(var1, var2.children[var14], var14 != 1 && var14 != 3 ? var3 : var12, var14 > 1 ? var13 : var4, var14 != 1 && var14 != 3 ? var12 : var5, var14 > 1 ? var6 : var13);
					}
				}

				if (var11) {
					return;
				}

				if (var2.value != null && var2.value != var1) {
					float var15 = this.params[0] * var1.mass * var2.value.mass / (var9 * var9 * var9);
					var1.force[0] += var15 * var7;
					var1.force[1] += var15 * var8;
				}
			}
		} else {
			if (var11) {
				return;
			}

			var12 = this.params[0] * var1.mass * var2.mass / (var9 * var9 * var9);
			var1.force[0] += var12 * var7;
			var1.force[1] += var12 * var8;
		}

	}

	public final class QuadTreeNodeFactory {
		private int maxNodes = 10000;
		private ArrayList nodes = new ArrayList();

		public QuadTreeNodeFactory() {
		}

		public NBodyForce.QuadTreeNode getQuadTreeNode() {
			return this.nodes.size() > 0 ? (NBodyForce.QuadTreeNode)this.nodes.remove(this.nodes.size() - 1) : NBodyForce.this.new QuadTreeNode();
		}

		public void reclaim(NBodyForce.QuadTreeNode var1) {
			var1.mass = 0.0F;
			var1.com[0] = 0.0F;
			var1.com[1] = 0.0F;
			var1.value = null;
			var1.hasChildren = false;
			Arrays.fill(var1.children, (Object)null);
			if (this.nodes.size() < this.maxNodes) {
				this.nodes.add(var1);
			}

		}
	}

	public final class QuadTreeNode {
		boolean hasChildren = false;
		float mass;
		float[] com = new float[]{0.0F, 0.0F};
		ForceItem value;
		NBodyForce.QuadTreeNode[] children = new NBodyForce.QuadTreeNode[4];

		public QuadTreeNode() {
		}
	}
}
