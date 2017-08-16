package seebee.geebeeview.layout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import seebee.geebeeview.R;
import seebee.geebeeview.database.DatabaseAdapter;
import seebee.geebeeview.database.VolleySingleton;
import seebee.geebeeview.model.account.Dataset;
import seebee.geebeeview.model.adapter.DatasetAdapter;

public class ViewDatasetListActivity extends AppCompatActivity {

    RecyclerView rvDataset;
    Button btnRefresh;
    ArrayList<Dataset> datasetList = new ArrayList<>();
    DatasetAdapter datasetAdapter;
    private static String URL_SAVE_NAME = "http://128.199.205.226/save.php";
    DatabaseAdapter getBetterDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_dataset_list);

        getBetterDb = new DatabaseAdapter(this);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        rvDataset = (RecyclerView) findViewById(R.id.rv_dataset);

        datasetAdapter = new DatasetAdapter(datasetList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvDataset.setLayoutManager(mLayoutManager);
        rvDataset.setItemAnimator(new DefaultItemAnimator());
        rvDataset.setAdapter(datasetAdapter);
        prepareDatasetList();

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDatasets();
            }
        });


    }

    private void prepareDatasetList() {
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        //updateDatasets();
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get datasetList from database */
        datasetList.clear();
        datasetList.addAll(getBetterDb.getAllDatasets());
        /* close database after insert */
        getBetterDb.closeDatabase();
        Log.v("ViewDatasetListActivity", "number of datasets = " + datasetList.size());
        datasetAdapter.notifyDataSetChanged();
    }

    private void updateDatasets(){
        ArrayList<Dataset> datasets;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_NAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonDataset;
                        Dataset dataset;
                        try {
                            JSONArray jsonDatasets = new JSONArray(response);
                            for(int i=1;i<jsonDatasets.length();i++){
                                jsonDataset = jsonDatasets.getJSONObject(i);
                                dataset = new Dataset(jsonDataset.getInt("school_id"), jsonDataset.getString("name"), jsonDataset.getString("date_created"), 0);
                                getBetterDb.updateDatasetList(dataset);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("JSON ERROR:", "SOMETHING WRONG WITH JSON PARSING");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Connection Error!",Toast.LENGTH_SHORT).show();
                        Log.e("RESOPNSE ERROR:", "IDK WHY THO");
                        error.printStackTrace();
                        //progressDialog.dismiss();
                        //on error storing the name to sqlite with status unsynced
                        //saveNameToLocalStorage(name, NAME_NOT_SYNCED_WITH_SERVER);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("request", "query-datasets");
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        prepareDatasetList();
    }

}
