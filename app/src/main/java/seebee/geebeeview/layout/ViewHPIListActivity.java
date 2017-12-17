package seebee.geebeeview.layout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

import seebee.geebeeview.R;
import seebee.geebeeview.adapter.HPIListAdapter;
import seebee.geebeeview.database.DatabaseAdapter;
import seebee.geebeeview.model.consultation.HPI;
import seebee.geebeeview.model.consultation.Patient;
import seebee.geebeeview.model.consultation.School;

public class ViewHPIListActivity extends AppCompatActivity {

    private final String TAG = "ViewHPIListActivity";

    RecyclerView rvHPIList;
    ArrayList<HPI> hpiList = new ArrayList<HPI>();
    HPIListAdapter hpiListAdapter;

    int schoolId, patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_hpi_list);

        schoolId = getIntent().getIntExtra(School.C_SCHOOL_ID, 0);
        patientId = getIntent().getIntExtra(Patient.C_PATIENT_ID, 0);

        rvHPIList = (RecyclerView) findViewById(R.id.rv_hpi_list);

        hpiListAdapter = new HPIListAdapter(hpiList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvHPIList.setLayoutManager(mLayoutManager);
        rvHPIList.setItemAnimator(new DefaultItemAnimator());
        rvHPIList.setAdapter(hpiListAdapter);

        prepareHPIList();

    }

    private void prepareHPIList() {
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get hpi list from database */
        if(schoolId != 0 || patientId != 0) {
            if(schoolId != 0) {
                hpiList.addAll(getBetterDb.getHPIsFromSchool(schoolId));
            } else {
                hpiList.addAll(getBetterDb.getHPIs(patientId));
            }
        } else {
            Toast.makeText(this, "No HPI record available!", Toast.LENGTH_SHORT).show();
        }
        /* close database after insert */
        getBetterDb.closeDatabase();
        Log.v(TAG, "number of HPIs = " + hpiList.size());
        hpiListAdapter.notifyDataSetChanged();
    }
}
