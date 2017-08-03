package seebee.geebeeview.layout;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import seebee.geebeeview.R;
import seebee.geebeeview.database.DatabaseAdapter;
import seebee.geebeeview.model.consultation.Patient;
import seebee.geebeeview.model.monitoring.AgeCalculator;
import seebee.geebeeview.model.monitoring.BMICalculator;
import seebee.geebeeview.model.monitoring.LineChartValueFormatter;
import seebee.geebeeview.model.monitoring.Record;

public class ViewPatientActivity extends AppCompatActivity {

    private static final String TAG = "ViewPatientActivity";

    private TextView tvName, tvBirthday, tvGender, tvDominantHand, tvRecordDate, tvBMI, tvRemarks;
    private TextView tvHeight, tvWeight, tvVisualLeft, tvVisualRight, tvColorVision, tvHearingLeft,
            tvHearingRight, tvGrossMotor, tvFineMotorD, tvFineMotorND;
    private Button btnViewHPI;
    private Spinner spRecordDate;

    private int patientID;
    private Patient patient;
    private ArrayList<Record> patientRecords;

    private RelativeLayout graphLayout;
    private LineChart lineChart;
    private RadarChart radarChart;
    private Spinner spRecordColumn;
    private String recordColumn = "Height (in cm)";
    private String chartType = "Line Chart";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient);

        // lock orientation of activity to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        /* get patient ID */
        patientID = getIntent().getIntExtra(Patient.C_PATIENT_ID, 0);
        /* connect views in layout here */
        tvName = (TextView) findViewById(R.id.tv_name_r);
        tvBirthday = (TextView) findViewById(R.id.tv_birthday);
        tvGender = (TextView) findViewById(R.id.tv_gender);
        tvDominantHand = (TextView) findViewById(R.id.tv_dominant_hand);
        tvRecordDate = (TextView) findViewById(R.id.tv_record_date);
        tvBMI = (TextView) findViewById(R.id.tv_bmi);
        tvRemarks = (TextView) findViewById(R.id.tv_remarks);
        /* connect views in layout to show record of last check up */
        tvHeight = (TextView) findViewById(R.id.tv_height);
        tvWeight = (TextView) findViewById(R.id.tv_weight);
        tvVisualLeft = (TextView) findViewById(R.id.tv_visual_acuity_left);
        tvVisualRight = (TextView) findViewById(R.id.tv_visual_acuity_right);
        tvColorVision = (TextView) findViewById(R.id.tv_color_vision);
        tvHearingLeft = (TextView) findViewById(R.id.tv_hearing_left);
        tvHearingRight = (TextView) findViewById(R.id.tv_hearing_right);
        tvGrossMotor = (TextView) findViewById(R.id.tv_gross_motor);
        tvFineMotorD = (TextView) findViewById(R.id.tv_fine_motor_d);
        tvFineMotorND = (TextView) findViewById(R.id.tv_fine_motor_nd);
        /* connect graph container to here */
        graphLayout = (RelativeLayout) findViewById(R.id.patient_chart_container);
        /* connect spinner here */
        spRecordColumn = (Spinner) findViewById(R.id.sp_vp_record_column);
        spRecordDate = (Spinner) findViewById(R.id.sp_record_date);
        /* connect buttons */
        btnViewHPI = (Button) findViewById(R.id.btn_view_hpi);
        /* set button so that it will go to the ViewHPIListActivity */
        btnViewHPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ViewHPIActivity.class);
                intent.putExtra(Patient.C_PATIENT_ID, patient.getPatientID());
                startActivity(intent);
            }
        });

        patient = null;
        getPatientData();
        /* fill up the patient data */
        if(patient != null) {
            tvName.setText(patient.getLastName()+", "+patient.getFirstName());
            tvBirthday.setText(patient.getBirthday());
            tvGender.setText(patient.getGenderString());
            tvDominantHand.setText(patient.getHandednessString());
            tvRemarks.setText(patient.getRemarksString());
        }

        getPatientRecords();

        /* set up spinner selector */
        spRecordColumn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                recordColumn = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(),
                        "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();
                /* change the contents of the chart */
                if(lineChart != null) {
                    lineChart.clear();
                    prepareLineChartData();
                } else {
                    radarChart.clear();
                    prepareRadarChartData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        prepareRecordDateSpinner();

        /* show details in relation to latest check up record*/
        if(patientRecords.size() > 0) {
            spRecordDate.setSelection(patientRecords.size()-1);
            Record lastRecord = patientRecords.get(patientRecords.size()-1);
            tvRecordDate.setText(lastRecord.getDateCreated());
        }

        createCharts();
        addChartToView();
        prepareLineChart();
        prepareLineChartData();


    }

    private void displayRecord(Record record) {
        String recordDate = record.getDateCreated();

        boolean isGirl = true;
        if(patient.getGender() != 1) {
            isGirl = false;
        }
        String bmi = BMICalculator.getBMIResultString(isGirl,
                AgeCalculator.calculateAge(patient.getBirthday(), recordDate),
                BMICalculator.computeBMIMetric(Double.valueOf(record.getHeight()).intValue(),
                        Double.valueOf(record.getWeight()).intValue()));
        tvBMI.setText(bmi);
        tvHeight.setText(record.getHeight()+" cm");
        tvWeight.setText(record.getWeight()+" kg");
        tvVisualLeft.setText(record.getVisualAcuityLeft());
        tvVisualRight.setText(record.getVisualAcuityRight());
        tvColorVision.setText(record.getColorVision());
        tvHearingLeft.setText(record.getHearingLeft());
        tvHearingRight.setText(record.getHearingRight());
        tvGrossMotor.setText(record.getGrossMotorString());
        tvFineMotorD.setText(record.getFineMotorString(record.getFineMotorDominant()));
        tvFineMotorND.setText(record.getFineMotorString(record.getFineMotorNDominant()));
    }

    private void prepareRadarChartData() {
        RadarData radarData = new RadarData();
        ArrayList<Entry> entries;
        Record record = patientRecords.get(0);
        for(int i = 0; i < 5; i++) {
            //entries.add(new Entry());
        }
    }

    private void prepareLineChartData() {
        LineData lineData = new LineData();
        lineData.setValueTextColor(Color.WHITE);

        // add data to line chart
        lineChart.setData(lineData);
        LineDataSet dataset = (LineDataSet) lineData.getDataSetByIndex(0);
        if(dataset == null) {
            dataset = createLineDataSet();
            lineData.addDataSet(dataset);
        }

        Record record;
        float x; int age;
        for(int i = 0; i < patientRecords.size(); i++) {
            record = patientRecords.get(i);
            lineData.addXValue(record.getDateCreated());
            x = getColumnValue(record);
            Log.v(TAG, recordColumn+": "+x);
            lineData.addEntry(new Entry(x, dataset.getEntryCount()), 0);
        }
        setLineChartValueFormatter(lineData);

        // notify chart data has changed
        lineChart.notifyDataSetChanged();

    }

    private float getColumnValue(Record record) {
        float x;
        switch (recordColumn) {
            default:
            case "Height (in cm)": x = Double.valueOf(record.getHeight()).floatValue();
                break;
            case "Weight (in kg)": x = Double.valueOf(record.getWeight()).floatValue();
                break;
            case "BMI": x = BMICalculator.computeBMIMetric(
                    Double.valueOf(record.getHeight()).intValue(),
                    Double.valueOf(record.getWeight()).intValue());
                break;
            case "Visual Acuity Left":
                x = LineChartValueFormatter.ConvertVisualAcuity(record.getVisualAcuityLeft());
                break;
            case "Visual Acuity Right":
                x = LineChartValueFormatter.ConvertVisualAcuity(record.getVisualAcuityRight());
                break;
            case "Color Vision":
                x = LineChartValueFormatter.ConvertColorVision(record.getColorVision());
                break;
            case "Hearing Left":
                x = LineChartValueFormatter.ConvertHearing(record.getHearingLeft());
                break;
            case "Hearing Right":
                x = LineChartValueFormatter.ConvertHearing(record.getHearingRight());
                break;
            case "Gross Motor": x = record.getGrossMotor();
                break;
            case "Fine Motor (Dominant Hand)": x = record.getFineMotorDominant();
                break;
            case "Fine Motor (Non-Dominant Hand)": x = record.getFineMotorNDominant();
                break;
            case "Fine Motor (Hold)": x = record.getFineMotorHold();
                break;
        }
        return x;
    }

    private void setLineChartValueFormatter(LineData lineData) {
        if(recordColumn.contains("BMI")) {
            int age = AgeCalculator.calculateAge(patient.getBirthday(), patientRecords.get(patientRecords.size()-1).getDateCreated());
            //TODO: Make sure that bmi result (String) is right
            lineChart.getAxisRight().setValueFormatter(LineChartValueFormatter.getYAxisValueFormatterBMI(patient.isGirl(), age));
        } else if(recordColumn.contains("Visual Acuity")) {
            lineData.setValueFormatter(LineChartValueFormatter.getValueFormatterVisualAcuity());
            lineChart.getAxisRight().setValueFormatter(LineChartValueFormatter.getYAxisValueFormatterVisualAcuity());
        } else if(recordColumn.contains("Color Vision")) {
            lineData.setValueFormatter(LineChartValueFormatter.getValueFormatterColorVision());
            lineChart.getAxisRight().setValueFormatter(LineChartValueFormatter.getYAxisValueFormatterColorVision());
        } else if(recordColumn.contains("Hearing")) {
            lineData.setValueFormatter(LineChartValueFormatter.getValueFormatterHearing());
            lineChart.getAxisRight().setValueFormatter(LineChartValueFormatter.getYAxisValueFormatterHearing());
        } else if(recordColumn.contains("Motor")) {
            lineData.setValueFormatter(LineChartValueFormatter.getValueFormatterMotor());
            lineChart.getAxisRight().setValueFormatter(LineChartValueFormatter.getYAxisValueFormatterMotor());
        } else {
            lineData.setValueFormatter(lineChart.getDefaultValueFormatter());
            lineChart.getAxisRight().setValueFormatter(new YAxisValueFormatter() {
                @Override
                public String getFormattedValue(float v, YAxis yAxis) {
                    return Float.toString(v);
                }
            });
        }
    }

    private LineDataSet createLineDataSet() {
        LineDataSet lineDataset = new LineDataSet(null, recordColumn);
        lineDataset.setDrawCubic(true);
        lineDataset.setCubicIntensity(0.2f);
        lineDataset.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataset.setColor(ColorTemplate.getHoloBlue());
        lineDataset.setCircleColor(ColorTemplate.getHoloBlue());
        lineDataset.setLineWidth(2f);
        lineDataset.setFillAlpha(4);
        lineDataset.setFillColor(ColorTemplate.getHoloBlue());
        lineDataset.setHighLightColor(Color.rgb(244, 11, 11));
        lineDataset.setValueTextColor(Color.WHITE);
        lineDataset.setValueTextSize(10f);
        return lineDataset;
    }

    private void customizeChart(Chart chart) {
        // customize line chart
        chart.setDescription("");
        chart.setNoDataTextDescription("No data for the moment");
        // enable value highlighting
        chart.setHighlightPerTapEnabled(true);
        // enable touch gestures
        chart.setTouchEnabled(true);
        // alternative background color
        chart.setBackgroundColor(Color.LTGRAY);
        // get legend object
        Legend l = chart.getLegend();
        // customize legend
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);
        // customize xAxis
        XAxis xl = chart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
    }


    private void prepareLineChart() {
        // enable draging and scalinng
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);

        // enable pinch zoom to avoid scaling x and y separately
        lineChart.setPinchZoom(true);

        YAxis yl = lineChart.getAxisLeft();
        yl.setTextColor(Color.WHITE);
        //yl.setAxisMaxValue(120f);
        yl.setDrawGridLines(true);

        YAxis y12 = lineChart.getAxisLeft();
        y12.setEnabled(false);
    }

    private void createCharts() {
        /* create line chart */
        lineChart = new LineChart(this);
        customizeChart(lineChart);
        /* create radar chart */
        radarChart = new RadarChart(this);
        customizeChart(radarChart);
    }

    private Chart getCurrentChart(){
        Chart chart;
        switch (chartType) {
            default:
            case "Line Chart": chart = lineChart;
                break;
            case "Radar Chart": chart = radarChart;
                break;
        }
        return chart;
    }

    private void addChartToView() {
        graphLayout.addView(getCurrentChart());
        ViewGroup.LayoutParams params = getCurrentChart().getLayoutParams();
        /* match chart size to layout size */
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
    }

    private void getPatientData() {
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get patient from database */
        patient = getBetterDb.getPatient(patientID);
        /* close database after retrieval */
        getBetterDb.closeDatabase();
        patient.printPatient();
    }

    private void getPatientRecords() {
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get patient records from database */
        patientRecords = getBetterDb.getRecords(patientID);
        /* close database after retrieval */
        getBetterDb.closeDatabase();
        Log.v(TAG, "number of records: "+patientRecords.size());
    }

    private void prepareRecordDateSpinner() {
        List<String> recordDateList = new ArrayList();
        for(int i = 0; i < patientRecords.size(); i++) {
            recordDateList.add(patientRecords.get(i).getDateCreated());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, recordDateList);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spRecordDate.setAdapter(spinnerAdapter);
        spRecordDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Record record = patientRecords.get(position);
                displayRecord(record);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Record lastRecord = patientRecords.get(patientRecords.size()-1);
                displayRecord(lastRecord);
            }
        });
    }
}
