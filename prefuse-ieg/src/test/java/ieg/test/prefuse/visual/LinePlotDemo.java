package ieg.test.prefuse.visual;

import ieg.prefuse.action.layout.LinePlotAction;
import ieg.prefuse.action.layout.LinePlotLayout;
import ieg.prefuse.renderer.LineRenderer;
import ieg.prefuse.renderer.StepChartLineRenderer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.filter.VisibilityFilter;
import prefuse.action.layout.AxisLayout;
import prefuse.controls.ToolTipControl;
import prefuse.data.Table;
import prefuse.data.expression.AndPredicate;
import prefuse.data.expression.OrPredicate;
import prefuse.data.expression.Predicate;
import prefuse.data.io.DelimitedTextTableReader;
import prefuse.data.query.RangeQueryBinding;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.UpdateListener;
import prefuse.util.ui.JRangeSlider;
import prefuse.util.ui.JToggleGroup;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTable;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.expression.VisiblePredicate;
import prefuse.visual.sort.ItemSorter;

@SuppressWarnings("serial")
public class LinePlotDemo extends JPanel {
	public static final String POINTS = "points";
	public static final String LINES = "lines";

	public static final int DEFAULT_POINT_SIZE = 4;

	private static Visualization myVisualization;
	private Display myDisplay;
	private ShapeRenderer myShapeRenderer = new ShapeRenderer(DEFAULT_POINT_SIZE);
	//    private LineRenderer myLineRenderer = new LineRenderer();
	private LineRenderer myLineRenderer = new StepChartLineRenderer();

	private OrPredicate myDisplayPredicate = new OrPredicate();
	private boolean showLines = true, m_showPoints = false;
	private Predicate myPointsPredicate = new InGroupPredicate(POINTS);
	private Predicate myLinesPredicate = new InGroupPredicate(LINES);

	public static void main(String[] argv) {
		String data = "../TimeBench/data/climate.csv";
		String xfield = "Date";
		String yfield = "AvgTemp";

		// -- 1. load the data ------------------------------------------------

		Table table = null;
		try {
			table = new DelimitedTextTableReader().readTable(data);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error reading file. Exiting...");
			System.exit(1);
		}

		// -- 2-6. the visualization --------------------------------------------
		LinePlotDemo lineChart = new LinePlotDemo(table, xfield, yfield);

		// -- 7. launch the visualization -------------------------------------
		JFrame frame = new JFrame("p r e f u s e  |  l i n e");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(lineChart, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}


	public LinePlotDemo(Table t, String xfield, String yfield) {
		super(new BorderLayout());

		// --------------------------------------------------------------------
		// STEP 1: setup the visualized data

		// create visualization
		myVisualization = new Visualization();
		// add tuples of data table to visualization (points)
		VisualTable myVisualTable = myVisualization.addTable(POINTS, t);

		// create new table schema for lines table (visualization of lines that connect points)
//        Schema lineSchema = PrefuseLib.getVisualItemSchema();
//        lineSchema.addColumn(VisualItem.X2, double.class);
//        lineSchema.addColumn(VisualItem.Y2, double.class);
//        lineSchema.addColumn("v1", VisualItem.class);
//        lineSchema.addColumn("v2", VisualItem.class);
//        myVisualization.addTable(LINES, lineSchema);

		// create RendererFactory
		// renderer for points
		DefaultRendererFactory myRendererFactory = new DefaultRendererFactory(myShapeRenderer);
		// renderer for lines
		myRendererFactory.add(myLinesPredicate, myLineRenderer);
		// add RendererFactory to visualization
		myVisualization.setRendererFactory(myRendererFactory);

		// --------------------------------------------------------------------
		// STEP 2: create actions to process the visual data

		// set up dynamic queries, search set
		RangeQueryBinding xAxisQueryBinding = new RangeQueryBinding(myVisualTable, xfield);
		RangeQueryBinding yAxisQueryBinding = new RangeQueryBinding(myVisualTable, yfield);

		// construct the filtering predicate
		AndPredicate filter = new AndPredicate(xAxisQueryBinding.getPredicate(),
				yAxisQueryBinding.getPredicate());

		// set up the actions
		AxisLayout x_axis = new AxisLayout(POINTS, xfield,
				Constants.X_AXIS, VisiblePredicate.TRUE);
		x_axis.setRangeModel(xAxisQueryBinding.getModel());

		AxisLayout y_axis = new AxisLayout(POINTS, yfield,
				Constants.Y_AXIS, VisiblePredicate.TRUE);
		y_axis.setRangeModel(yAxisQueryBinding.getModel());

		// lineFilter creates line segments from points
		LinePlotAction lineFilter = new LinePlotAction(LINES, POINTS, VisualItem.X);
		// lineLayout updates x and y coordinates of lines
		LinePlotLayout lineLayout = new LinePlotLayout(LINES);

		// set point color
		ColorAction color = new ColorAction(POINTS,
				VisualItem.FILLCOLOR, ColorLib.rgb(100, 100, 255));
		// set line color
		ColorAction lineColor = new ColorAction(LINES,
				VisualItem.STROKECOLOR, ColorLib.rgb(50, 50, 125));

		// setup acion list for initial drawing
		ActionList draw = new ActionList();
		draw.add(x_axis); // x-axis layout (points)
		draw.add(y_axis); // y-axis layout (points)
		draw.add(lineFilter); // create line segments from points
		draw.add(color); // set point color
		draw.add(lineColor); // set line color
		draw.add(new RepaintAction()); // do repaint
		myVisualization.putAction("draw", draw);

		// setup action list for drawing update
		ActionList update = new ActionList();
		update.add(new VisibilityFilter(POINTS, filter)); // dynamic query predicate (range slider - determine the visible part)
		update.add(x_axis); // x-axis layout (points)
		update.add(y_axis); // y-axis layout (points)
		update.add(lineLayout); // update line coordinates
		update.add(new RepaintAction()); // do repaint
		myVisualization.putAction("update", update);

		// react upon predicate change (dynamic query / range slider) --> repaint
		UpdateListener lstnr = new UpdateListener() {
			public void update(Object src) {
				myVisualization.run("update");
			}
		};
		filter.addExpressionListener(lstnr);

		// --------------------------------------------------------------------
		// STEP 3: set up a display and ui components to show the visualization

		myDisplayPredicate.add(myLinesPredicate); // determine visibility of lines and points via check boxes
		// create display
		myDisplay = new Display(myVisualization, myDisplayPredicate);
		myDisplay.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		myDisplay.setSize(700, 450);
		myDisplay.setHighQuality(true);

		myDisplay.setItemSorter(new ItemSorter() {

			@Override
			public int score(VisualItem item) {
				int score = super.score(item);
				return LINES.equals(item.getGroup()) ? score - 1 : score;
			}

		});


		// enable tooltips for points
		ToolTipControl ttc = new ToolTipControl(new String[]{xfield, yfield});
		myDisplay.addControlListener(ttc);


		// --------------------------------------------------------------------
		// STEP 4: launching the visualization

		this.addComponentListener(lstnr);

		// create range slider for x and y axis
		JRangeSlider xslider = xAxisQueryBinding.createHorizontalRangeSlider();
		JRangeSlider yslider = yAxisQueryBinding.createVerticalRangeSlider();
		// set color of range slider thumb
		xslider.setThumbColor(Color.orange);
		yslider.setThumbColor(Color.orange);

		// set display quality to "low" while interaction takes place
		MouseAdapter qualityControl = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				myDisplay.setHighQuality(false);
			}

			public void mouseReleased(MouseEvent e) {
				myDisplay.setHighQuality(true);
				myDisplay.repaint();
			}
		};
		xslider.addMouseListener(qualityControl);
		yslider.addMouseListener(qualityControl);

