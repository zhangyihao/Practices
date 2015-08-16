package com.zhangyihao.mpandroidchartdemo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.PercentFormatter;

public class MainActivity extends Activity {

	private PieChart mPieChart;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}
	
	private void initView() {
		mPieChart = (PieChart)findViewById(R.id.activity_main_pieChart);
		
		PieData mPieData = getPieData(4, 100); 
		showChart(mPieData);
		
	}
	
	private void showChart(PieData pieData) {
		mPieChart.setHoleColorTransparent(true);
		mPieChart.setHoleRadius(60f); //设置中间圆形半径，如果设置为0，则为实心饼图
		mPieChart.setDrawHoleEnabled(true); //false则表示设置为实心饼图
		mPieChart.setTransparentCircleRadius(64f); //设置半透明圈半径
		//如果不设置，默认显示Description
		mPieChart.setDescription("");
		mPieChart.setDescriptionColor(Color.RED);
		
		mPieChart.setDrawCenterText(true); //饼图中间是否可以添加文字
//		mPieChart.setRotationAngle(90); //不知道这个角度有什么作用
		mPieChart.setRotationEnabled(true); //设置是否可以手动旋转
		mPieChart.setUsePercentValues(false); //显示成百分比
		mPieChart.setCenterText("盘点状态Text");
		mPieChart.setData(pieData);
		Legend legend =mPieChart.getLegend(); //设置比例图 
		legend.setEnabled(true); //设置是否显示比例图，默认为显示
		legend.setPosition(LegendPosition.RIGHT_OF_CHART);
		legend.setForm(LegendForm.CIRCLE);
		legend.setXEntrySpace(10f);    
		legend.setYEntrySpace(5f);    
            	
        mPieChart.animateXY(1000, 1000);  //设置动画 
		
	}
	
	private PieData getPieData(int count, int range) {
		//xValues表示每个饼块的内容
		List<String> xValues = new ArrayList<String>();
		xValues.add("未盘点");
		xValues.add("盘盈");
		xValues.add("盘亏");
		xValues.add("无盈亏");
		
		//yValues表示每个饼块的实际数据，下面设置的为比例
		List<Entry> yValues = new ArrayList<Entry>();
		yValues.add(new Entry(1, 0));
		yValues.add(new Entry(5, 1));
		yValues.add(new Entry(60, 2));
		yValues.add(new Entry(34, 3));

		PieDataSet pieDataSet = new PieDataSet(yValues, "盘点结果");
		//设置每个饼块间距
		pieDataSet.setSliceSpace(0f);
		
		ArrayList<Integer> colors = new ArrayList<Integer>();
		// 饼图颜色
		colors.add(Color.rgb(207, 248, 246));
		colors.add(Color.rgb(148, 212, 212));
		colors.add(Color.rgb(136, 180, 187));
		colors.add(Color.rgb(118, 174, 175));
		
		pieDataSet.setColors(colors);
		
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float px = 5 * (metrics.densityDpi / 160f);    
	    pieDataSet.setSelectionShift(px); // 选中态多出的长度
	    
	    PieData pieData = new PieData(xValues, pieDataSet);
	    pieData.setValueFormatter(new PercentFormatter());
	    pieData.setValueTextColor(Color.WHITE);
	    return pieData;
	}

}
