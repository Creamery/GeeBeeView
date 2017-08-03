package seebee.geebeeview.layout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;

import seebee.geebeeview.R;
import seebee.geebeeview.database.DatabaseAdapter;
import seebee.geebeeview.model.adapter.PatientListAdapter;
import seebee.geebeeview.model.consultation.Patient;
import seebee.geebeeview.model.consultation.School;
import seebee.geebeeview.model.monitoring.Record;

public class ViewPatientListActivity extends AppCompatActivity {

    private static final String TAG = "ViewPatientActivity";

    private int schoolID;
    private String date, recordColumn, columnValue;
    private RecyclerView rvPatientList;
    private ArrayList<Patient> patientList = new ArrayList<Patient>();
    private PatientListAdapter patientListAdapter;

    private TextView tvSchoolName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient_list);

        schoolID = getIntent().getIntExtra(School.C_SCHOOL_ID, 0);
        date = getIntent().getStringExtra(Record.C_DATE_CREATED);
        String schoolName = getIntent().getStringExtra(School.C_SCHOOLNAME);
        recordColumn = getIntent().getStringExtra("column");
        columnValue = getIntent().getStringExtra("value");

        rvPatientList = (RecyclerView) findViewById(R.id.rv_patient_list);
        tvSchoolName = (TextView) findViewById(R.id.tv_school_name);

        tvSchoolName.setText(schoolName);

        patientListAdapter = new PatientListAdapter(patientList, date);
        RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(getBaseContext());
        rvPatientList.setLayoutManager(rvLayoutManager);
        rvPatientList.setItemAnimator(new DefaultItemAnimator());
        rvPatientList.setAdapter(patientListAdapter);

        preparePatientList();
    }

    private void preparePatientList() {
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get patientList from database */
        if(recordColumn != null && columnValue != null) {
            patientList.addAll(getBetterDb.getPatientsWithCondition(schoolID, date, recordColumn, columnValue));
        } else{
            patientList.addAll(getBetterDb.getPatientsFromSchool(schoolID));
        }
        /* close database after insert */
        getBetterDb.closeDatabase();
        Log.v(TAG, "number of patients = " + patientList.size());
        patientListAdapter.notifyDataSetChanged();
    }
}
