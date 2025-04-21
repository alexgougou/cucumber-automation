package Core.Utils;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.connection.ConnectionPoolSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.concurrent.TimeUnit;

import static Core.EnvData.mongoDBAddress;
import static Core.Utils.Config.*;

public class MongoDBUtil
{
    private static final Logger logger = LogManager.getLogger(MongoDBUtil.class);
    private volatile static MongoDBUtil instance = null;

    private MongoDBUtil()
    {
    }

    public static MongoDBUtil getInstance()
    {
        if (instance == null)
        {
            synchronized (MongoDBUtil.class)
            {
                if (instance == null)
                {
                    instance = new MongoDBUtil();
                }
            }
        }
        return instance;
    }

    private volatile MongoClient mongoClient = null;

    private MongoClient getMongoClient()
    {
        if (mongoClient == null)
        {
            synchronized (MongoDBUtil.class)
            {
                if (mongoClient == null)
                {
                    ConnectionPoolSettings poolSettings = ConnectionPoolSettings.builder()
                            .maxSize(MONGODB_MAX_SIZE)
                            .minSize(MONGODB_MIN_SIZE)
                            .maxWaitTime(MONGODB_MAX_WAIT_TIME, TimeUnit.MILLISECONDS)
                            .maxConnecting(MONGODB_CONNECTING)
                            .build();
//                    String URI = "mongodb://" + mongoDBUserName + ":" + mongoDBPassword + "@" + mongoDBAddress;  //"mongodb://admin:pass@127.0.0.1:27017"
                    String URI = "mongodb://" + mongoDBAddress;  //"mongodb://localhost:27017"
//                    String URI = "mongodb://localhost:27017";  //"mongodb://localhost:27017"
                    MongoClientSettings settings = MongoClientSettings.builder()
                            .applyConnectionString(new ConnectionString(URI))
                            .applyToConnectionPoolSettings(builder ->
                            {
                                ConnectionPoolSettings settings1 = poolSettings;
                            })
                            .build();
                    mongoClient = MongoClients.create(settings);
                    System.out.println("connect mongoDB successfully");
                }
            }
        }
        return mongoClient;
    }

    public MongoDatabase getDB(String DBName)
    {
        MongoClient mongoClient = this.getMongoClient();
        return mongoClient.getDatabase(DBName);
    }

    public MongoCollection<Document> getCollection(String DBName, String collectionName)
    {
        MongoDatabase database = getDB(DBName);
        return database.getCollection(collectionName);
    }

    public void insertOne(String DBName, String collectionName, String key, Object value)
    {
        MongoCollection<Document> collection = getCollection(DBName, collectionName);
        Document doc = new Document()
                .append(key, value);
        collection.insertOne(doc);
    }

//    public void insertOne2(String DBName, String collectionName, String key, Object value)
//    {
//        MongoCollection<Document> collection = getCollection(DBName, collectionName);
//        Document doc = new Document(key, value);
//        collection.insertOne(doc);
//    }

    public Document findOne(String DBName, String collectionName, Bson filter)
    {
        MongoCollection<Document> collection = getCollection(DBName, collectionName);
        return collection.find(filter).first();
    }

//    public Object findByKey3(String DBName, String collectionName, String key)
//    {
//        MongoCollection<Document> collection = getCollection(DBName, collectionName);
//        FindIterable<Document> results = collection.find().projection(include(key));
//        results.forEach(System.out::println);
//        return results.first().get(key);
//    }
//
//    public Object findByKey2(String DBName, String collectionName, String key)
//    {
//        Object value = null;
//        MongoCollection<Document> collection = getCollection(DBName, collectionName);
//        Bson projection = Projections.fields(
//                Projections.include(key),
//                Projections.excludeId()
//        );
//        FindIterable<Document> results = collection.find().projection(projection);
//        results.forEach(System.out::println);
//        for (Document doc : results)
//        {
//            if (doc.containsKey(key))
//            {
//                value = doc.get(key);
//            }
//        }
//        return value;
//    }

    public Object findByKey(String DBName, String collectionName, String key)
    {
        Object value = null;
        MongoCollection<Document> collection = getCollection(DBName, collectionName);
        Bson filter = Filters.exists(key);
        Bson projection = Projections.fields(
                Projections.include(key),
                Projections.excludeId()
        );
        FindIterable<Document> results = collection.find(filter).projection(projection);
        results.forEach(System.out::println);
        for (Document doc : results)
        {
            if (doc.containsKey(key))
            {
                value = doc.get(key);
            }
        }
        return value;
    }

    public Object findOneByKey(String DBName, String collectionName, String key)
    {
        MongoCollection<Document> collection = getCollection(DBName, collectionName);
        Bson filter = Filters.exists(key);
        Bson projection = Projections.fields(
                Projections.include(key),
                Projections.excludeId()
        );
        Document result;
        result = collection.find(filter).projection(projection).first();
        if (null != result)
        {
            return result.get(key);
        }
        else return null;
    }

    /***
     * 字段存在则更新，不存在则插入
     * @param DBName
     * @param collectionName
     * @param key
     * @param value
     */
    public void updateOneByKey(String DBName, String collectionName, String key, Object value){
        MongoCollection<Document> collection = getCollection(DBName, collectionName);
        Bson filter = Filters.exists(key, true);
        Bson update = Updates.set(key, value);
        UpdateOptions options = new UpdateOptions().upsert(true);
        collection.updateOne(filter, update, options);
    }

}
