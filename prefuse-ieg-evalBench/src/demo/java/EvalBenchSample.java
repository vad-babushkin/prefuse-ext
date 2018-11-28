import java.util.Locale;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import evaluation.evalBench.EvaluationManager;

public class EvalBenchSample  {

	private static EvalBenchSample instance;
	
	private final EvalBenchSampleFrame frame;
	
	/**
	 * initialize a new frame for the sample application 
	 */
	private EvalBenchSample(){
		instance = this;
		
		frame = new EvalBenchSampleFrame();
		
		frame.setDefaultCloseOperation(EvalBenchSampleFrame.DISPOSE_ON_CLOSE);

		frame.pack();
		frame.setLocationRelativeTo(null);

		frame.setVisible(true);
		
	}
	
	/**
	 * start point
	 * @param args not used
	 */
	public static void main(String[] args) {
	    Locale.setDefault(Locale.US);
		initLnf();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new EvalBenchSample();
				EvaluationManager.getInstance().setDelegate(EvalBenchSample.getInstance().getFrame());
			}
		});
	}
	
	
	/**
	 * returns the current {@link EvalBenchSample} instance
	 * @return current {@link EvalBenchSample} instance
	 */
	public static EvalBenchSample getInstance() {
		return instance;
	}
	
	/**
	 * returns the current {@link EvalBenchSampleFrame}
	 * @return  current {@link EvalBenchSampleFrame}
	 */
	public EvalBenchSampleFrame getFrame() {
		return frame;
	}
	
	
	/**
	 * init look and feel (tries to set the system look and feel)
	 */
	public static void initLnf() {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("apple.awt.showGrowBox", "false");
		System.setProperty("apple.awt.brushMetalLook", "false");
		System.setProperty("apple.awt.brushMetalRounded", "true");

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("could not set look and feel " + e.getLocalizedMessage());
		}
	}
	

	

}
