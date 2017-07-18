package seebee.geebeeview.model.adapter;

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
import seebee.geebeeview.layout.DataVisualizationActivity;
import seebee.geebeeview.model.account.Dataset;
import seebee.geebeeview.model.consultation.School;
import seebee.geebeeview.model.monitoring.Record;

/**
 * Created by Joy on 5/31/2017.
 */

public class DatasetAdapter extends RecyclerView.Adapter<DatasetAdapter.DatasetViewHolder> {

    private ArrayList<Dataset> datasetList;
    private Context context;

    public DatasetAdapter(ArrayList<Dataset> datasetList) {
        this.datasetList = datasetList;
    }

    public class DatasetViewHolder extends RecyclerView.ViewHolder {
        public TextView tvSchoolName, tvDate;
        public Button btnStatus;

        public DatasetViewHolder(View view) {
            super(view);
            context = view.getContext();
            tvSchoolName = (TextView) view.findViewById(R.id.tv_dataset_sname);
            tvDate = (TextView) view.findViewById(R.id.tv_dataset_date);
            btnStatus = (Button) view.findViewById(R.id.btn_dataset_status);
        }
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
        if(dataset.getStatus() == 1) {
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
                    // download dataset
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datasetList.size();
    }
}
