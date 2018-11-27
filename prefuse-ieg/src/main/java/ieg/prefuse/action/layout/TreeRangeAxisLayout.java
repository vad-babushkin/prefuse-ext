package ieg.prefuse.action.layout;

import java.util.Iterator;

import prefuse.Constants;
import prefuse.action.layout.AxisLayout;
import prefuse.data.expression.ColumnExpression;
import prefuse.data.expression.ComparisonPredicate;
import prefuse.data.expression.NumericLiteral;
import prefuse.data.query.NumberRangeModel;
import prefuse.data.tuple.TableTuple;
import prefuse.data.tuple.TupleSet;
import prefuse.util.MathLib;
import prefuse.util.PrefuseLib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

public class TreeRangeAxisLayout extends AxisLayout {

	public TreeRangeAxisLayout(String group, String field, int axis) {
		super(group, field, axis);
	}

	@Override
	public void run(double frac) {
		TupleSet ts = m_vis.getGroup(m_group);

		if (!(ts instanceof VisualGraph))
			throw new UnsupportedOperationException();

		setMinMax();

		switch (getDataType(((VisualGraph) ts).getNodes())) {
			case Constants.NUMERICAL:
				numericalLayout((VisualGraph) ts);
				break;
			default:
				ordinalLayout((VisualGraph) ts);
		}
	}

	protected void numericalLayout(VisualGraph ts) {
		Iterator<?> roots = ts.getNodes().tuples(new ComparisonPredicate(ComparisonPredicate.EQ, new ColumnExpression("depth"), new NumericLiteral(0)));

		m_dist[0] = 0;
		m_dist[1] = 0;
		while (roots.hasNext())
			m_dist[1] += ((TableTuple) roots.next()).getDouble(m_field);

		double lo = m_dist[0], hi = m_dist[1];
		if (m_model == null) {
			m_model = new NumberRangeModel(lo, hi, lo, hi);
		} else {
			((NumberRangeModel) m_model).setValueRange(lo, hi, lo, hi);
		}

		roots = ts.getNodes().tuples(new ComparisonPredicate(ComparisonPredicate.EQ, new ColumnExpression("depth"), new NumericLiteral(0)));
		layout(roots, 0);
	}

	private void layout(Iterator<?> iterator, double base) {

		while (iterator.hasNext()) {
			NodeItem node = (NodeItem) iterator.next();
			VisualItem item = (VisualItem) node;
			double v = item.getDouble(m_field);

			double f1 = 0;
			double f2 = 0;
			f1 = MathLib.interp(m_scale, base, m_dist);
			f2 = MathLib.interp(m_scale, base + v, m_dist);

			set(item, f1, f2);
			layout(node.inNeighbors(), base);
			base += v;
		}
	}

	protected void set(VisualItem item, double frac1, double frac2) {
		double size = (frac2 - frac1);
		double xOrY = m_min + (frac1 + size / 2) * m_range;
		if (m_axis == Constants.X_AXIS) {
			setX(item, null, xOrY);
			PrefuseLib.setSizeX(item, null, size * m_range);
		} else {
			setY(item, null, xOrY);
			PrefuseLib.setSizeY(item, null, size * m_range);
		}
	}

	protected void ordinalLayout(VisualGraph ts) {
		throw new UnsupportedOperationException();
	}
}
