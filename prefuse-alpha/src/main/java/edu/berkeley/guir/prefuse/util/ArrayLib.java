//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Comparator;
import java.util.StringTokenizer;

public abstract class ArrayLib {
	public static final int MERGE_THRESHOLD = 1000;

	public ArrayLib() {
	}

	public static final int binarySearch(int[] var0, int var1) {
		int var2 = 0;
		int var3 = var0.length;

		int var4;
		for(var4 = var3 / 2; var2 < var3; var4 = var2 + (var3 - var2) / 2) {
			if (var0[var4] == var1) {
				return var4;
			}

			if (var0[var4] < var1) {
				var2 = var4 + 1;
			} else {
				var3 = var4;
			}
		}

		return -1 * (var4 + 1);
	}

	public static final int binarySearch(int[] var0, int var1, int var2) {
		int var3 = 0;
		int var4 = var2;

		int var5;
		for(var5 = var2 / 2; var3 < var4; var5 = var3 + (var4 - var3) / 2) {
			if (var0[var5] == var1) {
				return var5;
			}

			if (var0[var5] < var1) {
				var3 = var5 + 1;
			} else {
				var4 = var5;
			}
		}

		return -1 * (var5 + 1);
	}

	public static final int binarySearch(int[] var0, int var1, int var2, int var3) {
		int var4 = var2;
		int var5 = var3;

		int var6;
		for(var6 = var2 + (var3 - var2) / 2; var4 < var5; var6 = var4 + (var5 - var4) / 2) {
			if (var0[var6] == var1) {
				return var6;
			}

			if (var0[var6] < var1) {
				var4 = var6 + 1;
			} else {
				var5 = var6;
			}
		}

		return -1 * (var6 + 1);
	}

	public static final int binarySearch(Object[] var0, Object var1) {
		int var2 = 0;
		int var3 = var0.length;

		int var4;
		for(var4 = var3 / 2; var2 < var3; var4 = var2 + (var3 - var2) / 2) {
			int var5 = ((Comparable)var0[var4]).compareTo(var1);
			if (var5 == 0) {
				return var4;
			}

			if (var5 < 0) {
				var2 = var4 + 1;
			} else {
				var3 = var4;
			}
		}

		return -1 * (var4 + 1);
	}

	public static final int binarySearch(Object[] var0, Object var1, int var2) {
		int var3 = 0;
		int var4 = var2;

		int var5;
		for(var5 = var2 / 2; var3 < var4; var5 = var3 + (var4 - var3) / 2) {
			int var6 = ((Comparable)var0[var5]).compareTo(var1);
			if (var6 == 0) {
				return var5;
			}

			if (var6 < 0) {
				var3 = var5 + 1;
			} else {
				var4 = var5;
			}
		}

		return -1 * (var5 + 1);
	}

	public static final int binarySearch(Object[] var0, Object var1, int var2, int var3) {
		int var4 = var2;
		int var5 = var3;

		int var6;
		for(var6 = var2 + (var3 - var2) / 2; var4 < var5; var6 = var4 + (var5 - var4) / 2) {
			int var7 = ((Comparable)var0[var6]).compareTo(var1);
			if (var7 == 0) {
				return var6;
			}

			if (var7 < 0) {
				var4 = var6 + 1;
			} else {
				var5 = var6;
			}
		}

		return -1 * (var6 + 1);
	}

	public static final int binarySearch(Object[] var0, Object var1, Comparator var2) {
		int var3 = 0;
		int var4 = var0.length;

		int var5;
		for(var5 = var4 / 2; var3 < var4; var5 = var3 + (var4 - var3) / 2) {
			int var6 = var2.compare(var0[var5], var1);
			if (var6 == 0) {
				return var5;
			}

			if (var6 < 0) {
				var3 = var5 + 1;
			} else {
				var4 = var5;
			}
		}

		return -1 * (var5 + 1);
	}

	public static final int binarySearch(Object[] var0, Object var1, Comparator var2, int var3) {
		int var4 = 0;
		int var5 = var3;

		int var6;
		for(var6 = var3 / 2; var4 < var5; var6 = var4 + (var5 - var4) / 2) {
			int var7 = var2.compare(var0[var6], var1);
			if (var7 == 0) {
				return var6;
			}

			if (var7 < 0) {
				var4 = var6 + 1;
			} else {
				var5 = var6;
			}
		}

		return -1 * (var6 + 1);
	}

