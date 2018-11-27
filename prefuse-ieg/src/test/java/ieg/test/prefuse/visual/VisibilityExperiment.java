package ieg.test.prefuse.visual;

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataShapeAction;
import prefuse.action.filter.VisibilityFilter;
import prefuse.action.layout.AxisLabelLayout;
import prefuse.action.layout.AxisLayout;
import prefuse.controls.PanControl;
import prefuse.controls.ToolTipControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Table;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.data.query.NumberRangeModel;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.AxisRenderer;
import prefuse.render.Renderer;
import prefuse.render.RendererFactory;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTable;
import prefuse.visual.expression.VisiblePredicate;
import prefuse.visual.sort.ItemSorter;

public class VisibilityExperiment {

	private static Table generateTable() {
		Table table = new Table();

		// use a calendar for input of human-readable dates
		GregorianCalendar cal = new GregorianCalendar();

		// set up table schema
		table.addColumn("Date", Date.class);
		table.addColumn("BMI", double.class);
		table.addColumn("NBZ", int.class);
		table.addColumn("Insult", String.class);

		table.addRows(3);

		cal.set(2007, 11, 23);
		table.set(0, 0, cal.getTime());
		table.set(0, 1, 21.0);
		table.set(0, 2, 236);
		table.set(0, 3, "F");

		cal.set(2008, 6, 22);
		table.set(1, 0, cal.getTime());
		table.set(1, 1, 35.8);
		table.set(1, 2, 400);
		table.set(1, 3, "F");

		cal.set(2009, 3, 8);
		table.set(2, 0, cal.getTime());
		table.set(2, 1, 28.8);
		table.set(2, 2, 309);
		table.set(2, 3, "T");

		return table;
	}

	private static JComponent createVisualization(Table data) {
		final Visualization vis = new Visualization();
		final Display display = new Display(vis);

		final Rectangle2D boundsData = new Rectangle2D.Double();
		final Rectangle2D boundsLabelsX = new Rectangle2D.Double();
		final Rectangle2D boundsLabelsY = new Rectangle2D.Double();

		// --------------------------------------------------------------------
		// STEP 1: setup the visualized data

		VisualTable vt = vis.addTable("data", data);

		// add a new column containing a label string
		vt.addColumn("label",
				"CONCAT('NBZ: ', [NBZ], '; BMI: ', FORMAT([BMI],1))");

		// --------------------------------------------------------------------
		// STEP 2: set up renderers for the visual data

		vis.setRendererFactory(new RendererFactory() {
			AbstractShapeRenderer sr = new ShapeRenderer(7) {

				// Lesson learned: Display enforces VISIBLE() as filter predicate
				@Override
				public void render(Graphics2D g, VisualItem item) {
					System.out.println("render shape " + item);
					super.render(g, item);
				}

			};
			Renderer arY = new AxisRenderer(Constants.FAR_LEFT,
					Constants.CENTER);
			Renderer arX = new AxisRenderer(Constants.CENTER,
					Constants.FAR_BOTTOM);

			public Renderer getRenderer(VisualItem item) {
				return item.isInGroup("ylab") ? arY
						: item.isInGroup("xlab") ? arX : sr;
			}
		});

		// --------------------------------------------------------------------
		// STEP 3: create actions to process the visual data

		AxisLayout x_axis = new AxisLayout("data", "NBZ", Constants.X_AXIS,
				VisiblePredicate.TRUE);

		AxisLayout y_axis = new AxisLayout("data", "BMI", Constants.Y_AXIS,
				VisiblePredicate.TRUE);

		x_axis.setLayoutBounds(boundsData);
		y_axis.setLayoutBounds(boundsData);

		AxisLabelLayout x_labels = new AxisLabelLayout("xlab", x_axis,
				boundsLabelsX);

		AxisLabelLayout y_labels = new AxisLabelLayout("ylab", y_axis,
				boundsLabelsY);

		// define the visible range for the y axis
		y_axis.setRangeModel(new NumberRangeModel(1, 40, 1, 40));

		// use square root scale for y axis
		y_axis.setScale(Constants.SQRT_SCALE);
		y_labels.setScale(Constants.SQRT_SCALE);

		// use a special format for y axis labels
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);
		y_labels.setNumberFormat(nf);