		// set up point size selector
		final JSlider pointSizes = new JSlider(1, 10, DEFAULT_POINT_SIZE);
		pointSizes.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int val = ((JSlider) e.getSource()).getValue();
				myShapeRenderer.setBaseSize(val);
				myVisualization.invalidateAll();
				myVisualization.repaint();
			}
		});
		pointSizes.setEnabled(false);
		pointSizes.setSnapToTicks(true);
		pointSizes.setMaximumSize(new Dimension(200, 30));
		pointSizes.setPreferredSize(new Dimension(100, 30));

		// set up mark visibility toggles
		final JToggleGroup checks = new JToggleGroup(
				JToggleGroup.CHECKBOX, new String[]{"Lines", "Points"});
		checks.getSelectionModel().setSelectionInterval(0, 0);
		checks.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						ListSelectionModel sel = (ListSelectionModel) e.getSource();
						setShowLines(sel.isSelectedIndex(0));
						setShowPoints(sel.isSelectedIndex(1));
						pointSizes.setEnabled(sel.isSelectedIndex(1));
					}
				}
		);


		myVisualization.run("draw");


		// add UI components
		Box controls = new Box(BoxLayout.X_AXIS);
		controls.add(checks);
		controls.add(Box.createHorizontalStrut(10));
		controls.add(pointSizes);
		controls.add(new JLabel("Point Size"));
		controls.add(Box.createHorizontalStrut(5));
		controls.add(Box.createHorizontalGlue());

		add(controls, BorderLayout.NORTH);
		add(myDisplay, BorderLayout.CENTER);
		add(yslider, BorderLayout.EAST);

		Box xbox = new Box(BoxLayout.X_AXIS);
		xbox.add(xslider);
		int corner = yslider.getPreferredSize().width;
		xbox.add(Box.createHorizontalStrut(corner));
		add(xbox, BorderLayout.SOUTH);
	}

	public int getPointSize() {
		return myShapeRenderer.getBaseSize();
	}

	public void setPointSize(int size) {
		myShapeRenderer.setBaseSize(size);
		repaint();
	}

	public void setShowPoints(boolean b) {
		if (b != m_showPoints) {
			m_showPoints = b;
			updateDisplay();
		}
	}

	public void setShowLines(boolean b) {
		if (b != showLines) {
			showLines = b;
			updateDisplay();
		}
	}

	// update display predicate in response to check box configuration (visiblity of points and/or lines)
	protected void updateDisplay() {
		if (showLines && m_showPoints) {
			myDisplayPredicate.set(new Predicate[]{myLinesPredicate, myPointsPredicate});
		} else if (showLines) {
			myDisplayPredicate.set(myLinesPredicate);
		} else if (m_showPoints) {
			myDisplayPredicate.set(myPointsPredicate);
		} else {
			myDisplayPredicate.clear();
		}
		myDisplay.repaint();
	}

	public Display getDisplay() {
		return myDisplay;
	}

	public Visualization getVisualization() {
		return myVisualization;
	}

}
