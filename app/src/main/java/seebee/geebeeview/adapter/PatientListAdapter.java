package seebee.geebeeview.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import seebee.geebeeview.R;
import seebee.geebeeview.layout.ViewPatientActivity;
import seebee.geebeeview.model.consultation.Patient;
import seebee.geebeeview.model.monitoring.AgeCalculator;

/**
 * Created by Joy on 7/11/2017.
 */

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.PatientListViewHolder> {

    private static final String TAG = "PatientListAdapter";

    private ArrayList<Patient> patientList;
    private Context context;
    private String recordDate;

    public PatientListAdapter(ArrayList<Patient> patientList, String date) {
        this.patientList = patientList;
        this.recordDate = date;
    }

    @Override
    public PatientListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.patient_list_holder, parent, false);

        return new PatientListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PatientListViewHolder holder, int position) {
        final Patient patient = patientList.get(position);
        String name = patient.getLastName() + ", "+patient.getFirstName();
        holder.tvName.setText(name);
        holder.tvGender.setText(patient.getGenderString());

        int age = AgeCalculator.calculateAge(patient.getBirthday(), recordDate);
        //Log.v(TAG, "Birthday: "+patient.getBirthday()+" RecordDate: "+recordDate);
        //Log.v(TAG, "Age: "+age);
        holder.tvAge.setText(Integer.toString(age));
        holder.btnViewRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewPatientActivity.class);
                intent.putExtra(Patient.C_PATIENT_ID, patient.getPatientID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public class PatientListViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvGender, tvAge;
        public Button btnViewRecord;

        public PatientListViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            tvName = (TextView) itemView.findViewById(R.id.tv_patient_name);
            tvGender = (TextView) itemView.findViewById(R.id.tv_patient_gender);
            tvAge = (TextView) itemView.findViewById(R.id.tv_patient_age);
            btnViewRecord = (Button) itemView.findViewById(R.id.btn_view_record);
        }
    }
}
