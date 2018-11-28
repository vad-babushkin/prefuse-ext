package evaluation.evalBench.session;

/**
 * permutates strings to be used for balancing evaluation sessions.  
 * @author Alexander Rind
 */
public abstract class BalanceOrder {
	
	/**
	 * arranges sessions so that in each permutation each session is at a different position
	 */
	public static final String LATIN_SQUARE = "latin-square";
	/**
	 * completely random order
	 */
	public static final String RANDOM = "random";
	
	/**
	 * @param sessions identifiers for evaluation sessions. Typically independents variables such as techniques
	 * @param index permutation requested. Can be incremented for each test participant
	 * @return a permutation of session identifiers. Same length as input array.
	 */
	public abstract String[] permutate(String[] sessions, int index);
	
	/**
	 * factory method for a balancing method.
	 * @param method balancing method. See constants.
	 * @return implementation of a balancing method.
	 */
	public static BalanceOrder byMethod(String method) {
		if (LATIN_SQUARE.equals(method)) {
			return new LatinSquareOrder(); 
		}
		else if (RANDOM.equals(method)) {
			return new RandomOrder(); 
		}
		else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Arranges sessions so that in each permutation each session is at a different position.
	 * @author Alexander Rind
	 */
	private static class LatinSquareOrder extends BalanceOrder {

		@Override
		public String[] permutate(String[] sessions, int index) {
			String[] order = new String[sessions.length];
			for (int i=0; i<sessions.length; i++) {
				int src = (i + index) % sessions.length; 
				order[i] = sessions[src];
			}
			return order;
		}
	}

	/**
	 * Permutates evaluation sessions in completely random order.
	 * @author Alexander Rind
	 */
	private static class RandomOrder extends BalanceOrder {

		@Override
		public String[] permutate(String[] sessions, int index) {
			String[] order = new String[sessions.length];
			int[] srcs = ieg.util.lang.MoreMath.generateRandomlyOrderedIntegers(sessions.length);
			for (int i=0; i<sessions.length; i++) {
				order[i] = sessions[srcs[i]];
			}
			return order;
		}
	}

}
