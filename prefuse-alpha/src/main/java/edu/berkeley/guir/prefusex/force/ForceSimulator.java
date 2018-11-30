//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefusex.force;

import edu.berkeley.guir.prefusex.force.Spring.SpringFactory;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ForceSimulator {
	private Set items;
	private Set springs;
	private Force[] iforces;
	private Force[] sforces;
	private int iflen;
	private int sflen;
	private Integrator integrator;
	private float speedLimit;

	public ForceSimulator() {
		this(new RungeKuttaIntegrator());
	}

	public ForceSimulator(Integrator var1) {
		this.speedLimit = 1.0F;
		this.integrator = var1;
		this.iforces = new Force[5];
		this.sforces = new Force[5];
		this.iflen = 0;
		this.sflen = 0;
		this.items = new HashSet();
		this.springs = new HashSet();
	}

	public float getSpeedLimit() {
		return this.speedLimit;
	}

	public void setSpeedLimit(float var1) {
		this.speedLimit = var1;
	}

	public void clear() {
		this.items.clear();
		Iterator var1 = this.springs.iterator();
		SpringFactory var2 = Spring.getFactory();

		while(var1.hasNext()) {
			var2.reclaim((Spring)var1.next());
		}

		this.springs.clear();
	}

	public void addForce(Force var1) {
		Force[] var2;
		if (var1.isItemForce()) {
			if (this.iforces.length == this.iflen) {
				var2 = new Force[this.iflen + 10];
				System.arraycopy(this.iforces, 0, var2, 0, this.iforces.length);
				this.iforces = var2;
			}

			this.iforces[this.iflen++] = var1;
		}

		if (var1.isSpringForce()) {
			if (this.sforces.length == this.sflen) {
				var2 = new Force[this.sflen + 10];
				System.arraycopy(this.sforces, 0, var2, 0, this.sforces.length);
				this.sforces = var2;
			}

			this.sforces[this.sflen++] = var1;
		}

	}

	public Force[] getForces() {
		Force[] var1 = new Force[this.iflen + this.sflen];
		System.arraycopy(this.iforces, 0, var1, 0, this.iflen);
		System.arraycopy(this.sforces, 0, var1, this.iflen, this.sflen);
		return var1;
	}

	public void addItem(ForceItem var1) {
		this.items.add(var1);
	}

	public boolean removeItem(ForceItem var1) {
		return this.items.remove(var1);
	}

	public Iterator getItems() {
		return this.items.iterator();
	}

	public Spring addSpring(ForceItem var1, ForceItem var2) {
		return this.addSpring(var1, var2, -1.0F, -1.0F);
	}

	public Spring addSpring(ForceItem var1, ForceItem var2, float var3) {
		return this.addSpring(var1, var2, -1.0F, var3);
	}

	public Spring addSpring(ForceItem var1, ForceItem var2, float var3, float var4) {
		if (var1 != null && var2 != null) {
			Spring var5 = Spring.getFactory().getSpring(var1, var2, var3, var4);
			this.springs.add(var5);
			return var5;
		} else {
			throw new IllegalArgumentException("ForceItems must be non-null");
		}
	}

	public boolean removeSpring(Spring var1) {
		return this.springs.remove(var1);
	}

	public Iterator getSprings() {
		return this.springs.iterator();
	}

	public void runSimulator(long var1) {
		this.accumulate();
		this.integrator.integrate(this, var1);
	}

	public void accumulate() {
		int var1;
		for(var1 = 0; var1 < this.iflen; ++var1) {
			this.iforces[var1].init(this);
		}

		for(var1 = 0; var1 < this.sflen; ++var1) {
			this.sforces[var1].init(this);
		}

		Iterator var5 = this.items.iterator();

		while(var5.hasNext()) {
			ForceItem var2 = (ForceItem)var5.next();
			var2.force[0] = 0.0F;
			var2.force[1] = 0.0F;

			for(int var3 = 0; var3 < this.iflen; ++var3) {
				this.iforces[var3].getForce(var2);
			}
		}

		Iterator var6 = this.springs.iterator();

		while(var6.hasNext()) {
			Spring var7 = (Spring)var6.next();

			for(int var4 = 0; var4 < this.sflen; ++var4) {
				this.sforces[var4].getForce(var7);
			}
		}

	}
}
