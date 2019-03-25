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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class EntryDetail extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextView entryTitle;
    private TextView entryContent;
    private CheckBox entryPri;

    private ScrollView container;

    ProgressBar progress;

    private String entryId;
    private DataBaseConnection database;
    private String currentUserId;
    private EntryClass entryItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_detail);
        database = new DataBaseConnection();
        database.initDB(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        currentUserId = sharedPref.getString(getString(R.string.clientUID), null);

        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        progress = findViewById(R.id.progressBar);
        entryTitle = findViewById(R.id.textViewTitle);
        entryContent = findViewById(R.id.textViewContent);
        entryPri = findViewById(R.id.checkBoxPri);
        container = findViewById(R.id.entry_container);
        container.setVisibility(View.GONE);

        try {
            entryId = getIntent().getExtras().getString(getString(R.string.entryID));
        } catch (NullPointerException e) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.edit_entry_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddEditEntry.class);
                intent.putExtra(getString(R.string.entryID), entryId);
                startActivity(intent);
            }
        });

        database.getEntry(currentUserId, entryId).subscribe(new DisposableObserver<EntryClass>() {
            @Override
            public void onNext(EntryClass entryClass) {
                if (entryClass != null) {
                    entryItem = entryClass;
                    setTitle(entryClass.getTitle());
                    entryTitle.setText(entryClass.getTitle());
                    entryContent.setText(entryClass.getContent());
                    if(entryClass.getPriority().equals("true")) {
                        entryPri.setChecked(true);
                    }
                } else {
                    Snackbar.make(entryTitle, "Null Data was found", Snackbar.LENGTH_LONG).show();
                }
                progress.setVisibility(View.GONE);
                container.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Throwable e) {
                progress.setVisibility(View.GONE);
                container.setVisibility(View.VISIBLE);
                e.printStackTrace();
                Snackbar.make(entryTitle, "Could not get Data", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() {
            }
        });

        /*db.collection("journal_entries").document(entryId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        setTitle(document.getData().get(getString(R.string.entry_title)).toString());
                        entryTitle.setText(document.getData().get(getString(R.string.entry_title)).toString().substring(0, 1).toUpperCase()+document.getData().get(getString(R.string.entry_title)).toString().substring(1));
                        entryContent.setText(document.getData().get(getString(R.string.entry_content)).toString());
                        if(document.getData().get(getString(R.string.entry_priority)).toString().equals("true")) {
                            entryPri.setChecked(true);
                        }
                    } else {
                        Snackbar.make(entryTitle, "Could not get Data", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(entryTitle, "Could not get Data", Snackbar.LENGTH_LONG).show();
                }

                entryPri.setEnabled(false);
                progress.setVisibility(View.GONE);
                container.setVisibility(View.VISIBLE);
            }
        });*/
    }

    public void deleteEntry() {
        progress.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);
        database.deleteEntry(entryItem).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                progress.setVisibility(View.GONE);
                container.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplicationContext(), JournalEntries.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Entry deleted.",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                progress.setVisibility(View.GONE);
                container.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Could not delete the entery!",
                        Toast.LENGTH_SHORT).show();
            }
        });
       /* db.collection("journal_entries").document(docId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progress.setVisibility(View.GONE);
                        container.setVisibility(View.VISIBLE);
                        Intent intent = new Intent(getApplicationContext(), JournalEntries.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Entry deleted.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progress.setVisibility(View.GONE);
                        container.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "Could not delete the entery!",
                                Toast.LENGTH_SHORT).show();
                    }
                });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_entry_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItem = item.getItemId();
        if(menuItem == R.id.menu_share_entry) {
            Intent shareEntryIntent = new Intent();
            shareEntryIntent.setAction(Intent.ACTION_SEND);
            shareEntryIntent.putExtra(Intent.EXTRA_TITLE, entryTitle.getText());
            shareEntryIntent.putExtra(Intent.EXTRA_TEXT, entryContent.getText());
            shareEntryIntent.setType("text/plain");
            startActivity(shareEntryIntent);
            return true;
        } else if( menuItem == R.id.menu_delete_entry) {
            deleteEntry();
            return true;
        } else if( menuItem == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