	public static final int binarySearch(Object[] var0, Object var1, Comparator var2, int var3, int var4) {
		int var5 = var3;
		int var6 = var4;

		int var7;
		for(var7 = var3 + (var4 - var3) / 2; var5 < var6; var7 = var5 + (var6 - var5) / 2) {
			int var8 = var2.compare(var0[var7], var1);
			if (var8 == 0) {
				return var7;
			}

			if (var8 < 0) {
				var5 = var7 + 1;
			} else {
				var6 = var7;
			}
		}

		return -1 * (var7 + 1);
	}

	public static final int find(int[] var0, int var1) {
		for(int var2 = 0; var2 < var0.length; ++var2) {
			if (var0[var2] == var1) {
				return var2;
			}
		}

		return -1;
	}

	public static final int find(int[] var0, int var1, int var2) {
		for(int var3 = 0; var3 < var2; ++var3) {
			if (var0[var3] == var1) {
				return var3;
			}
		}

		return -1;
	}

	public static final int find(int[] var0, int var1, int var2, int var3) {
		for(int var4 = var2; var4 < var3; ++var4) {
			if (var0[var4] == var1) {
				return var4;
			}
		}

		return -1;
	}

	public static final int[] resize(int[] var0, int var1) {
		int[] var2 = new int[var1];
		System.arraycopy(var0, 0, var2, 0, var0.length);
		return var2;
	}

	public static final float[] resize(float[] var0, int var1) {
		float[] var2 = new float[var1];
		System.arraycopy(var0, 0, var2, 0, var0.length);
		return var2;
	}

	public static final double[] resize(double[] var0, int var1) {
		double[] var2 = new double[var1];
		System.arraycopy(var0, 0, var2, 0, var0.length);
		return var2;
	}

	public static final Object[] resize(Object[] var0, int var1) {
		Object[] var2 = new Object[var1];
		System.arraycopy(var0, 0, var2, 0, var0.length);
		return var2;
	}

	public static final int[] trim(int[] var0, int var1) {
		if (var0.length == var1) {
			return var0;
		} else {
			int[] var2 = new int[var1];
			System.arraycopy(var0, 0, var2, 0, var1);
			return var2;
		}
	}

	public static final float[] trim(float[] var0, int var1) {
		if (var0.length == var1) {
			return var0;
		} else {
			float[] var2 = new float[var1];
			System.arraycopy(var0, 0, var2, 0, var1);
			return var2;
		}
	}

	public static final double[] trim(double[] var0, int var1) {
		if (var0.length == var1) {
			return var0;
		} else {
			double[] var2 = new double[var1];
			System.arraycopy(var0, 0, var2, 0, var1);
			return var2;
		}
	}

	public static final void sort(int[] var0, double[] var1) {
		mergesort((int[])var0, (double[])var1, 0, var0.length - 1);
	}

	public static final void sort(int[] var0, double[] var1, int var2) {
		mergesort((int[])var0, (double[])var1, 0, var2 - 1);
	}

	public static final void sort(int[] var0, double[] var1, int var2, int var3) {
		mergesort(var0, var1, var2, var3 - 1);
	}

	public static final void insertionsort(int[] var0, double[] var1, int var2, int var3) {
		for(int var4 = var2 + 1; var4 <= var3; ++var4) {
			int var5 = var0[var4];
			double var6 = var1[var4];

			int var8;
			for(var8 = var4 - 1; var8 >= var2 && var0[var8] > var5; --var8) {
				var0[var8 + 1] = var0[var8];
				var1[var8 + 1] = var1[var8];
			}

			var0[var8 + 1] = var5;
			var1[var8 + 1] = var6;
		}

	}

	public static final void mergesort(int[] var0, double[] var1, int var2, int var3) {
		if (var2 < var3) {
			if (var3 - var2 + 1 < 1000) {
				insertionsort(var0, var1, var2, var3);
			} else {
				int var4 = (var2 + var3) / 2;
				mergesort(var0, var1, var2, var4);
				mergesort(var0, var1, var4 + 1, var3);
				merge(var0, var1, var2, var4, var3);
			}

		}
	}

