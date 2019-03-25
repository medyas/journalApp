/*
 * Copyright 2018 Medyas Journal App
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.medyas.journalapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class JournalEntries extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private AdapterJournalEntries mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<EntryClass> dataList = new ArrayList<EntryClass>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String currentUserId;
    private FirebaseUser user;

    private SearchView searchEntry;
    private ProgressBar progress;
    SwipeRefreshLayout swipeRefreshLayout;
    private DataBaseConnection database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_entries);
        database = new DataBaseConnection();
        database.initDB(this).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                refreshData();
            }

            @Override
            public void onError(Throwable e) {

            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        setTitle(user.getDisplayName().substring(0, 1).toUpperCase()+user.getDisplayName().substring(1)+" Journals");

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        currentUserId = sharedPref.getString(getString(R.string.clientUID), null);
        if(currentUserId == null) {
            currentUserId = user.getUid();
            sharedPref.edit().putString(getString(R.string.clientUID), currentUserId);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_entry_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddEditEntry.class);
                startActivity(intent);
            }
        });

        swipeRefreshLayout =
                (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.secondaryDarkColor, R.color.secondaryColor, R.color.secondaryLightColor);


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_items);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // specify an adapter (see also next example)
        mAdapter = new AdapterJournalEntries(dataList, getResources());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setAdapter(mAdapter);

        progress = (ProgressBar) findViewById(R.id.progressBar);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition(); //get position which is swipe

                if (direction == ItemTouchHelper.LEFT) {    //if swipe left

                    AlertDialog.Builder builder = new AlertDialog.Builder(JournalEntries.this, R.style.AppThemeDeleteDialog); //alert for confirm to delete
                    builder.setMessage("Are you sure to delete?").setIcon(R.drawable.baseline_delete_white_48);    //set message

                    builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() { //when click on DELETE
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteEntry(dataList.get(position));
                            mAdapter.notifyItemRemoved(position);    //item removed from recylcerview
                            mAdapter.removeItem(position);
//                            dataList.remove(position);  //then remove item
                            mAdapter.notifyDataSetChanged();
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAdapter.notifyItemRemoved(position + 1);    //notifies the RecyclerView Adapter that data in adapter has been removed at a particular position.
                            mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());   //notifies the RecyclerView Adapter that positions of element in adapter has been changed from position(removed element index to end of list), please update it.
                        }
                    }).show();  //show alert dialog
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                final View foregroundView = ((AdapterJournalEntries.ViewHolder) viewHolder).viewFor;

                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                        actionState, isCurrentlyActive);
            }

            @Override
            public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                final View foregroundView = ((AdapterJournalEntries.ViewHolder) viewHolder).viewFor;
                getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                        actionState, isCurrentlyActive);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                final View foregroundView = ((AdapterJournalEntries.ViewHolder) viewHolder).viewFor;
                getDefaultUIUtil().clearView(foregroundView);
            }

            @Override
            public int convertToAbsoluteDirection(int flags, int layoutDirection) {
                return super.convertToAbsoluteDirection(flags, layoutDirection);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    public void refreshData() {
        database.getEntries(currentUserId)
                .subscribe(new DisposableObserver<List<EntryClass>>() {
                    @Override
                    public void onNext(List<EntryClass> documents) {
                        if(documents != null && !documents.isEmpty()) {
                            dataList.clear();
                            dataList.addAll(documents);
                            mAdapter.setmDataset(dataList);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            if(documents != null && documents.isEmpty()) {
                                Snackbar.make(mRecyclerView, "List is empty", Snackbar.LENGTH_INDEFINITE)
                                        .show();
                            } else {
                                Snackbar.make(mRecyclerView, "Could not get Data", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Retry", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                refreshData();
                                            }
                                        })
                                        .show();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        progress.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
        /*db.collection("journal_entries")
                .whereEqualTo(getString(R.string.client_uid), currentUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            dataList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                EntryClass entry = new EntryClass(document.getData().get(getString(R.string.entry_title)).toString(),
                                        document.getData().get(getString(R.string.entry_content)).toString(),
                                        document.getData().get(getString(R.string.entry_date)).toString(),
                                        document.getData().get(getString(R.string.entry_priority)).toString(),
                                        document.getId());
                                dataList.add(entry);
                            }
                            mAdapter.setmDataset(dataList);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Snackbar.make(mRecyclerView, "Could not get Data", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Retry", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            refreshData();
                                        }
                                    })
                                    .show();
                        }
                        progress.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });*/
    }

    public void deleteEntry(EntryClass item) {
        progress.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        database.deleteEntry(item).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                progress.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Entry deleted.",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                progress.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Could not delete the entry!",
                        Toast.LENGTH_SHORT).show();
            }
        });
        /*db.collection("journal_entries").document(docId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progress.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "Entry deleted.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progress.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "Could not delete the entery!",
                                Toast.LENGTH_SHORT).show();
                    }
                });*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_journal, menu);

        searchEntry = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchEntry.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.filter(newText);
                return true;
            }
        });
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_signout:
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
