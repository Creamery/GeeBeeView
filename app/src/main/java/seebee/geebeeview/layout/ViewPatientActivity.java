package seebee.geebeeview.layout;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;

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
    private int patientID;
    private Patient patient;
    private ArrayList<Record> patientRecords;

    private RelativeLayout graphLayout;
    private LineChart lineChart;
    private Spinner spRecordColumn;
    private String recordColumn = "Height (in cm)";

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
        /* show details in relation to latest check up record*/
        if(patientRecords.size() > 0) {
            Record lastRecord = patientRecords.get(patientRecords.size()-1);
            String recordDate = lastRecord.getDateCreated();
            tvRecordDate.setText(recordDate);
            boolean isGirl = true;
            if(patient.getGender() != 1) {
                isGirl = false;
            }
            String bmi = BMICalculator.getBMIResultString(isGirl,
                    AgeCalculator.calculateAge(patient.getBirthday(), recordDate),
                    BMICalculator.computeBMIMetric(new Double(lastRecord.getHeight()).intValue(),
                            new Double(lastRecord.getWeight()).intValue()));
            tvBMI.setText(bmi);
            tvHeight.setText(Double.toString(lastRecord.getHeight())+" cm");
            tvWeight.setText(Double.toString(lastRecord.getWeight())+" kg");
            tvVisualLeft.setText(lastRecord.getVisualAcuityLeft());
            tvVisualRight.setText(lastRecord.getVisualAcuityRight());
            tvColorVision.setText(lastRecord.getColorVision());
            tvHearingLeft.setText(lastRecord.getHearingLeft());
            tvHearingRight.setText(lastRecord.getHearingRight());
            tvGrossMotor.setText(lastRecord.getGrossMotorString());
            tvFineMotorD.setText(lastRecord.getFineMotorString(lastRecord.getFineMotorDominant()));
            tvFineMotorND.setText(lastRecord.getFineMotorString(lastRecord.getFineMotorNDominant()));
        }

        addChartToView();
        prepareLineChart();
        prepareLineChartData();

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
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void prepareLineChartData() {
        LineData lineData = new LineData();
        lineData.setValueTextColor(Color.WHITE);

        // add data to line chart
        lineChart.setData(lineData);
        if(lineData != null) {
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
                Log.v(TAG, recordColumn+": "+x);
                lineData.addEntry(new Entry(x, dataset.getEntryCount()), 0);
            }
            setLineChartValueFormatter(lineData);

            // notify chart data has changed
            lineChart.notifyDataSetChanged();
        }
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
        lineDataset.setCircleSize(4f);
        lineDataset.setFillAlpha(4);
        lineDataset.setFillColor(ColorTemplate.getHoloBlue());
        lineDataset.setHighLightColor(Color.rgb(244, 11, 11));
        lineDataset.setValueTextColor(Color.WHITE);
        lineDataset.setValueTextSize(10f);
        return lineDataset;
    }

    private void prepareLineChart() {
        // customize line chart
        lineChart.setDescription("");
        lineChart.setNoDataTextDescription("No data for the moment");

        // enable value highlighting
        lineChart.setHighlightPerTapEnabled(true);

        // enable touch gestures
        lineChart.setTouchEnabled(true);

        // enable draging and scalinng
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);

        // enable pinch zoom to avoid scaling x and y separately
        lineChart.setPinchZoom(true);

        // alternative background color
        lineChart.setBackgroundColor(Color.LTGRAY);

        // get legend object
        Legend l = lineChart.getLegend();

        // customize legend
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = lineChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);

        YAxis yl = lineChart.getAxisLeft();
        yl.setTextColor(Color.WHITE);
        //yl.setAxisMaxValue(120f);
        yl.setDrawGridLines(true);

        YAxis y12 = lineChart.getAxisLeft();
        y12.setEnabled(false);

        // adjust size of layout
        ViewGroup.LayoutParams params = lineChart.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
    }

    private void addChartToView() {
        /* create line chart */
        lineChart = new LineChart(this);
        /* add line chart to view */
        graphLayout.addView(lineChart);
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
}
