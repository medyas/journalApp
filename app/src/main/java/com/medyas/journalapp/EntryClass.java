package com.medyas.journalapp;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.UUID;

@DynamoDBTable(tableName = "journal_entries")
public class EntryClass {

    private String clientUid, title, content, date, priority, docId;

    public EntryClass() {
    }

    public EntryClass(String clientUid, String title, String content, String date, String priority, String docId) {
        this.clientUid = clientUid;
        this.title = title;
        this.content = content;
        this.date = date;
        this.priority = priority;
        this.docId = docId;
    }

    public EntryClass(String clientUid, String title, String content, String date, String priority) {
        this.clientUid = clientUid;
        this.title = title;
        this.content = content;
        this.date = date;
        this.priority = priority;
        this.docId = UUID.randomUUID().toString();
    }

//    @DynamoDBRangeKey(attributeName = "clientUid")
    @DynamoDBAttribute(attributeName = "clientUid")
    public String getClientUid() {
        return clientUid;
    }

    public void setClientUid(String clientUid) {
        this.clientUid = clientUid;
    }

    @DynamoDBHashKey(attributeName = "docId")
    @DynamoDBAttribute(attributeName = "docId")
    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    @DynamoDBAttribute(attributeName = "entryTitle")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @DynamoDBAttribute(attributeName = "entryContent")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @DynamoDBAttribute(attributeName = "entryDate")
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @DynamoDBAttribute(attributeName = "entryPriority")
    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Drawable retrievePriorityImage(Resources res) {
        if(this.priority.equals("false")) return res.getDrawable(android.R.drawable.btn_star_big_off);
        else return res.getDrawable(android.R.drawable.btn_star_big_on);
    }
/*
    public Document getAsDocument() {
        Document doc = new Document();
        doc.put("clientUid", this.clientUid);
        doc.put("docId", this.docId);
        doc.put("entryTitle", this.title);
        doc.put("entryContent", this.content);
        doc.put("entryDate", this.date);
        doc.put("entryPriority", this.priority);
        return doc;
    }

    public static EntryClass parseDocument(Document doc) {
        if(doc != null) {
            return new EntryClass(doc.get("clientUid").asString(), doc.get("entryTitle").asString(), doc.get("entryContent").asString(), doc.get("entryDate").asString(), doc.get("entryPriority").asString(), doc.get("docId").asString());
        }
        return null;
    }*/
}
