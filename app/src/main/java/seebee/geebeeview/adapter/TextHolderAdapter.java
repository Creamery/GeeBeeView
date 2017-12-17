package seebee.geebeeview.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import seebee.geebeeview.R;

/**
 * Created by Joy on 6/6/2017.
 */

public class TextHolderAdapter extends RecyclerView.Adapter<TextHolderAdapter.TextAdapterViewHolder> {

    private static final String TAG = "TextHolderAdapter";
    private ArrayList<String> textList;
    private TextListener textListener;

    public TextHolderAdapter (ArrayList<String> textList , TextListener mListener) {
        this.textList = textList;
        this.textListener = mListener;
    }

    @Override
    public TextAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_holder, parent, false);

        return new TextAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TextHolderAdapter.TextAdapterViewHolder holder, final int position) {
        String text = textList.get(position);
        holder.tvDataset.setText(text);
        if(position != 0) {
            holder.tvClose.setText(R.string.close);
        } else {
            holder.tvClose.setText("");
        }

        holder.tvClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textListener.removeDataset(textList.get(position));
                    //textList.remove(position);
                    notifyDataSetChanged();
                    Log.v(TAG, "remove dataset click");
                }
            });
    }

    @Override
    public int getItemCount() {
        return textList.size();
    }

    public interface TextListener {
        void removeDataset(String dataset);
    }

    public class TextAdapterViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDataset, tvClose;

        public TextAdapterViewHolder(View view) {
            super(view);
            tvDataset = (TextView) view.findViewById(R.id.tv_filter_text);
            tvClose = (TextView) view.findViewById(R.id.tv_filter_close);
        }
    }

}
