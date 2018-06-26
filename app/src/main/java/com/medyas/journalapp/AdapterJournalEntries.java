package com.medyas.journalapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AdapterJournalEntries extends RecyclerView.Adapter<AdapterJournalEntries.ViewHolder> {
    private String[] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View rootView;
        public ViewHolder(View v) {
            super(v);
            rootView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterJournalEntries(String[] myDataset) {
        mDataset = myDataset;
    }

    public AdapterJournalEntries() {
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
        holder.title.setText(mDataset[position]);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