		ColorAction color = new ColorAction("data", VisualItem.STROKECOLOR,
				ColorLib.rgb(100, 100, 255));

		int[] palette = {Constants.SHAPE_STAR, Constants.SHAPE_ELLIPSE};
		DataShapeAction shape = new DataShapeAction("data", "Insult", palette) {

			@Override
			public int getShape(VisualItem item) {
				// Lesson learned: ItemAction defaults to VISIBLE() as filter predicate
				System.out.println("shape action " + item);
				System.out.println("shape predicate " + m_predicate);
				return super.getShape(item);
			}
		};

		ActionList draw = new ActionList();
		draw.add(new VisibilityFilter(ExpressionParser.predicate("[BMI]>25")));
		draw.add(x_axis);
		draw.add(y_axis);
		draw.add(x_labels);
		draw.add(y_labels);
		draw.add(color);
		draw.add(shape);
		draw.add(new RepaintAction());
		vis.putAction("draw", draw);

		ActionList update = new ActionList();
		update.add(x_axis);
		update.add(y_axis);
		update.add(x_labels);
		update.add(y_labels);
		update.add(new RepaintAction());
		vis.putAction("update", update);

		// --------------------------------------------------------------------
		// STEP 4: set up a display and controls
		display.setHighQuality(true);
		display.setSize(700, 450);

		display.setBorder(BorderFactory.createTitledBorder("Demo"));

		// show data items in front of axis labels
		display.setItemSorter(new ItemSorter() {
			public int score(VisualItem item) {
				int score = super.score(item);
				if (item.isInGroup("data"))
					score++;
				return score;
			}
		});

		// react on window resize
		display.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				updateBounds(display, boundsData, boundsLabelsX, boundsLabelsY);
				vis.run("update");
			}
		});

		ToolTipControl ttc = new ToolTipControl("label");
		display.addControlListener(ttc);

		display.addControlListener(new PanControl());
		display.addControlListener(new ZoomControl());
		display.addControlListener(new ZoomToFitControl());

		// --------------------------------------------------------------------
		// STEP 5: launching the visualization
		updateBounds(display, boundsData, boundsLabelsX, boundsLabelsY);
		vis.run("draw");

		return display;
	}

	private static void updateBounds(Display display, Rectangle2D boundsData,
	                                 Rectangle2D boundsLabelsX, Rectangle2D boundsLabelsY) {

		int paddingLeft = 30;
		int paddingTop = 15;
		int paddingRight = 30;
		int paddingBottom = 15;

		int axisWidth = 20;
		int axisHeight = 10;

		Insets i = display.getInsets();

		int left = i.left + paddingLeft;
		int top = i.top + paddingTop;
		int innerWidth = display.getWidth() - i.left - i.right - paddingLeft
				- paddingRight;
		int innerHeight = display.getHeight() - i.top - i.bottom - paddingTop
				- paddingBottom;

		boundsData.setRect(left + axisWidth, top, innerWidth - axisWidth,
				innerHeight - axisHeight);
		boundsLabelsX.setRect(left + axisWidth, top + innerHeight - axisHeight,
				innerWidth - axisWidth, axisHeight);
		boundsLabelsY.setRect(left, top, innerWidth + paddingRight, innerHeight
				- axisHeight);
	}

	private static void createAndShowGUI(JComponent display) {
		JFrame frame = new JFrame();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("p r e f u s e | s c a t t e r   p l o t");

		frame.getContentPane().add(display);

		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		UILib.setPlatformLookAndFeel();

		Table table = generateTable();
		final JComponent display = createVisualization(table);

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI(display);
			}
		});
	}

	// TODO subclasses with logs
}
