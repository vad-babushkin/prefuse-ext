package evaluation.evalBench;

import java.util.ResourceBundle;

/**
 * resource bundle wrapper
 */
public class EvaluationResources {

	private static final ResourceBundle resourceBundle = ResourceBundle
			.getBundle(EvaluationResources.class.getPackage().getName()
					+ ".evaluationGui");

	/**
	 * Prevent instantiation
	 */
	private EvaluationResources() {
	}

	/**
	 * Retrieves a string from the resource bundle
	 */
	public static String getString(String key) {
		return resourceBundle.getString(key);
	}
}