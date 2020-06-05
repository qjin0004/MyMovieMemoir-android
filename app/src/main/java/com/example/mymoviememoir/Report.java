package com.example.mymoviememoir;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.example.mymoviememoir.entity.Person;
import com.example.mymoviememoir.networkconnection.NetworkConnection;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Report extends Fragment {
    private PieChart pieChart;
    private Person person;
    EditText startDate;
    EditText endDate;
    DatePickerDialog picker;
    NetworkConnection networkConnection;
    private BarChart barChart;
    Spinner yearSpinner;




    public Report(Person person){
        this.person = person;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        networkConnection = new NetworkConnection();
        View view = inflater.inflate(R.layout.report,container,false);
        startDate = view.findViewById(R.id.startDate);
        endDate = view.findViewById(R.id.endDate);



        startDate.setInputType(InputType.TYPE_NULL);
        startDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        startDate.setText( year + "-" + (month + 1) + "-" + dayOfMonth);
                    }
                },year,month,day);
                picker.show();
            }
        });

        endDate.setInputType(InputType.TYPE_NULL);
        endDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        endDate.setText( year + "-" + (month + 1) + "-" + dayOfMonth);
                    }
                },year,month,day);
                picker.show();
            }
        });

        pieChart = view.findViewById(R.id.pieChart);
        pieChart.getDescription().setEnabled(false);
        pieChart.setRotationEnabled(true); //可以转
        pieChart.setDrawHoleEnabled(false); //没中间的圈
        pieChart.setTransparentCircleAlpha(0); //两层圈
        pieChart.setUsePercentValues(true);  // set y in %
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);

        Button confirmButton = view.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String end = String.valueOf(endDate.getText());
                String start = String.valueOf(startDate.getText());
                DoPie doPie = new DoPie();
                doPie.execute(start,end);
            }
        });


        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
