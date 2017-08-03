package seebee.geebeeview.layout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import seebee.geebeeview.R;
import seebee.geebeeview.database.DatabaseAdapter;
import seebee.geebeeview.model.consultation.HPI;
import seebee.geebeeview.model.consultation.Patient;

public class ViewHPIActivity extends AppCompatActivity {

    private String TAG = "ViewHPIActivity";

    Spinner spHPIDate;
    TextView tvHPIText, tvPatientName;

    int patientId, hpiId;
    
    ArrayList<HPI> HPIs = new ArrayList<>();
    Patient patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_hpi);

        hpiId = getIntent().getIntExtra(HPI.C_HPI_ID, 0);
        patientId = getIntent().getIntExtra(Patient.C_PATIENT_ID, 0);

        spHPIDate = (Spinner) findViewById(R.id.sp_hpi_date);
        tvPatientName = (TextView) findViewById(R.id.tv_hpi_name);
        tvHPIText = (TextView) findViewById(R.id.tv_hpi_text);

        prepareData();

        tvPatientName.setText(patient.getLastName()+", "+patient.getFirstName());
        if(hpiId == 0 && HPIs.size() > 0) {
            hpiId = HPIs.get(HPIs.size()-1).getHpi_id();
        }
        HPI hpi = findHPI(hpiId);
        if(hpi != null) {
            tvHPIText.setText(findHPI(hpiId).getHpiText());
        } else {
            /* close activity if consultation record does not exist */
            Toast.makeText(getBaseContext(),
                    "No consultation records found!",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        prepareSpinner();
    }

    private HPI findHPI(int hpiId) {
        HPI hpi = null;
        int i = 0;
        while(hpi == null && i < HPIs.size()) {
            if(HPIs.get(i).getHpi_id() == hpiId) {
                hpi = HPIs.get(i);
            } else {
                i++;
            }
        }
        return hpi;
    }

    private void prepareSpinner() {
        List<String> hpiDateList = new ArrayList<>();
        for(int i = 0; i < HPIs.size(); i++) {
            hpiDateList.add(HPIs.get(i).getDateCreated());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, hpiDateList);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spHPIDate.setAdapter(spinnerAdapter);

        spHPIDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                HPI hpi = HPIs.get(position);
                tvHPIText.setText(hpi.getHpiText());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void prepareData() {
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get hpi list from database */
        if(patientId != 0) {
            HPIs.addAll(getBetterDb.getHPIs(patientId));
            patient = getBetterDb.getPatient(patientId);
        } else {
            Toast.makeText(this, "No HPI record available!", Toast.LENGTH_SHORT).show();
        }
        /* close database after insert */
        getBetterDb.closeDatabase();
        Log.v(TAG, "number of HPIs = " + HPIs.size());
    }
}