	public static final void merge(int[] var0, double[] var1, int var2, int var3, int var4) {
		int[] var5 = new int[var4 - var2 + 1];
		double[] var6 = new double[var4 - var2 + 1];
		int var8 = var2;
		int var9 = var3 + 1;

		int var7;
		for(var7 = 0; var8 <= var3 && var9 <= var4; ++var7) {
			if (var0[var8] < var0[var9]) {
				var6[var7] = var1[var8];
				var5[var7] = var0[var8++];
			} else {
				var6[var7] = var1[var9];
				var5[var7] = var0[var9++];
			}
		}

		while(var8 <= var3) {
			var6[var7] = var1[var8];
			var5[var7] = var0[var8];
			++var8;
			++var7;
		}

		while(var9 <= var4) {
			var6[var7] = var1[var9];
			var5[var7] = var0[var9];
			++var9;
			++var7;
		}

		var7 = 0;

		for(var8 = var2; var7 < var5.length; ++var8) {
			var1[var8] = var6[var7];
			var0[var8] = var5[var7];
			++var7;
		}

	}

	public static final void quicksort(int[] var0, double[] var1, int var2, int var3) {
		if (var2 < var3) {
			int var4 = partition(var0, var1, var2, var3);
			quicksort(var0, var1, var2, var4);
			quicksort(var0, var1, var4 + 1, var3);
		}
	}

	private static final int partition(int[] var0, double[] var1, int var2, int var3) {
		int var4 = var0[var2];
		int var5 = var2;
		int var6 = var3;

		while(true) {
			while(var0[var6] <= var4) {
				while(var0[var5] < var4) {
					++var5;
				}

				if (var5 >= var6) {
					return var6;
				}

				int var7 = var0[var5];
				double var8 = var1[var5];
				var0[var5] = var0[var6];
				var1[var5] = var1[var6];
				var0[var6] = var7;
				var1[var6] = var8;
			}

			--var6;
		}
	}

	public static final void sort(int[] var0, int[] var1) {
		mergesort((int[])var0, (int[])var1, 0, var0.length - 1);
	}

	public static final void sort(int[] var0, int[] var1, int var2) {
		mergesort((int[])var0, (int[])var1, 0, var2 - 1);
	}

	public static final void sort(int[] var0, int[] var1, int var2, int var3) {
		mergesort(var0, var1, var2, var3 - 1);
	}

	public static final void insertionsort(int[] var0, int[] var1, int var2, int var3) {
		for(int var4 = var2 + 1; var4 <= var3; ++var4) {
			int var5 = var0[var4];
			int var6 = var1[var4];

			int var7;
			for(var7 = var4 - 1; var7 >= var2 && var0[var7] > var5; --var7) {
				var0[var7 + 1] = var0[var7];
				var1[var7 + 1] = var1[var7];
			}

			var0[var7 + 1] = var5;
			var1[var7 + 1] = var6;
		}

	}

	public static final void mergesort(int[] var0, int[] var1, int var2, int var3) {
		if (var2 < var3) {
			if (var3 - var2 + 1 < 1000) {
				insertionsort(var0, var1, var2, var3);
			} else {
				int var4 = (var2 + var3) / 2;
				mergesort(var0, var1, var2, var4);
				mergesort(var0, var1, var4 + 1, var3);
				merge(var0, var1, var2, var4, var3);
			}

		}
	}

	public static final void merge(int[] var0, int[] var1, int var2, int var3, int var4) {
		int[] var5 = new int[var4 - var2 + 1];
		int[] var6 = new int[var4 - var2 + 1];
		int var8 = var2;
		int var9 = var3 + 1;

		int var7;
		for(var7 = 0; var8 <= var3 && var9 <= var4; ++var7) {
			if (var0[var8] < var0[var9]) {
				var6[var7] = var1[var8];
				var5[var7] = var0[var8++];
			} else {
				var6[var7] = var1[var9];
				var5[var7] = var0[var9++];
			}
		}

		while(var8 <= var3) {
			var6[var7] = var1[var8];
			var5[var7] = var0[var8];
			++var8;
			++var7;
		}

		while(var9 <= var4) {
			var6[var7] = var1[var9];
			var5[var7] = var0[var9];
			++var9;
			++var7;
		}

		var7 = 0;

		for(var8 = var2; var7 < var5.length; ++var8) {
			var1[var8] = var6[var7];
			var0[var8] = var5[var7];
			++var7;
		}

	}

	public static final void quicksort(int[] var0, int[] var1, int var2, int var3) {
		if (var2 < var3) {
			int var4 = partition(var0, var1, var2, var3);
			quicksort(var0, var1, var2, var4);
			quicksort(var0, var1, var4 + 1, var3);
		}
	}

