package com.medyas.journalapp;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AdapterJournalEntries extends RecyclerView.Adapter<AdapterJournalEntries.ViewHolder> {
    private List<EntryClass> mDataset = new ArrayList<EntryClass>();
    private List<EntryClass> items = new ArrayList<EntryClass>();
    private Resources res;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        TextView title;
        TextView content;
        TextView date;
        FrameLayout layout;
        ImageView priority;
        RelativeLayout viewFor, viewBack;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.textView_title);
            content = (TextView) v.findViewById(R.id.textView_content);
            date = (TextView) v.findViewById(R.id.textView_date);
            priority = (ImageView) v.findViewById(R.id.imageView_fav);
            layout = (FrameLayout) v.findViewById(R.id.item_linearlayout);
            viewBack = (RelativeLayout) v.findViewById(R.id.view_background);
            viewFor = (RelativeLayout) v.findViewById(R.id.view_foreground);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), EntryDetail.class);
                    intent.putExtra(view.getContext().getString(R.string.entryID), items.get(getAdapterPosition()).getDocId());
                    view.getContext().startActivity(intent);
                }
            });

            layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return false;
                }
            });
        }
    }

    public AdapterJournalEntries() {
    }

    public AdapterJournalEntries(List<EntryClass> dataList, Resources res) {
        this.res = res;
        this.mDataset = dataList;
        this.items.addAll(dataList);
    }

    public void delete(int position) { //removes the row
        mDataset.remove(position);
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void filter(String text) {
        items.clear();
        if(text.isEmpty()){
            items.addAll(mDataset);
        } else{
            text = text.toLowerCase();
            for(EntryClass item: mDataset){
                if(item.getTitle().toLowerCase().contains(text)){
                    items.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setmDataset(List<EntryClass> mDataset) {
        clearData();
        this.mDataset = mDataset;
        this.items.addAll(mDataset);

        notifyDataSetChanged();
    }

    public void clearData() {
        if(this.items != null && !this.items.isEmpty()) this.items.clear();
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        this.items.remove(position);
        this.mDataset.remove(position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AdapterJournalEntries.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entry, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            holder.title.setText(items.get(position).getTitle().substring(0, 1).toUpperCase()+items.get(position).getTitle().substring(1));
            holder.content.setText(items.get(position).getContent().substring(0, 1).toUpperCase()+items.get(position).getContent().substring(1));
        }
        catch(StringIndexOutOfBoundsException e) {
            holder.title.setText("");
            holder.content.setText("");
        }
        String date = items.get(position).getDate().split("-")[0] +"-"+ items.get(position).getDate().split("-")[1];
        holder.date.setText(date);
        holder.priority.setImageDrawable(items.get(position).retrievePriorityImage(res));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }
}

