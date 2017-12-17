package seebee.geebeeview.layout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import seebee.geebeeview.R;
import seebee.geebeeview.database.DatabaseAdapter;
import seebee.geebeeview.model.consultation.Patient;
import seebee.geebeeview.model.monitoring.Record;

public class ViewImmunizationActivity extends AppCompatActivity {
    private static final String TAG = "ImmunizationActivity";
    /* layout components */
    private TextView tvName;
    private Spinner spDate;
    private ImageView ivImmunizaiton;
    /* needed values */
    private int patientId;
    private Patient patient;
    private ArrayList<Record> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_immunization);
        /* connect activity to layout components */
        tvName = (TextView) findViewById(R.id.tv_vi_name);
        spDate = (Spinner) findViewById(R.id.sp_vi_date);
        ivImmunizaiton = (ImageView) findViewById(R.id.iv_vaccination);
        /* get patient id */
        patientId = getIntent().getIntExtra(Patient.C_PATIENT_ID, 0);
        if(patientId == 0) {
            finish();
        }


        prepareRecords();
        prepareSpinner();

        /* display patient name */
        tvName.setText(patient.getLastName()+", "+patient.getFirstName());
    }

    private void prepareSpinner() {
        List<String> dateList = new ArrayList<>();
        ArrayList<Record> temp = new ArrayList<>();
        for(int i = 0; i < records.size(); i++) {
            if(records.get(i).getVaccination() != null) {
                dateList.add(records.get(i).getDateCreated());
                temp.add(records.get(i));
            }
        }
        records.clear();
        records.addAll(temp);

        if(records.size() == 0) {
            finish();
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, dateList);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spDate.setAdapter(spinnerAdapter);

        spDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Record record = records.get(position);
                Bitmap bmp = BitmapFactory.decodeByteArray(record.getVaccination(), 0, record.getVaccination().length);

                ivImmunizaiton.setImageBitmap(Bitmap.createScaledBitmap(bmp, ivImmunizaiton.getWidth(),
                        ivImmunizaiton.getHeight(), false));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Bitmap bmp = BitmapFactory.decodeByteArray(records.get(0).getVaccination(), 0, records.get(0).getVaccination().length);

                ivImmunizaiton.setImageBitmap(Bitmap.createScaledBitmap(bmp, ivImmunizaiton.getWidth(),
                        ivImmunizaiton.getHeight(), false));
            }
        });
    }

    private void prepareRecords() {
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get hpi list from database */
        if(patientId != 0) {
            patient = getBetterDb.getPatient(patientId);
            records = getBetterDb.getRecords(patientId);
        } else {
            Toast.makeText(this, "No Immunization record available!", Toast.LENGTH_SHORT).show();
        }
        /* close database after insert */
        getBetterDb.closeDatabase();
        Log.v(TAG, "number of records = " + records.size());
    }
}
