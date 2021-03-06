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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddEditEntry extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String entryId = null;
    String currentUserId;

    private EditText entryTitle;
    private EditText entryContent;
    private CheckBox entryPri;
    private ProgressBar progress;
    private ScrollView container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_entry);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        currentUserId = sharedPref.getString(getString(R.string.clientUID), null);
        if(currentUserId == null) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            currentUserId = user.getUid();
        }

        try {
            entryId = getIntent().getExtras().getString(getString(R.string.entryID));
        } catch (NullPointerException e) {
        }

        entryTitle = (EditText) findViewById(R.id.editTextTitle);
        entryContent = (EditText) findViewById(R.id.editTextContent);
        entryPri = (CheckBox) findViewById(R.id.checkBoxPri);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        container = (ScrollView) findViewById(R.id.add_edit_container);
        container.setVisibility(View.GONE);

        FloatingActionButton cancelFab = (FloatingActionButton) findViewById(R.id.cancelEntryFab);
        cancelFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FloatingActionButton saveFab = (FloatingActionButton) findViewById(R.id.saveEntryFab);
        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(entryTitle.getText().toString().isEmpty()) {
                    entryTitle.setError("Please Provide a Title");
                } else if(entryContent.getText().toString().isEmpty()) {
                    entryContent.setError("Please provide Content");
                } else {
                    progress.setVisibility(View.VISIBLE);
                    container.setVisibility(View.GONE);
                    String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
                    if(entryId != null) {
                        DocumentReference entry = db.collection("journal_entries").document(entryId);
                        entry.update(getString(R.string.entry_title), entryTitle.getText().toString());
                        entry.update(getString(R.string.entry_content), entryContent.getText().toString());
                        entry.update(getString(R.string.entry_date), timeStamp);
                        entry.update(getString(R.string.entry_priority), entryPri.isChecked())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(getApplicationContext(), EntryDetail.class);
                                        intent.putExtra(getString(R.string.entryID), entryId);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                    progress.setVisibility(View.GONE);
                                    container.setVisibility(View.VISIBLE);
                                    Toast.makeText(getApplicationContext(), "Could not update entry!", Toast.LENGTH_LONG);
                            }
                        });;
                    } else {
                        Map<String, Object> entry = new HashMap<>();
                        entry.put(getString(R.string.entry_title), entryTitle.getText().toString());
                        entry.put(getString(R.string.entry_content), entryContent.getText().toString());
                        entry.put(getString(R.string.entry_date), timeStamp);
                        entry.put(getString(R.string.entry_priority), entryPri.isChecked());
                        entry.put(getString(R.string.client_uid), currentUserId);

                        db.collection("journal_entries")
                                .add(entry)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Intent intent = new Intent(getApplicationContext(), EntryDetail.class);
                                        intent.putExtra(getString(R.string.entryID), documentReference.getId());
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progress.setVisibility(View.GONE);
                                        container.setVisibility(View.VISIBLE);
                                        Toast.makeText(getApplicationContext(), "Could not add entry!", Toast.LENGTH_LONG);
                                    }
                                });
                    }
                }
            }
        });

        if(entryId == null) {
            progress.setVisibility(View.GONE);
            container.setVisibility(View.VISIBLE);
            setTitle("New Entry");
        } else {
            db.collection("journal_entries").document(entryId)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            setTitle(document.getData().get(getString(R.string.entry_title)).toString());
                            entryTitle.setText(document.getData().get(getString(R.string.entry_title)).toString());
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

                    progress.setVisibility(View.GONE);
                    container.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
