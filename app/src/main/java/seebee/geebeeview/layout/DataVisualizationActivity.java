package seebee.geebeeview.layout;

import android.app.DialogFragment;
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
import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
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
import java.util.Comparator;

import seebee.geebeeview.R;
import seebee.geebeeview.database.DatabaseAdapter;
import seebee.geebeeview.model.account.Dataset;
import seebee.geebeeview.model.adapter.FilterAdapter;
import seebee.geebeeview.model.adapter.TextHolderAdapter;
import seebee.geebeeview.model.consultation.School;
import seebee.geebeeview.model.monitoring.PatientRecord;
import seebee.geebeeview.model.monitoring.Record;
import seebee.geebeeview.model.monitoring.ValueCounter;


public class DataVisualizationActivity extends AppCompatActivity
        implements AddFilterDialogFragment.AddFilterDialogListener,
        AddDatasetDialogFragment.AddDatasetDialogListener, FilterAdapter.FilterAdapterListener {
    private static final String TAG = "DataVisualActivity";

    ArrayList<String> datasetList, filterList;
    TextHolderAdapter datasetAdapter;
    FilterAdapter filterAdapter;
    RecyclerView rvDataset, rvFilter;
    Button btnAddDataset, btnAddFilter, btnViewPatientList, btnViewHPIList, btnBack;
    RelativeLayout graphLayout; /* space where graph will be set on */

    int schoolID;
    String schoolName, date;
    PieChart pieChart;
    BarChart barChart;
    ScatterChart scatterChart;
    BubbleChart bubbleChart;
    ArrayList<PatientRecord> allRecords;
    ArrayList<PatientRecord> filteredRecords;
    String[] xData;
    int[] yData;
    ArrayList<Dataset> datasets;
    private ValueCounter valueCounter;
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
        btnViewHPIList = (Button) findViewById(R.id.btn_view_hpi_list);
        btnBack = (Button) findViewById(R.id.btn_dv_back);
        rvDataset = (RecyclerView) findViewById(R.id.rv_dv_dataset);
        rvFilter = (RecyclerView) findViewById(R.id.rv_dv_filter);
        graphLayout = (RelativeLayout) findViewById(R.id.graph_container);
        spRecordColumn = (Spinner) findViewById(R.id.sp_record_column);
        spChartType = (Spinner) findViewById(R.id.sp_chart_type);

        /* set listener for button view hpi list */
        btnViewHPIList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ViewHPIListActivity.class);
                intent.putExtra(School.C_SCHOOL_ID, schoolID);
                intent.putExtra(School.C_SCHOOLNAME, schoolName);
                startActivity(intent);
            }
        });

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

        /* set listener for back button */
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /* ready recycler view list for dataset */
        datasetList = new ArrayList<>();
        datasetAdapter = new TextHolderAdapter(datasetList);
        RecyclerView.LayoutManager dLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvDataset.setLayoutManager(dLayoutManager);
        rvDataset.setItemAnimator(new DefaultItemAnimator());
        rvDataset.setAdapter(datasetAdapter);

        allRecords = new ArrayList<>();
        /* initialized the fitlered list */
        filteredRecords = new ArrayList<>();
        getDatasetList();
        addDatasetToList(schoolName, date);

        /* ready recycler view list for filters */
        filterList = new ArrayList<>();
        filterAdapter = new FilterAdapter(filterList, this);
        RecyclerView.LayoutManager fLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvFilter.setLayoutManager(fLayoutManager);
        rvFilter.setItemAnimator(new DefaultItemAnimator());
        rvFilter.setAdapter(filterAdapter);

        prepareFilterList(null);

        btnAddFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment addFilterDialog = new AddFilterDialogFragment();
                addFilterDialog.show(getFragmentManager(), AddFilterDialogFragment.TAG);
            }
        });

        btnAddDataset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddDatasetDialogFragment addDatasetDialog = new AddDatasetDialogFragment();
                addDatasetDialog.setDatasetList(datasets);
                addDatasetDialog.show(getFragmentManager(), AddDatasetDialogFragment.TAG);
            }
        });

        spRecordColumn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                recordColumn = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(),
                        "OnItemSelectedListener : " + recordColumn,
                        Toast.LENGTH_SHORT).show();
                refreshCharts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                recordColumn = "BMI";
            }
        });

        /* prepare record so that it can be plotted immediately */
        prepareChartData();
        createCharts();

        spChartType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chartType = parent.getItemAtPosition(position).toString();
                ViewGroup.LayoutParams params;
                graphLayout.removeAllViews();
                if(position == 0){
                    graphLayout.addView(pieChart);
                    // adjust size of layout
                    params = pieChart.getLayoutParams();
                } else if(position == 1) {
                    /* add bar chart to layout */
                    graphLayout.addView(barChart);
                    /* adjust the size of the bar chart */
                    params = barChart.getLayoutParams();
                } else if (position == 2) {
                    graphLayout.addView(scatterChart);
                    /* adjust the size of the bar chart */
                    params = scatterChart.getLayoutParams();
                } else {
                    graphLayout.addView(bubbleChart);
                    /* adjust the size of the bar chart */
                    params = bubbleChart.getLayoutParams();
                }
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                addDataSet();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                /* Default chart is pie chart */
                chartType = "Pie Chart";
                graphLayout.addView(pieChart);
                // adjust size of layout
                ViewGroup.LayoutParams params = pieChart.getLayoutParams();
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        });
    }

    private void refreshCharts() {
        /* change the contents of the chart */
        if(pieChart != null || barChart != null) {
            prepareChartData();
            if(chartType.contentEquals("Pie Chart")) {
                pieChart.clear();
            } else if(chartType.contentEquals("Bar Chart")) {
                barChart.clear();
            } else if (chartType.contentEquals("Scatter Chart")) {
                scatterChart.clear();
            } else {
                bubbleChart.clear();
            }
            addDataSet();
        }
    }

    private void createCharts() {
        graphLayout.setBackgroundColor(Color.LTGRAY);
        createPieChart();
        createBarChart();
        createScatterChart();
        createBubbleChart();
    }

    /* change the contents of xData and yData */
    private void prepareChartData() {
        valueCounter = new ValueCounter(filteredRecords);
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
                        xData[entry.getXIndex()] + " = " + entry.getVal() + " children",
                        Toast.LENGTH_SHORT).show();
                Log.v(TAG, xData[entry.getXIndex()] + " = " + entry.getVal() + " children");

                if(!recordColumn.contentEquals("BMI")) {
                    Intent intent = new Intent(getBaseContext(), ViewPatientListActivity.class);
                    intent.putExtra(School.C_SCHOOL_ID, schoolID);
                    intent.putExtra(School.C_SCHOOLNAME, schoolName);
                    intent.putExtra(Record.C_DATE_CREATED, date);
                    intent.putExtra("column", ValueCounter.convertRecordColumn(recordColumn));
                    intent.putExtra("value", xData[entry.getXIndex()]);
                    startActivity(intent);
                }

            }

            @Override
            public void onNothingSelected() {

            }
        };
    }

    private void createBubbleChart() {
        bubbleChart = new BubbleChart(this);

        bubbleChart.setOnChartValueSelectedListener(getOnChartValueSelectedListener());
    }

    private void createScatterChart() {
        scatterChart = new ScatterChart(this);

        scatterChart.setOnChartValueSelectedListener(getOnChartValueSelectedListener());
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

        // configure pie chart
        pieChart.setUsePercentValues(true);
        pieChart.setDescriptionTextSize(R.dimen.title_text_size);

        // enable hole and configure
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.LTGRAY);
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

    private void prepareFilterList(String filter) {
        /* specify the filters used for this visualization */
        if(filter == null) {
            filterList.add("N/A");
        } else {
            filterList.remove("N/A");
            //filterList.clear();
            filterList.add(filter);
        }
        filterAdapter.notifyDataSetChanged();
    }

    private void addDatasetToList(String schoolName, String date) {
        /* specify the school and date from which the visualization data comes from */
        String dataset = schoolName+"("+date+")";
        if(!datasetList.contains(dataset)) {
            datasetList.add(dataset);
        }

        Log.v(TAG, "number of datasets: " + datasetList.size());
        datasetAdapter.notifyDataSetChanged();
        /* get records of patients taken in specified school and date from database */
        prepareRecord();
        refreshCharts();
    }

    private void prepareRecord(/*String schoolName, String date*/){
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "number of records (before): "+allRecords.size());
        /* get datasetList from database */
        allRecords.clear();
        //Log.d(TAG, "number of dataset = "+datasetList.size());
        for(int i = 0; i < datasetList.size(); i++) {
            String dataset = datasetList.get(i);
            String school = dataset.substring(0, dataset.indexOf("("));
            String date = dataset.substring(dataset.indexOf("(")+1,dataset.indexOf(")"));
            //Log.d(TAG, "dataset to be added: "+dataset);
            int schoolId = getSchoolId(school, date);
            //Log.d(TAG, "schoolId = "+schoolId);
            if(schoolId != -1) {
                allRecords.addAll(getBetterDb.getRecordsFromSchool(schoolId, date));
                Log.d(TAG, "added dataset: "+dataset);
            }
        }

        /* close database after insert */
        getBetterDb.closeDatabase();
        Log.v(TAG, "number of records (after): " + allRecords.size());
        filteredRecords.clear();
        filteredRecords.addAll(allRecords);
    }

    private int getSchoolId(String schoolName, String date) {
        //Log.d(TAG, "schoolName: "+schoolName);
        //Log.d(TAG, "date: "+date);
        for(int i = 0; i < datasets.size(); i++) {
            if(datasets.get(i).getSchoolName().contentEquals(schoolName)
                    && datasets.get(i).getDate().contentEquals(date)) {
                return datasets.get(i).getSchoolID();
            }
        }
        return -1;
    }

    private void getDatasetList() {
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get datasetList from database */
        datasets = getBetterDb.getAllDatasets();
        /* close database after insert */
        getBetterDb.closeDatabase();
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
        } else if(chartType.contentEquals("Scatter Chart")) {
            prepareScatterChartData(getColorPalette());
        } else {
            prepareBubbleChartData(getColorPalette());
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

    private void prepareBubbleChartData(ArrayList<Integer> colors) {
        ArrayList<BubbleEntry> yVals1 = new ArrayList<>();
        String year = date.substring(date.length() - 4);
        for(int i = 0; i < yData.length; i++) {
            /* BubbleEntry(xpos, ypos, size)  */
            yVals1.add(new BubbleEntry(i, Integer.valueOf(year), yData[i]));
        }

        ArrayList<String> labels = createLabels();

        BubbleDataSet bubbleDataSet = new BubbleDataSet(yVals1, "");
        bubbleDataSet.setColor(colors.get(0));
        BubbleData bubbleData = new BubbleData(labels, bubbleDataSet);
        bubbleChart.setData(bubbleData);
        bubbleChart.setDescription(recordColumn);
    }

    private void prepareScatterChartData(ArrayList<Integer> colors) {
        ArrayList<Entry> yVals1 = createEntries();
        ArrayList<String> labels = createLabels();

        ScatterDataSet scatterDataSet = new ScatterDataSet(yVals1, "");
        /* set the shape of drawn scatter point. */
        scatterDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        scatterDataSet.setColor(colors.get(0));
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
        barDataSet.setColor(colors.get(0));
        /*BarDataSet barDataSet1 = new BarDataSet(yVals1, "");
        barDataSet.setColor(colors.get(0));
        List<IBarDataSet> barDataSetList = new ArrayList<>();
        barDataSetList.add(barDataSet);
        barDataSetList.add(barDataSet1);aa
        BarData barData = new BarData(xVals, barDataSetList);*/
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

    @Override
    public void onDialogPositiveClick(AddFilterDialogFragment dialog) {
        String ageEquator, ageValue, genderValue;
        ageEquator = dialog.getAgeEquator();
        ageValue = dialog.getAgeValue();
        genderValue = dialog.getGenderValue();

        Log.d(AddFilterDialogFragment.TAG, "Filter: age "+ageEquator+" "+ageValue);
        /* filter records*/
        if(!ageValue.contentEquals("")) {
            for(int i = 0; i < filterList.size(); i++) {
                if(filterList.get(i).contains("age")){
                    removeFilters(filterList.get(i));
                }
            }
            filterRecordsByAge(ageEquator, ageValue);
            prepareFilterList("age "+ageEquator+" "+ageValue);
        }
        if(!genderValue.contentEquals("N/A")) {
            for(int i = 0; i < filterList.size(); i++) {
                if(filterList.get(i).contains("gender")){
                    removeFilters(filterList.get(i));
                }
            }
            filterRecordsByGender(genderValue);
            prepareFilterList("gender = "+genderValue);
        }
        refreshCharts();
    }

    private void filterRecordsByGender(String genderValue) {
        Log.d(TAG, "Gender Filter: "+genderValue);
        for(int i = 0; i < filteredRecords.size(); i ++) {
            if(genderValue.contentEquals("Female")) {
                if(!filteredRecords.get(i).getGender()) {
                    filteredRecords.remove(i);
                    i--;
                }
            } else if(genderValue.contentEquals("Male")) {
                if(filteredRecords.get(i).getGender()){
                    filteredRecords.remove(i);
                    i--;
                }
            }
        }
//        for(int i = 0; i < filteredRecords.size(); i++) {
//            Log.d(TAG, "Filtered Gender = "+filteredRecords.get(i).getGender());
//        }
    }

    private void filterRecordsByAge(String filterEquator, String filterValue) {
        // sort list according to age, ascending order
        Collections.sort(filteredRecords, new Comparator<PatientRecord>() {
            @Override
            public int compare(PatientRecord o1, PatientRecord o2) {
                if(o1.getAge() > o2.getAge()) {
                    return 1;
                } else if(o1.getAge() < o2.getAge()) {
                    return -1;
                }
                return 0;
            }
        });
        int value = Integer.valueOf(filterValue);
        int index = getIndexByProperty(value);
        Log.d(TAG, "Index: "+ index);
        ArrayList<PatientRecord> tempArray = new ArrayList<>();
        if(index != -1) {
            if(filterEquator.contains("=")) {
                int endIndex = getIndexByProperty(value+1);
                if(endIndex == -1) {
                    tempArray.addAll(filteredRecords.subList(index, filteredRecords.size()-1));
                } else {
                    tempArray.addAll(filteredRecords.subList(index, endIndex));
                }
            }
            if(filterEquator.contains("<")) {
                tempArray.addAll(filteredRecords.subList(0, index));
            } else if(filterEquator.contains(">")) {
                tempArray.addAll(filteredRecords.subList(index, filteredRecords.size() - 1));
            }
            filteredRecords.clear();
            filteredRecords.addAll(tempArray);
//            for(int i = 0; i < filteredRecords.size(); i++) {
//                Log.d(TAG, "Age: "+ filteredRecords.get(i).getAge());
//            }
        } else {
            Toast.makeText(this, "There is no one with that age!", Toast.LENGTH_SHORT).show();
        }
//        prepareFilterList("age "+filterEquator+" "+value);
//        refreshCharts();
    }
    /* Get index of the first record with the specified age value*/
    private int getIndexByProperty(int value) {
        for(int i = 0; i < filteredRecords.size(); i++) {
            if(filteredRecords.get(i).getAge() == value) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void removeFilters(String filter) {
        filteredRecords.clear();
        filteredRecords.addAll(allRecords);
        Log.d(TAG, "Removed Filter: "+filter);
        filterList.remove(filter);

        if(filterList.size() > 0) {
            String filterLeft = filterList.get(0);
            Log.d(TAG, "Filter Left: "+filterLeft);
            if(filterLeft.contains("age")) {
                filterRecordsByAge(String.valueOf(filterLeft.charAt(4)), filterLeft.substring(6));
            } else if(filterLeft.contains("gender")) {
                filterRecordsByGender(filterLeft.substring(9));
            }
//        } else {
//            Log.d(TAG, "Removed All filters");
////            filteredRecords.clear();
////            filteredRecords.addAll(allRecords);
//
        }
        Log.d(TAG, "Displayed records: "+filteredRecords.size());
//        if(filter.contains("age")) {
//            if(filterList.get(0).contains("gender")) {
//                filterRecordsByGender(filterList.get(0).substring(7));
//            }
//        } else if(filter.contains("gender")) {
//            filterRecordsByAge(String.valueOf(filter.charAt(4)), filter.substring(6));
//        }

        refreshCharts();
    }

    @Override
    public void onDialogPositiveClick(AddDatasetDialogFragment dialog) {
        int selectedDatasetIndex = dialog.getSelectedDatasetIndex();
        Dataset dataset = datasets.get(selectedDatasetIndex);
        //dataset.printDataset();
        addDatasetToList(dataset.getSchoolName(), dataset.getDate());
    }
}
