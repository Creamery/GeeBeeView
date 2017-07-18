package seebee.geebeeview.model.adapter;

import android.support.v7.widget.RecyclerView;
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

    private ArrayList<String> textList;

    public TextHolderAdapter (ArrayList<String> textList) {
        this.textList = textList;
    }

    public class TextAdapterViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTextHolder;

        public TextAdapterViewHolder(View view) {
            super(view);
            tvTextHolder = (TextView) view.findViewById(R.id.tv_text_holder);
        }
    }

    @Override
    public TextAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_holder, parent, false);

        return new TextAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TextHolderAdapter.TextAdapterViewHolder holder, int position) {
        String text = textList.get(position);
        holder.tvTextHolder.setText(text);
    }

    @Override
    public int getItemCount() {
        return textList.size();
    }
}