	private static final int partition(int[] var0, int[] var1, int var2, int var3) {
		int var4 = var0[var2];
		int var5 = var2;
		int var6 = var3;

		while(true) {
			while(var0[var6] <= var4) {
				while(var0[var5] < var4) {
					++var5;
				}

				if (var5 >= var6) {
					return var6;
				}

				int var7 = var0[var5];
				int var8 = var1[var5];
				var0[var5] = var0[var6];
				var1[var5] = var1[var6];
				var0[var6] = var7;
				var1[var6] = var8;
			}

			--var6;
		}
	}

	public static final void sort(float[] var0, int[] var1) {
		mergesort((float[])var0, (int[])var1, 0, var0.length - 1);
	}

	public static final void sort(float[] var0, int[] var1, int var2) {
		mergesort((float[])var0, (int[])var1, 0, var2 - 1);
	}

	public static final void sort(float[] var0, int[] var1, int var2, int var3) {
		mergesort(var0, var1, var2, var3 - 1);
	}

	public static final void insertionsort(float[] var0, int[] var1, int var2, int var3) {
		for(int var4 = var2 + 1; var4 <= var3; ++var4) {
			float var5 = var0[var4];
			int var6 = var1[var4];

			int var7;
			for(var7 = var4 - 1; var7 >= var2 && var0[var7] > var5; --var7) {
				var0[var7 + 1] = var0[var7];
				var1[var7 + 1] = var1[var7];
			}

			var0[var7 + 1] = var5;
			var1[var7 + 1] = var6;
		}

	}

	public static final void mergesort(float[] var0, int[] var1, int var2, int var3) {
		if (var2 < var3) {
			if (var3 - var2 + 1 < 1000) {
				insertionsort(var0, var1, var2, var3);
			} else {
				int var4 = (var2 + var3) / 2;
				mergesort(var0, var1, var2, var4);
				mergesort(var0, var1, var4 + 1, var3);
				merge(var0, var1, var2, var4, var3);
			}

		}
	}

	public static final void merge(float[] var0, int[] var1, int var2, int var3, int var4) {
		float[] var5 = new float[var4 - var2 + 1];
		int[] var6 = new int[var4 - var2 + 1];
		int var8 = var2;
		int var9 = var3 + 1;

		int var7;
		for(var7 = 0; var8 <= var3 && var9 <= var4; ++var7) {
			if (var0[var8] < var0[var9]) {
				var6[var7] = var1[var8];
				var5[var7] = var0[var8++];
			} else {
				var6[var7] = var1[var9];
				var5[var7] = var0[var9++];
			}
		}

		while(var8 <= var3) {
			var6[var7] = var1[var8];
			var5[var7] = var0[var8];
			++var8;
			++var7;
		}

		while(var9 <= var4) {
			var6[var7] = var1[var9];
			var5[var7] = var0[var9];
			++var9;
			++var7;
		}

		var7 = 0;

		for(var8 = var2; var7 < var5.length; ++var8) {
			var1[var8] = var6[var7];
			var0[var8] = var5[var7];
			++var7;
		}

	}

	public static final void quicksort(float[] var0, int[] var1, int var2, int var3) {
		if (var2 < var3) {
			int var4 = partition(var0, var1, var2, var3);
			quicksort(var0, var1, var2, var4);
			quicksort(var0, var1, var4 + 1, var3);
		}
	}

	private static final int partition(float[] var0, int[] var1, int var2, int var3) {
		float var4 = var0[var2];
		int var5 = var2;
		int var6 = var3;

		while(true) {
			while(var0[var6] <= var4) {
				while(var0[var5] < var4) {
					++var5;
				}

				if (var5 >= var6) {
					return var6;
				}

				float var7 = var0[var5];
				int var8 = var1[var5];
				var0[var5] = var0[var6];
				var1[var5] = var1[var6];
				var0[var6] = var7;
				var1[var6] = var8;
			}

			--var6;
		}
	}

	public static int[] getIntArray(String var0) {
		Object var1 = null;

		try {
			BufferedReader var2 = new BufferedReader(new FileReader(var0));
			String var3 = var2.readLine();
			StringTokenizer var4 = new StringTokenizer(var3);
			int var5 = var4.countTokens();
			int var6 = 0;
			int[] var9 = new int[var5];

			while(var4.hasMoreTokens()) {
				String var7 = var4.nextToken();
				if (!var7.startsWith("#")) {
					var9[var6++] = Integer.parseInt(var7);
				}
			}

			if (var6 != var5) {
				var9 = trim(var9, var6);
			}

			return var9;
		} catch (Exception var8) {
			var8.printStackTrace();
			return null;
		}
	}
}
