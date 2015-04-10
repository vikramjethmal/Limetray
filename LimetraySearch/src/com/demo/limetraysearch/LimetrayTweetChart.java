
package com.demo.limetraysearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

import com.demo.limetraysearch.dao.SearchData;

public class LimetrayTweetChart{
	/**
	 * Executes the chart demo.
	 * 
	 * @param context the context
	 * @param list 
	 * @return the built intent
	 */
	public Intent execute(Context context, List<SearchData> dataList) 
	{
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (SearchData searchData : dataList) { 
			
			if(map.containsKey(searchData.getDateCreated())){
				int i = map.get(searchData.getDateCreated());
				map.put(searchData.getDateCreated(), i+1);
			}
			else{
				map.put(searchData.getDateCreated(), 1);
			}
		}

		String[] titles = new String[] { "Limetray Tweet"};

		List<String[]> x = new ArrayList<String[]>();
		List<int[]> xValue = new ArrayList<int[]>();
		List<int[]> value = new ArrayList<int[]>();
		
		int[] tweetCount = new int[map.size()];
		int[] xVal = new int[map.size()];
		String[] dateStrList = new String[map.size()];
		
		int index = 0;
		
		for (Map.Entry<String,Integer> entry : map.entrySet()) {
		    System.out.println(entry.getKey());
		    System.out.println(entry.getValue());
		    dateStrList[index] = entry.getKey().toString().substring(0, 20);
		    xVal[index] = index;
		    tweetCount[index] = Integer.valueOf(entry.getValue().toString());
		    index++;
		}
		
		value.add(tweetCount);
		x.add(dateStrList);
		xValue.add(xVal);
		
		int[] colors = new int[] { Color.BLUE};

		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE };

		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);

		int length = renderer.getSeriesRendererCount();

		for (int i = 0; i < length; i++) 
		{
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
		}

		setChartSettings(renderer, "Limetray Tweet Graph", "Date-Time", "Tweet Count", -10,tweetCount.length, 0, 10,
				Color.LTGRAY, Color.LTGRAY);
		//Custom X Axis Series
		for(int i=0;i<tweetCount.length;i++)
			renderer.addXTextLabel(xVal[i], dateStrList[i]); 
		renderer.setXLabels(0);
		renderer.setXAxisMin(0);//give margin on X Axis increase this Value
		renderer.setXAxisMax(10);
		renderer.setYLabels(10);
		renderer.setShowGrid(true);
		renderer.setXLabelsAlign(Align.RIGHT);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setZoomButtonsVisible(true);
		renderer.setPanLimits(new double[] { -10, 20, -1, 10 });
		renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });

		XYMultipleSeriesDataset dataset = buildDataset(titles, xValue, value);
		Intent intent = ChartFactory.getLineChartIntent(context, dataset, renderer,
				"Limetray Tweet Graph");
		return intent;
	}



	/**
	 * Builds an XY multiple dataset using the provided values.
	 * 
	 * @param titles the series titles
	 * @param xValues the values for the X axis
	 * @param yValues the values for the Y axis
	 * @return the XY multiple dataset
	 */
	protected XYMultipleSeriesDataset buildDataset(String[] titles, List<int[]> xValues,
			List<int[]> yValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		addXYSeries(dataset, titles, xValues, yValues, 0);
		return dataset;
	}

	public void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles, List<int[]> xValues,
			List<int[]> yValues, int scale) {
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			XYSeries series = new XYSeries(titles[i], scale);
			int[] xV = xValues.get(i);
			int[] yV = yValues.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
				
			}
			dataset.addSeries(series);
		}
	}

	/**
	 * Builds an XY multiple series renderer.
	 * 
	 * @param colors the series rendering colors
	 * @param styles the series point styles
	 * @return the XY multiple series renderers
	 */
	protected XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		setRenderer(renderer, colors, styles);
		return renderer;
	}

	protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors, PointStyle[] styles) {
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setPointSize(5f);
		renderer.setMargins(new int[] { 20, 30, 15, 20 });
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
			renderer.addSeriesRenderer(r);
		}
	}

	/**
	 * Sets a few of the series renderer settings.
	 * 
	 * @param renderer the renderer to set the properties to
	 * @param title the chart title
	 * @param xTitle the title for the X axis
	 * @param yTitle the title for the Y axis
	 * @param xMin the minimum value on the X axis
	 * @param xMax the maximum value on the X axis
	 * @param yMin the minimum value on the Y axis
	 * @param yMax the maximum value on the Y axis
	 * @param axesColor the axes color
	 * @param labelsColor the labels color
	 */
	protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
			String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
	}
}
