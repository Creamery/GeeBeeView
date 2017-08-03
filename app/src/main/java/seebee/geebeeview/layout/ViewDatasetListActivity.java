package seebee.geebeeview.layout;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;

import seebee.geebeeview.R;
import seebee.geebeeview.database.DatabaseAdapter;
import seebee.geebeeview.model.account.Dataset;
import seebee.geebeeview.model.adapter.DatasetAdapter;

public class ViewDatasetListActivity extends BaseActivity {

    RecyclerView rvDataset;
    ArrayList<Dataset> datasetList = new ArrayList<>();
    DatasetAdapter datasetAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_dataset_list);
        /* set title shown in the upper right corner
        * setTitle(R.string.dataset_title);
        */
        rvDataset = (RecyclerView) findViewById(R.id.rv_dataset);

        datasetAdapter = new DatasetAdapter(datasetList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvDataset.setLayoutManager(mLayoutManager);
        rvDataset.setItemAnimator(new DefaultItemAnimator());
        rvDataset.setAdapter(datasetAdapter);

        prepareDatasetList();

    }

    private void prepareDatasetList() {
        DatabaseAdapter getBetterDb = new DatabaseAdapter(this);
        /* ready database for reading */
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /* get datasetList from database */
        datasetList.addAll(getBetterDb.getAllDatasets());
        /* close database after insert */
        getBetterDb.closeDatabase();
        Log.v("ViewDatasetListActivity", "number of datasets = " + datasetList.size());
        datasetAdapter.notifyDataSetChanged();
    }
}
