package edu.berkeley.guir.prefuse.util.display;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class ScaleSelector
		extends JComponent
		implements ChangeListener {
	private static final int MAX_SIZE = 135;
	private ImagePanel preview = new ImagePanel();
	private JLabel value = new JLabel("x1");
	private JLabel size = new JLabel("   ");
	private JSlider slider = new JSlider(1, 10, 1);
	private Image image;
	private int width;
	private int height;

	public ScaleSelector() {
		this.value.setPreferredSize(new Dimension(25, 10));
		this.size.setHorizontalAlignment(0);
		this.slider.setMajorTickSpacing(1);
		this.slider.setSnapToTicks(true);
		this.slider.addChangeListener(this);
		setLayout(new BorderLayout());
		Box localBox1 = new Box(0);
		localBox1.add(Box.createHorizontalStrut(5));
		localBox1.add(Box.createHorizontalGlue());
		localBox1.add(this.preview);
		localBox1.add(Box.createHorizontalGlue());
		localBox1.add(Box.createHorizontalStrut(5));
		add(localBox1, "Center");
		Box localBox2 = new Box(0);
		localBox2.add(this.slider);
		localBox2.add(Box.createHorizontalStrut(5));
		localBox2.add(this.value);
		Box localBox3 = new Box(0);
		localBox3.add(Box.createHorizontalStrut(5));
		localBox3.add(Box.createHorizontalGlue());
		localBox3.add(this.size);
		localBox3.add(Box.createHorizontalGlue());
		localBox3.add(Box.createHorizontalStrut(5));
		Box localBox4 = new Box(1);
		localBox4.add(localBox2);
		localBox4.add(localBox3);
		add(localBox4, "South");
	}

	public void setImage(Image paramImage) {
		this.image = getScaledImage(paramImage);
		stateChanged(null);
	}

	private Image getScaledImage(Image paramImage) {
		int i = this.width = paramImage.getWidth(null);
		int j = this.height = paramImage.getHeight(null);
		double d = i / j;
		int k = 135;
		int m = 135;
		if (i > j) {
			m = (int) Math.round(k / d);
		} else {
			k = (int) Math.round(m * d);
		}
		return paramImage.getScaledInstance(k, m, 4);
	}

	public void stateChanged(ChangeEvent paramChangeEvent) {
		int i = this.slider.getValue();
		this.value.setText("x" + String.valueOf(i));
		this.size.setText("Image Size: " + this.width * i + " x " + this.height * i + " pixels");
		this.preview.repaint();
	}

	public double getScale() {
		return this.slider.getValue();
	}

	public class ImagePanel
			extends JComponent {
		Dimension d = new Dimension(135, 135);

		public ImagePanel() {
			setPreferredSize(this.d);
			setMinimumSize(this.d);
			setMaximumSize(this.d);
		}

		public void paintComponent(Graphics paramGraphics) {
			double d1 = 0.4D + 0.06D * ScaleSelector.this.getScale();
			int i = (int) Math.round(d1 * ScaleSelector.this.image.getWidth(null));
			int j = (int) Math.round(d1 * ScaleSelector.this.image.getHeight(null));
			Image localImage = d1 == 1.0D ? ScaleSelector.this.image : ScaleSelector.this.image.getScaledInstance(i, j, 1);
			int k = (135 - i) / 2;
			int m = (135 - j) / 2;
			paramGraphics.drawImage(localImage, k, m, null);
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/util/display/ScaleSelector.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */