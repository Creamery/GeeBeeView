package seebee.geebeeview.layout;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import seebee.geebeeview.R;
import seebee.geebeeview.database.DatabaseAdapter;
import seebee.geebeeview.model.adapter.TextHolderAdapter;
import seebee.geebeeview.model.consultation.School;
import seebee.geebeeview.model.monitoring.PatientRecord;
import seebee.geebeeview.model.monitoring.Record;
import seebee.geebeeview.model.monitoring.ValueCounter;


public class DataVisualizationActivity extends AppCompatActivity {
    private static final String TAG = "DataVisualActivity";

    ArrayList<String> datasetList, filterList;
    TextHolderAdapter datasetAdapter, filterAdapter;
    RecyclerView rvDataset, rvFilter;
    Button btnAddDataset, btnAddFilter, btnViewPatientList;
    RelativeLayout graphLayout; /* space where graph will be set on */

    int schoolID;
    String schoolName, date;
    private ValueCounter valueCounter;

    PieChart pieChart;
    BarChart barChart;
    ScatterChart scatterChart;

    ArrayList<PatientRecord> records;

    String[] xData;
    int[] yData;

    private Spinner spRecordColumn, spChartType;
    private String recordColumn = "BMI";
    private String chartType = "Pie Chart";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_visualization);
        // lock orientation of activity to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // get extras (schoolName & date)
        schoolName = getIntent().getStringExtra(School.C_SCHOOLNAME);
        schoolID = getIntent().getIntExtra(School.C_SCHOOL_ID, 0);
        date = getIntent().getStringExtra(Record.C_DATE_CREATED);

        btnAddDataset = (Button) findViewById(R.id.btn_add_dataset);
        btnAddFilter = (Button) findViewById(R.id.btn_add_filter);
        btnViewPatientList = (Button) findViewById(R.id.btn_view_patient_list);
        rvDataset = (RecyclerView) findViewById(R.id.rv_dv_dataset);
        rvFilter = (RecyclerView) findViewById(R.id.rv_dv_filter);
        graphLayout = (RelativeLayout) findViewById(R.id.graph_container);
        spRecordColumn = (Spinner) findViewById(R.id.sp_record_column);
        spChartType = (Spinner) findViewById(R.id.sp_chart_type);

        /* set button for view patient list */
        btnViewPatientList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ViewPatientListActivity.class);
                intent.putExtra(School.C_SCHOOL_ID, schoolID);
                intent.putExtra(School.C_SCHOOLNAME, schoolName);
                intent.putExtra(Record.C_DATE_CREATED, date);
                startActivity(intent);
                //Log.v(TAG, "started ViewPatientListActivity");
            }
        });

        /* ready recycler view list for dataset */
        datasetList = new ArrayList<>();
        datasetAdapter = new TextHolderAdapter(datasetList);
        RecyclerView.LayoutManager dLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvDataset.setLayoutManager(dLayoutManager);
        rvDataset.setItemAnimator(new DefaultItemAnimator());
        rvDataset.setAdapter(datasetAdapter);

        String temp = schoolName+"("+date+")";
        datasetList.add(temp);
        prepareDatasetList();

        /* ready recycler view list for filters */
        filterList = new ArrayList<>();
        filterAdapter = new TextHolderAdapter(filterList);
        RecyclerView.LayoutManager fLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvFilter.setLayoutManager(fLayoutManager);
        rvFilter.setItemAnimator(new DefaultItemAnimator());
        rvFilter.setAdapter(filterAdapter);

        prepareFilterList();

        spRecordColumn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                recordColumn = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(),
                        "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();
                /* change the contents of the chart */
                if(pieChart != null || barChart != null) {
                    prepareChartData();
                    if(chartType.contentEquals("Pie Chart")) {
                        barChart.clear();
                        addDataSet();
                    } else if(chartType.contentEquals("Bar Chart")) {
                        pieChart.clear();
                        addDataSet();
                    } else {
                        scatterChart.clear();
                        addDataSet();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                recordColumn = "BMI";
            }
        });

        /* get records of patients taken in specified school and date from database */
        prepareRecord(schoolName, date);
        /* prepare record so that it can be plotted immediately */
        valueCounter = new ValueCounter(records);
        prepareChartData();
        createCharts();

        spChartType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chartType = parent.getItemAtPosition(position).toString();
                ViewGroup.LayoutParams params;
                if(position == 0){
                    graphLayout.removeAllViews();
                    graphLayout.addView(pieChart);
                    // adjust size of layout
                    params = pieChart.getLayoutParams();
                } else if(position == 1) {
                    graphLayout.removeAllViews();
                    /* add bar chart to layout */
                    graphLayout.addView(barChart);
                    /* adjust the size of the bar chart */
                    params = barChart.getLayoutParams();
                } else {
                    graphLayout.removeAllViews();
                    graphLayout.addView(scatterChart);
                    /* adjust the size of the bar chart */
                    params = scatterChart.getLayoutParams();
                }
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                addDataSet();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                chartType = "Pie Chart";
                graphLayout.addView(pieChart);
                // adjust size of layout
                ViewGroup.LayoutParams params = pieChart.getLayoutParams();
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        });
    }

    private void createCharts() {
        createPieChart();
        createBarChart();
        createScatterChart();
    }

    /* change the contents of xData and yData */
    private void prepareChartData() {
        switch (recordColumn) {
            default:
            case "BMI":
                xData = valueCounter.getLblBMI();
                yData = valueCounter.getValBMI();
                break;
            case "Visual Acuity Left":
                xData = valueCounter.getLblVisualAcuity();
                yData = valueCounter.getValVisualAcuityLeft();
                break;
            case "Visual Acuity Right":
                xData = valueCounter.getLblVisualAcuity();
                yData = valueCounter.getValVisualAcuityRight();
                break;
            case "Color Vision":
                xData = valueCounter.getLblColorVision();
                yData = valueCounter.getValColorVision();
                break;
            case "Hearing Left":
                xData = valueCounter.getLblHearing();
                yData = valueCounter.getValHearingLeft();
                break;
            case "Hearing Right":
                xData = valueCounter.getLblHearing();
                yData = valueCounter.getValHearingRight();
                break;
            case "Gross Motor":
                xData = valueCounter.getLblGrossMotor();
                yData = valueCounter.getValGrossMotor();
                break;
            case "Fine Motor (Dominant Hand)":
                xData = valueCounter.getLblFineMotor();
                yData = valueCounter.getValFineMotorDom();
                break;
            case "Fine Motor (Non-Dominant Hand)":
                xData = valueCounter.getLblFineMotor();
                yData = valueCounter.getValFineMotorNonDom();
                break;
            case "Fine Motor (Hold)":
                xData = valueCounter.getLblFineMotorHold();
                yData = valueCounter.getValFineMotorHold();
                break;
        }
    }

    private OnChartValueSelectedListener getOnChartValueSelectedListener() {
        return new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                // display msg when value selected
                if(entry == null)
                    return;
                Toast.makeText(DataVisualizationActivity.this,
                        xData[entry.getXIndex()] + " = " + entry.getVal() + "%",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        };
    }

    private void createScatterChart() {
        scatterChart = new ScatterChart(this);
    }

    private void createBarChart() {
        /* create bar chart */
        barChart = new BarChart(this);

        // set a chart value selected listener
        barChart.setOnChartValueSelectedListener(getOnChartValueSelectedListener());
    }

    /* prepare values specifically for piechart only */
    private void createPieChart() {
        /* add pie chart */
        pieChart = new PieChart(this);

        graphLayout.setBackgroundColor(Color.LTGRAY);

        // configure pie chart
        pieChart.setUsePercentValues(true);
        pieChart.setDescriptionTextSize(R.dimen.title_text_size);

        // enable hole and configure
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.LTGRAY); //mChart.setHoleColorTransparent(true);
        pieChart.setHoleRadius(7);
        pieChart.setTransparentCircleRadius(100);

        // enable rotation of the chart by touch
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);

        // set a chart value selected listener
        pieChart.setOnChartValueSelectedListener(getOnChartValueSelectedListener());

        // add data
        addDataSet();

        // customize legends
        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setTextSize(R.dimen.context_text_size);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);

    }

    private void prepareFilterList() {
        /* specify the filters used for this visualization */
        filterList.add("N/A");
        filterAdapter.notifyDataSetChanged();
    }

    private void prepareDatasetList() {
        /* specify the school and date from which the visualization data comes from */

//        Log.v("ViewDatasetListActivity", "number of datasets = " + datasetList.size());
        datasetAdapter.notifyDataSetChanged();
    }

    private void prepareRecord(String schoolName, String date){
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get datasetList from database */
        records = getBetterDb.getRecordsFromSchool(schoolID, date);
        /* close database after insert */
        getBetterDb.closeDatabase();
        Log.v(TAG, "number of records = " + records.size());
    }

    private ArrayList<Integer> getColorPalette() {
        ArrayList<Integer> colors = new ArrayList<>();

        for(int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for(int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for(int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for(int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for(int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        return colors;
    }

    private void addDataSet() {
        if(chartType.contentEquals("Pie Chart")) {
            preparePieChartData(getColorPalette());
        } else if(chartType.contentEquals("Bar Chart")){
            prepareBarChartData(getColorPalette());
        } else {
            prepareScatterChartData(getColorPalette());
        }
    }

    private ArrayList<Entry> createEntries() {
        ArrayList<Entry> yVals1 = new ArrayList<>();

        for(int i = 0; i < yData.length; i++) {
            yVals1.add(new Entry(yData[i], i));
        }
        return yVals1;
    }

    private ArrayList<String> createLabels() {
        ArrayList<String> xVals = new ArrayList<>();

        Collections.addAll(xVals, xData);

        return xVals;
    }

    private void prepareScatterChartData(ArrayList<Integer> colors) {
        ArrayList<Entry> yVals1 = createEntries();
        ArrayList<String> labels = createLabels();

        ScatterDataSet scatterDataSet = new ScatterDataSet(yVals1, "");
        scatterDataSet.addColor(colors.get(0));
        ScatterData scatterData = new ScatterData(labels, scatterDataSet);
        scatterChart.setData(scatterData);
        scatterChart.setDescription(recordColumn);
    }

    private void prepareBarChartData(ArrayList<Integer> colors) {
        ArrayList<BarEntry> yVals1 = new ArrayList<>();

        for(int i = 0; i < yData.length; i++) {
            yVals1.add(new BarEntry(yData[i], i));
        }

        ArrayList<String> xVals = createLabels();

        /* create bar chart dataset */
        BarDataSet barDataSet = new BarDataSet(yVals1, "");
        barDataSet.addColor(colors.get(0));
        BarData barData = new BarData(xVals, barDataSet);
        barChart.setData(barData);
        barChart.setDescription(recordColumn);
    }

    private void preparePieChartData(ArrayList<Integer> colors) {
        ArrayList<Entry> yVals1 = createEntries();

        ArrayList<String> xVals = createLabels();

        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);
        /* add colors to chart */
        dataSet.setColors(colors);

        // instantiate pie data object now
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.GRAY);

        pieChart.setData(data);
        pieChart.setDescription(recordColumn);

        // undo all highlights
        pieChart.highlightValues(null);

        // update pie chart
        pieChart.invalidate();
    }
}
