package seebee.geebeeview.model.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;

import seebee.geebeeview.R;
import seebee.geebeeview.database.DatabaseAdapter;
import seebee.geebeeview.layout.ViewHPIActivity;
import seebee.geebeeview.model.consultation.HPI;
import seebee.geebeeview.model.consultation.Patient;

/**
 * Created by Joy on 7/18/2017.
 */

public class HPIListAdapter extends RecyclerView.Adapter<HPIListAdapter.HPIListViewHolder> {

    private ArrayList<HPI> hpiList;
    private Context context;

    public HPIListAdapter (ArrayList<HPI> hpiList) {
        this.hpiList = hpiList;
    }

    @Override
    public HPIListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dataset_holder, parent, false);

        return new HPIListAdapter.HPIListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HPIListViewHolder holder, int position) {
        final HPI hpi = hpiList.get(position);

        final Patient patient = getPatientFromDB(hpi.getPatientId());
        String patientName = patient.getLastName()+", "+patient.getFirstName();
        holder.tvPatientName.setText(patientName);
        holder.tvDate.setText(hpi.getDateCreated());

        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewHPIActivity.class);
                intent.putExtra(HPI.C_HPI_ID, hpi.getHpi_id());
                intent.putExtra(Patient.C_PATIENT_ID, patient.getPatientID());
                context.startActivity(intent);
            }
        });
    }

    private Patient getPatientFromDB(int patientId) {
        Patient patient = null;
        DatabaseAdapter getBetterDb = new DatabaseAdapter(context);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get patient from database */
        patient = getBetterDb.getPatient(patientId);
        /* close database after retrieval */
        getBetterDb.closeDatabase();
        patient.printPatient();
        return patient;
    }

    @Override
    public int getItemCount() {
        return hpiList.size();
    }

    public class HPIListViewHolder extends RecyclerView.ViewHolder {
        public TextView tvPatientName, tvDate;
        public Button btnView;

        public HPIListViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            tvPatientName = (TextView) itemView.findViewById(R.id.tv_dataset_sname);
            tvDate = (TextView) itemView.findViewById(R.id.tv_dataset_date);
            btnView = (Button) itemView.findViewById(R.id.btn_dataset_status);
            btnView.setText(R.string.view);
        }
    }
}
