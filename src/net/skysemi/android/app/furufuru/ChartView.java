package net.skysemi.android.app.furufuru;

import org.afree.chart.AFreeChart;
import org.afree.chart.axis.NumberAxis;
import org.afree.chart.plot.Plot;
import org.afree.data.xy.XYSeries;
import org.afree.data.xy.XYSeriesCollection;
import org.afree.graphics.SolidColor;
import org.afree.graphics.geom.RectShape;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

public class ChartView extends View {
	private AFreeChart chart;
	private RectShape chartArea;

	public ChartView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public XYSeriesCollection getDataSet(int[] dataList) {

		XYSeries xy = new XYSeries("データ");
		int i = 0;
		for (int value : dataList) {
			xy.add(i + 1, value);
			i++;
		}
		XYSeriesCollection dataset = new XYSeriesCollection(xy);

		return dataset;
	}

	public XYSeriesCollection getDataSet(float[] dataList) {

		XYSeries xy = new XYSeries("データ");
		int i = 0;
		for (float value : dataList) {
			xy.add(i + 1, value);
			i++;
		}
		XYSeriesCollection dataset = new XYSeriesCollection(xy);

		return dataset;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		ChartView view = (ChartView) findViewById(R.id.chartView);
		chartArea = new RectShape(0.0, 0.0, view.getWidth(), view.getHeight());
		this.chart.draw(canvas, chartArea);
		invalidate();

	}

	public void setCountChart(AFreeChart chart) {
		this.chart = chart;
		chart.setBackgroundPaintType(new SolidColor(Color.argb(255, 142, 255, 171))); // 背景の色
		chart.setBorderPaintType(new SolidColor(Color.argb(255, 142, 255, 171))); // 枠線の色

		NumberAxis domainAxis = (NumberAxis) chart.getXYPlot().getDomainAxis();
		domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		NumberAxis rangeAxis = (NumberAxis) chart.getXYPlot().getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

	}

	public void setCalorieChart(AFreeChart chart) {
		this.chart = chart;

		chart.setBackgroundPaintType(new SolidColor(Color.argb(255, 142, 212, 255))); // 背景の色
		chart.setBorderPaintType(new SolidColor(Color.argb(255, 142, 212, 255))); // 枠線の色

		NumberAxis domainAxis = (NumberAxis) chart.getXYPlot().getDomainAxis();
		domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

	}

}
