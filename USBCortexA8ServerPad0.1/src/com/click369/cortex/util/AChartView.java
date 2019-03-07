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
        //������������������ϵ����е㣬��һ����ļ��ϣ�������Щ�㻭������
        series = new XYSeries(title);
        //����һ�����ݼ���ʵ����������ݼ�������������ͼ��
        mDataset = new XYMultipleSeriesDataset();
        //���㼯��ӵ�������ݼ���
        mDataset.addSeries(series);
        //���¶������ߵ���ʽ�����Եȵȵ����ã�renderer�൱��һ��������ͼ������Ⱦ�ľ��
        int color = Color.GREEN;
        PointStyle style = PointStyle.POINT;
        renderer = buildRenderer(color, style, true);
        renderer.setPanEnabled(true, false);
        //���ú�ͼ�����ʽ
        setChartSettings(renderer, "X", "Y", xmin, xmax, ymin, ymax, Color.WHITE, Color.WHITE);
//        setChartSettings(renderer, "X", "Y", 0, 60, -10, 50, Color.WHITE, Color.WHITE);
        //����ͼ��
		chart = ChartFactory.getLineChartView(context, mDataset, renderer);
        //��ͼ����ӵ�������ȥ
		ll.addView(chart, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }
    
    
    protected XYMultipleSeriesRenderer buildRenderer(int color, PointStyle style, boolean fill) {
    	XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
    	//����ͼ�������߱������ʽ��������ɫ����Ĵ�С�Լ��ߵĴ�ϸ��
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
    	//�йض�ͼ�����Ⱦ�ɲο�api�ĵ�
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
    	
    	//���ú���һ����Ҫ���ӵĽڵ�
    	addX = 0;
//    	addY = (int)((Math.random() * 100)%50);  
		addY = addData;
		//�Ƴ����ݼ��оɵĵ㼯
		mDataset.removeSeries(series);
		
		//�жϵ�ǰ�㼯�е����ж��ٵ㣬��Ϊ��Ļ�ܹ�ֻ������100�������Ե���������100ʱ��������Զ��100
		int length = series.getItemCount();
		if (length > 300) {
			length = 300;
		}
		
		//���ɵĵ㼯��x��y����ֵȡ��������backup�У����ҽ�x��ֵ��1�������������ƽ�Ƶ�Ч��
		for (int i = 0; i < length; i++) {
			xv[i] =  series.getX(i) + 1;
			yv[i] =  series.getY(i);
		}
		//�㼯����գ�Ϊ�������µĵ㼯��׼��
		series.clear();
		
		//���²����ĵ����ȼ��뵽�㼯�У�Ȼ����ѭ�����н�����任���һϵ�е㶼���¼��뵽�㼯��
		//�����������һ�°�˳��ߵ�������ʲôЧ������������ѭ���壬������²����ĵ�
		series.add(addX, addY);
		for (int k = 0; k < length; k++) {
    		series.add(xv[k], yv[k]);
    	}
		//�����ݼ�������µĵ㼯
		mDataset.addSeries(series);
		//��ͼ���£�û����һ�������߲�����ֶ�̬
		//����ڷ�UI���߳��У���Ҫ����postInvalidate()������ο�api
		chart.invalidate();
    }
    
}