//                Log.d("test",e.toString());
            }

            @Override
            public void onNothingSelected() {

            }
        });


        //bar chart
        barChart = view.findViewById(R.id.barChart);
        yearSpinner = view.findViewById(R.id.yearSpinner);
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedYear = parent.getItemAtPosition(position).toString();
                if(selectedYear != null){
                    int year = Integer.parseInt(selectedYear);
                    createBarChart();
                    DoBar doBar = new DoBar();
                    doBar.execute(year);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }



    private void createBarChart(){
        YAxis yAxis;
        XAxis xAxis;
        Legend legend;
        LimitLine limitLine;
//        barChart.setBackground(Color.WHITE);


        Description description = new Description();
        description.setEnabled(false);
        barChart.setDescription(description);
        barChart.setDrawGridBackground(false); //不显示图表网格
        barChart.setDrawBarShadow(false); //背景阴影
        barChart.setDrawValueAboveBar(true);
        barChart.setHighlightFullBarEnabled(false);
        barChart.setBackgroundColor(Color.WHITE);
        barChart.setDrawBarShadow(false); //设置边框
        barChart.animateX(1000);
        barChart.animateY(1000);
        barChart.getAxisRight().setEnabled(false);
        barChart.setFitBars(true);
        //disable auto-zoom in/out
        barChart.setTouchEnabled(false);

        //xy轴设置
        xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setEnabled(true);
        xAxis.setDrawLabels(true);

        yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);

        legend = barChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(11f);

        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        legend.setDrawInside(false);

    }



    public void showBarChar(ArrayList<String> xData,ArrayList<Integer> yData, int color) {
        ArrayList<BarEntry> entries = new ArrayList<>();

        //给数据
        for (int i = 0; i < xData.size(); i++) {
            BarEntry barEntry = new BarEntry(Integer.parseInt(xData.get(i)),yData.get(i));
            entries.add(barEntry);
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Month");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextSize(15f);
//        initBarDataSet(barDataSet, color);

        BarData data = new BarData(barDataSet);

        barChart.setData(data);
        barChart.invalidate();

        // y轴取整

        XAxis xAxis = barChart.getXAxis();
        xAxis.setSpaceMin(data.getBarWidth() / 2f);
        xAxis.setSpaceMax(data.getBarWidth() / 2f);
    }


    private void initBarDataSet(BarDataSet barDataSet, int color){
        barDataSet.setColor(color);
        barDataSet.setFormLineWidth(1f);
        barDataSet.setFormSize(15.f);
        barDataSet.setDrawValues(false);
    }

    //pie chart
    private void addDataSet(ArrayList<String> postcode,ArrayList<Integer> time){
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

        for(int i = 0; i < time.size();i++){
            yEntrys.add(new PieEntry(time.get(i),postcode.get(i)));
        }

        for(int i = 1; i < postcode.size();i++){
            xEntrys.add(postcode.get(i));
        }

        PieDataSet pieDataSet = new PieDataSet(yEntrys,"Different postcode");
        pieDataSet.setSliceSpace(2); //块块之间的缝隙
        pieDataSet.setValueTextSize(12);
        pieDataSet.setColor(Color.BLACK);

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);


        pieDataSet.setColors(colors);
        Legend legend = pieChart.getLegend();
        legend.isWordWrapEnabled();
        legend.isEnabled();
        legend.setFormSize(20F);
        legend.setFormToTextSpace(0f);
        legend.setTextSize(15f);


//        data.setValueTextColor(Color.BLACK);

        pieDataSet.setValueLinePart1OffsetPercentage(90.f);
        pieDataSet.setValueLinePart1Length(1f);
        pieDataSet.setValueLinePart2Length(.2f);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(15f);
        pieDataSet.setValueFormatter(new PercentFormatter(pieChart));
//        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);


        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(15f);
        pieChart.setData(pieData);
        pieChart.invalidate();
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelColor(Color.BLACK);



    }

    private class DoPie extends AsyncTask<String,Void,HashMap<String,Integer>>{
        @Override
        protected HashMap<String,Integer> doInBackground(String...params){

            HashMap<String,Integer> result = new HashMap<>();
            try {
                result = networkConnection.getPersonEachPostcodeMemoirNumber(person.getPersonId(),params[0],params[1]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(HashMap<String,Integer> result){
            ArrayList<String> postcode = new ArrayList<>(); //x data
            ArrayList<Integer> time = new ArrayList<>();  // y data
            for(Map.Entry<String,Integer> entry:result.entrySet()){
                postcode.add(entry.getKey());
                time.add(entry.getValue());
            }

            addDataSet(postcode,time);
        }
    }



    //for bar chart
    private class DoBar extends AsyncTask<Integer,Void,HashMap<String,Integer>>{
        @Override
        protected HashMap<String,Integer> doInBackground(Integer...params){

            HashMap<String,Integer> result = networkConnection.getPersonEachMonthMemoirNumber(person.getPersonId(),params[0]);
            return result;
        }

        @Override
        protected void onPostExecute(HashMap<String,Integer> result){
            ArrayList<String> month = new ArrayList<>(); //x data
            ArrayList<Integer> time = new ArrayList<>();  // y data
            for(Map.Entry<String,Integer> entry:result.entrySet()){
                month.add(entry.getKey());
                time.add(entry.getValue());
            }

            int maxValue = 0;
            for(Integer tt: time){
                if (maxValue < tt){
                    maxValue = tt;
                }
            }

            YAxis leftAxis = barChart.getAxisLeft();
            leftAxis.setAxisMaximum(maxValue);

//            int maxValueMod = (maxValue + 2) % 6; //calc mod, here 2 is to display Legend and 6 is no of labels
//            maxValue = maxValue + (6-maxValueMod); // calculate final maxValue to set in barchart
//            barChart.getAxisLeft().setAxisMaximum(maxValue+2);



            showBarChar(month,time,Color.BLUE);
//            addDataSet(postcode,time);
        }
    }



}
