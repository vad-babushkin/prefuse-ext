package ieg.util.lang;

import java.util.Random;

/**
 * some additional mathematical utilities.
 * @author Alexander Rind
 */
public class MoreMath {

	/**
	 * randomly orders integers from <tt>0</tt> to <tt>n</tt> using Durstenfeld-Fisher-Yates shuffling.
	 * These can be used as unique random numbers. 
	 * @param n number of returned integers
	 * @return array of length <tt>n</tt>
	 */
	public static int[] generateRandomlyOrderedIntegers(int n) {
		// http://stackoverflow.com/questions/196017/unique-non-repeating-random-numbers-in-o1#196065
		// http://stackoverflow.com/questions/1519736/random-shuffling-of-an-array#1520212
		
		int[] result = new int[n];
		for (int i = 0; i < n; i++) {
			result[i] = i;
		}
		// If running on Java 6 or older, use `new Random()` on RHS here
		// on Java 7++: java.util.concurrent.ThreadLocalRandom.current()
		Random rnd = new Random();
		for (int i = n - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			// Simple swap
			int temp = result[index];
			result[index] = result[i];
			result[i] = temp;
		}
		return result;
	}

	/**
	 * create a random alpha-numerical string, which is suitable as pseudo-unique id
	 * @param chars length of output
	 * @return a random alpha-numerical string
	 */
	public static String getRandomAlphaNum(int chars) {
	    // 4 digit number in base36 (alphanumerical)
	    // 46656 = 1000x36 = 36^3
	    // 1632960 = Z000x36 = 36^4 - 36^3 
	    final int base = 36;
	    int low = (int) Math.round(Math.pow(base, chars - 1));
	    int extent = low * (base - 1);
	    int code = (int) (Math.random() * extent) + low;
	    return Integer.toString(code, base);
	}

}
