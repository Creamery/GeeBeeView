package seebee.geebeeview.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import seebee.geebeeview.R;

/**
 * The FilterAdapter class is used to control and facilitate the methods
 * of the filter adapter in the DataVisualizationActivity.
 *
 * @author Stephanie Dy
 */

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterAdapterViewHolder> {

    private final static String TAG = "FilterAdapter";

    private ArrayList<String> filterList;
    private FilterAdapterListener mListener;

    public FilterAdapter(ArrayList<String> filterList, FilterAdapterListener mListener) {
        this.filterList = filterList;
        this.mListener = mListener;
    }

    @Override
    public FilterAdapter.FilterAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_holder, parent, false);

        return new FilterAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FilterAdapter.FilterAdapterViewHolder holder, final int position) {
        String text = filterList.get(position);
        holder.tvFilterText.setText(text);
        if(text.contentEquals("N/A")) {
            holder.tvFilterClose.setText("");
        } else {
            holder.tvFilterClose.setText("X");
        }
        /* when x button is clicked the item should be removed */
        holder.tvFilterClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.removeFilters(filterList.get(position));
                //filterList.remove(position);
                if(filterList.size() == 0) {
                    filterList.add("N/A");
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

    public interface FilterAdapterListener {
        void removeFilters(String filter);
    }

    public class FilterAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFilterText, tvFilterClose;

        private FilterAdapterViewHolder(View view) {
            super(view);
            tvFilterText = (TextView) view.findViewById(R.id.tv_filter_text);
            tvFilterClose = (TextView) view.findViewById(R.id.tv_filter_close);
        }
    }
}
