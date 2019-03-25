package com.medyas.journalapp;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.UpdateItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedParallelScanList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

import org.w3c.dom.EntityReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class DataBaseConnection {

    public static final String DYNAMODB_TABLE = "journal_entries";
    public static Table dbTable = null;
    private static DynamoDBMapper dynamoDBMapper = null;

    public DataBaseConnection() { }

    public void connectToDatabase(Context context) {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                "us-east-1:720b3d52-5023-44a6-afe6-90c2236d92ec", // Identity pool ID
                Regions.US_EAST_1 // Region
        );
        // Add code to instantiate a AmazonDynamoDBClient
        final AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);
        dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .build();
        dbTable = Table.loadTable(dynamoDBClient, DYNAMODB_TABLE);
    }

    public Completable initDB(final Context context) {
        if(dbTable == null) {
            // Create a table reference
            return new Completable() {

                @Override
                protected void subscribeActual(final CompletableObserver observer) {
                    connectToDatabase(context);
                    observer.onComplete();
                }
            }.subscribeOn(Schedulers.newThread()).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        } else {
            return new Completable() {

                @Override
                protected void subscribeActual(CompletableObserver observer) {
                    observer.onComplete();
                }
            }.subscribeOn(Schedulers.newThread()).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    }

    public Observable<List<EntryClass>> getEntries(final String clientId) {
        return Single.fromCallable(new Callable<List<EntryClass>>() {
            @Override
            public List<EntryClass> call() {
              /*  List<Document> docs = dbTable.query(new Primitive(clientId)).getAllResults();
                for (Document document: documents) {
                    dataList.add(EntryClass.parseDocument(document));
                }*/
                Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
                eav.put(":val1", new AttributeValue().withS(clientId));

                DynamoDBScanExpression queryExpression = new DynamoDBScanExpression()
                        .withFilterExpression("clientUid = :val1")
                        .withExpressionAttributeValues(eav);

                PaginatedParallelScanList<EntryClass> docs = dynamoDBMapper.parallelScan(EntryClass.class, queryExpression, 2);
                Log.d("mainactivity", String.format("docs length: %d", docs.size()));

                return (List<EntryClass>) docs;
            }
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .toObservable();
    }

    public Observable<EntryClass> getEntry(final String clientId, final String docId) {
        return Single.fromCallable(new Callable<EntryClass>() {
            @Override
            public EntryClass call() {
                /*Document doc = dbTable.getItem(new Primitive(clientId), new Primitive(docId));
                return EntryClass.parseDocument(doc);*/
                return dynamoDBMapper.load(EntryClass.class, docId);
            }
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .toObservable();
    }

    public Completable addEntry(final EntryClass item) {
        return new Completable() {
            @Override
            protected void subscribeActual(CompletableObserver observer) {
//                dbTable.putItem(item.getAsDocument());
                dynamoDBMapper.save(item);
                observer.onComplete();
            }
        }.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable updateEntry(final EntryClass item) {
        return new Completable() {
            @Override
            protected void subscribeActual(CompletableObserver observer) {
//                dbTable.updateItem(item.getAsDocument(), new UpdateItemOperationConfig());
                dynamoDBMapper.save(item);
                observer.onComplete();
            }
        }.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteEntry(final EntryClass item) {
        return new Completable() {
            @Override
            protected void subscribeActual(CompletableObserver observer) {
//                dbTable.deleteItem(new Primitive(docId));
                dynamoDBMapper.delete(item);
                observer.onComplete();
            }
        }.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
