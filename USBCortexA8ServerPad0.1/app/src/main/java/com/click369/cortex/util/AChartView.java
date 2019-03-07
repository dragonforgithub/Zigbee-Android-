package com.click369.cortex.util;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

@SuppressLint("HandlerLeak")
public class AChartView {

	//	private Timer timer = new Timer();
	private String title,xLabel,yLabel;
	private XYSeries series;
	private XYMultipleSeriesDataset mDataset;
	private GraphicalView chart;
	private XYMultipleSeriesRenderer renderer;
	private int addX = -1;
	double addY;

	double[] xv = new double[300];
	double[] yv = new double[300];

	public AChartView(Context context,LinearLayout ll,String title,String xLabel,String yLabel,int xmin,int xmax,int ymin,int ymax) {
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		this.title= title;
		//这个类用来放置曲线上的所有点，是一个点的集合，根据这些点画出曲线
		series = new XYSeries(title);
		//创建一个数据集的实例，这个数据集将被用来创建图表
		mDataset = new XYMultipleSeriesDataset();
		//将点集添加到这个数据集中
		mDataset.addSeries(series);
		//以下都是曲线的样式和属性等等的设置，renderer相当于一个用来给图表做渲染的句柄
		int color = Color.GREEN;
		PointStyle style = PointStyle.POINT;
		renderer = buildRenderer(color, style, true);
		renderer.setPanEnabled(true, false);
		//设置好图表的样式
		setChartSettings(renderer, "X", "Y", xmin, xmax, ymin, ymax, Color.WHITE, Color.WHITE);
//        setChartSettings(renderer, "X", "Y", 0, 60, -10, 50, Color.WHITE, Color.WHITE);
		//生成图表
		chart = ChartFactory.getLineChartView(context, mDataset, renderer);
		//将图表添加到布局中去
		ll.addView(chart, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}


	protected XYMultipleSeriesRenderer buildRenderer(int color, PointStyle style, boolean fill) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		//设置图表中曲线本身的样式，包括颜色、点的大小以及线的粗细等
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(Color.WHITE);
		r.setPointStyle(style);
		r.setFillPoints(fill);
		r.setLineWidth(3);

		renderer.addSeriesRenderer(r);

		return renderer;
	}

	protected void setChartSettings(XYMultipleSeriesRenderer renderer, String xTitle, String yTitle,
									double xMin, double xMax, double yMin, double yMax, int axesColor, int labelsColor) {
		//有关对图表的渲染可参看api文档
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
		renderer.setShowGrid(true);
		renderer.setGridColor(Color.GREEN);
		renderer.setXLabels(20);
		renderer.setYLabels(10);
		renderer.setXTitle(xLabel);
		renderer.setYTitle(yLabel);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setPointSize((float) 3 );
		renderer.setShowLegend(false);
	}

	public void updateChart(double addData) {

		//设置好下一个需要增加的节点
		addX = 0;
//    	addY = (int)((Math.random() * 100)%50);
		addY = addData;
		//移除数据集中旧的点集
		mDataset.removeSeries(series);

		//判断当前点集中到底有多少点，因为屏幕总共只能容纳100个，所以当点数超过100时，长度永远是100
		int length = series.getItemCount();
		if (length > 300) {
			length = 300;
		}

		//将旧的点集中x和y的数值取出来放入backup中，并且将x的值加1，造成曲线向右平移的效果
		for (int i = 0; i < length; i++) {
			xv[i] =  series.getX(i) + 1;
			yv[i] =  series.getY(i);
		}
		//点集先清空，为了做成新的点集而准备
		series.clear();

		//将新产生的点首先加入到点集中，然后在循环体中将坐标变换后的一系列点都重新加入到点集中
		//这里可以试验一下把顺序颠倒过来是什么效果，即先运行循环体，再添加新产生的点
		series.add(addX, addY);
		for (int k = 0; k < length; k++) {
			series.add(xv[k], yv[k]);
		}
		//在数据集中添加新的点集
		mDataset.addSeries(series);
		//视图更新，没有这一步，曲线不会呈现动态
		//如果在非UI主线程中，需要调用postInvalidate()，具体参考api
		chart.invalidate();
	}

}