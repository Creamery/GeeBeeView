package seebee.geebeeview.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import seebee.geebeeview.R;
import seebee.geebeeview.database.DatabaseAdapter;
import seebee.geebeeview.database.VolleySingleton;
import seebee.geebeeview.layout.DataVisualizationActivity;
import seebee.geebeeview.model.account.Dataset;
import seebee.geebeeview.model.consultation.Patient;
import seebee.geebeeview.model.consultation.School;
import seebee.geebeeview.model.monitoring.Record;

/**
 * Created by Joy on 5/31/2017.
 */

public class DatasetAdapter extends RecyclerView.Adapter<DatasetAdapter.DatasetViewHolder> {
    private static String URL_SAVE_NAME = "http://128.199.205.226/server.php";
    private final String TAG = "DatasetAdapter";
    private ArrayList<Dataset> datasetList;
    private Context context;
    private DatabaseAdapter getbetterDb ;

    public DatasetAdapter(ArrayList<Dataset> datasetList) {
        this.datasetList = datasetList;
    }

    @Override
    public DatasetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dataset_holder, parent, false);

        return new DatasetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DatasetViewHolder holder, int position) {
        final Dataset dataset = datasetList.get(position);
        holder.tvSchoolName.setText(dataset.getSchoolName());
        holder.tvDate.setText(dataset.getDate());
        //Log.d(TAG, "It should be View!");
        if(dataset.getStatus() == 1) {
            //Log.d(TAG, "It should be View!");
            holder.btnStatus.setText(R.string.view);
            holder.btnStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // move to data visualization activity
                    Intent intent = new Intent(context, DataVisualizationActivity.class);
                    intent.putExtra(School.C_SCHOOLNAME, dataset.getSchoolName());
                    intent.putExtra(School.C_SCHOOL_ID, dataset.getSchoolID());
                    intent.putExtra(Record.C_DATE_CREATED, dataset.getDate());
                    context.startActivity(intent);
                }
            });
        } else {
            holder.btnStatus.setText(R.string.download);
            holder.btnStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dataset.getStatus() == 0){
                        downloadDataset(dataset);
                        getbetterDb.updateDatasetStatus(dataset);
                        holder.btnStatus.setText(R.string.view);
                        dataset.setStatusTo1();
                    }
                    else if(dataset.getStatus() == 1){
                        // move to data visualization activity
                        Intent intent = new Intent(context, DataVisualizationActivity.class);
                        intent.putExtra(School.C_SCHOOLNAME, dataset.getSchoolName());
                        intent.putExtra(School.C_SCHOOL_ID, dataset.getSchoolID());
                        intent.putExtra(Record.C_DATE_CREATED, dataset.getDate());
                        context.startActivity(intent);
                    }


                }

            });
        }
    }

    @Override
    public int getItemCount() {
        return datasetList.size();
    }

    private void downloadDataset(final Dataset dataset){


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_NAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonRecord;
                        Record record;
                        Patient patient;
                        School school;
                        try {
                            JSONArray jsonRecordsArray = new JSONArray(response);
                            for(int i=1;i<jsonRecordsArray.length();i++){
                                jsonRecord = jsonRecordsArray.getJSONObject(i);
                                record = new Record(jsonRecord.getInt(Record.C_RECORD_ID), jsonRecord.getInt(Record.C_PATIENT_ID),
                                        jsonRecord.getString(Record.C_DATE_CREATED), jsonRecord.getInt(Record.C_HEIGHT), jsonRecord.getInt(Record.C_WEIGHT),
                                        jsonRecord.getString(Record.C_VISUAL_ACUITY_LEFT), jsonRecord.getString(Record.C_VISUAL_ACUITY_RIGHT),
                                        jsonRecord.getString(Record.C_COLOR_VISION), jsonRecord.getString(Record.C_HEARING_LEFT),
                                        jsonRecord.getString(Record.C_HEARING_RIGHT), jsonRecord.getInt(Record.C_GROSS_MOTOR),
                                        jsonRecord.getInt(Record.C_FINE_MOTOR_DOMINANT), jsonRecord.getInt(Record.C_FINE_MOTOR_N_DOMINANT),
                                        jsonRecord.getInt(Record.C_FINE_MOTOR_HOLD), jsonRecord.getString(Record.C_VACCINATION).getBytes(),
                                        jsonRecord.getString(Record.C_PATIENT_PICTURE).getBytes(), jsonRecord.getString(Record.C_REMARKS_STRING),
                                        jsonRecord.getString(Record.C_REMARKS_AUDIO).getBytes());
                                patient = new Patient(jsonRecord.getInt(Patient.C_PATIENT_ID),jsonRecord.getString(Patient.C_FIRST_NAME),jsonRecord.getString(Patient.C_LAST_NAME),
                                        jsonRecord.getString(Patient.C_BIRTHDAY), jsonRecord.getInt(Patient.C_GENDER), jsonRecord.getInt(Patient.C_SCHOOL_ID),jsonRecord.getInt(Patient.C_HANDEDNESS),
                                        jsonRecord.getString(Patient.C_REMARKS_STRING),jsonRecord.getString(Patient.C_REMARKS_AUDIO).getBytes());
                                school = new School(jsonRecord.getInt(School.C_SCHOOL_ID),jsonRecord.getString(School.C_SCHOOLNAME), jsonRecord.getString("citymunCode"));
                                getbetterDb.updateRecord(record);
                                getbetterDb.updatePatient(patient);
                                getbetterDb.updateSchools(school);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("DatasetAdapter:", "SOMETHING WRONG WITH JSON PARSING");
                            Log.e("RESPONSE IS:" , response);
                            Log.e("ID:" , Integer.toString(dataset.getSchoolID()));
                            Log.e("DATE:" , dataset.getDate());

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Connection Error! Can't fetch data for dataset.",Toast.LENGTH_LONG).show();
                        Log.e("DOWNLOAD ERROR:", "Can't fetch data for dataset.");
                        error.printStackTrace();

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("request", "download-data");
                params.put("school_id", Integer.toString(dataset.getSchoolID()));
                //params.put("name", dataset.getSchoolName());
                params.put("date_created", dataset.getDate().replaceAll("'",""));
                return params;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);


    }

//    public void refreshDataset(ArrayList<Dataset> newDatasets){
//        if (datasetList != null) {
//            datasetList.clear();
//            datasetList.addAll(newDatasets);
//        }
//        else {
//            datasetList = newDatasets;
//        }
//        notifyDataSetChanged();
//    }

    public void clearDatasetList(){
        datasetList.clear();
    }

    public class DatasetViewHolder extends RecyclerView.ViewHolder {
        public TextView tvSchoolName, tvDate;
        public Button btnStatus;

        public DatasetViewHolder(View view) {
            super(view);
            context = view.getContext();
            getbetterDb = new DatabaseAdapter(context);
            tvSchoolName = (TextView) view.findViewById(R.id.tv_dataset_sname);
            tvDate = (TextView) view.findViewById(R.id.tv_dataset_date);
            btnStatus = (Button) view.findViewById(R.id.btn_dataset_status);
        }
    }
}