package com.medyas.journalapp;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AdapterJournalEntries extends RecyclerView.Adapter<AdapterJournalEntries.ViewHolder> {
    private List<EntryListClass> mDataset = new ArrayList<EntryListClass>();
    private Resources res;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        TextView title;
        TextView content;
        TextView date;
        LinearLayout layout;
        ImageView priority;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.textView_title);
            content = (TextView) v.findViewById(R.id.textView_content);
            date = (TextView) v.findViewById(R.id.textView_date);
            priority = (ImageView) v.findViewById(R.id.imageView_fav);
            layout = (LinearLayout) v.findViewById(R.id.item_linearlayout);
        }
    }

    public AdapterJournalEntries() {
    }

    public AdapterJournalEntries(List<EntryListClass> dataList, Resources res) {
        this.res = res;
        this.mDataset = dataList;
    }



    // Create new views (invoked by the layout manager)
    @Override
    public AdapterJournalEntries.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entry, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.title.setText(mDataset.get(position).getTitle());
        holder.content.setText(mDataset.get(position).getContent());
        holder.date.setText(mDataset.get(position).getDate());
        holder.priority.setImageDrawable(mDataset.get(position).getPriorityImage(res));


        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EntryDetail.class);
                intent.putExtra("entry_id", "");
                view.getContext().startActivity(intent);
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

