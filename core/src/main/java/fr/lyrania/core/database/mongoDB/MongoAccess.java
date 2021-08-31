package fr.lyrania.core.database.mongoDB;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.Collections;

public class MongoAccess {

    public static final MongoAccess INSTANCE = new MongoAccess();

    private MongoClient mongoClient;
    private MongoDatabase database;

    private MongoAccess() {

    }

    public void initConnection(String host, int port, String username, String password, String database) throws Exception {
        this.mongoClient = new MongoClient(
                new MongoClientURI("mongodb://" + username + ":" + password + "@" + host + ":" + port)
        );
        this.database = this.mongoClient.getDatabase(database);
    }

    public Document getDocumentFrom(String collectionName, String id) {
        MongoCollection<Document> collection = this.database.getCollection(collectionName);
        return collection.find(Filters.eq("_id", id)).limit(1).first();
    }

    public boolean hasDocument(String collectionName, String id) {
        MongoCollection<Document> collection = this.database.getCollection(collectionName);
        return collection.countDocuments(Filters.eq("_id", id)) >= 1;
    }

    public void setDocumentIn(String collectionName, String id, Document document) {
        MongoCollection<Document> collection = this.database.getCollection(collectionName);
        if (this.hasDocument(collectionName, id)) {
            collection.replaceOne(Filters.eq("_id", id), document);
        } else {
            collection.insertOne(document);
        }
    }

    public void deleteDocument(String collectionName, String id) {
        MongoCollection<Document> collection = this.database.getCollection(collectionName);
        collection.deleteOne(Filters.eq("_id", id));
    }

    public void endConnection() {
        this.mongoClient.close();
    }
}
