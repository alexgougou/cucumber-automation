package Core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Map;

import static Core.Utils.Config.envPath;
import static Core.Utils.YamlUtil.getYamlMap;

public class EnvData
{

    private static final Logger logger = LogManager.getLogger(EnvData.class);
    public static Map<String, Object> envInfo;

    public static Map<String, Object> ServerInfo;

    public static Map<String, Object> mongoDBInfo;

    public static String mongoDBAddress;
    public static String mongoDBDataBaseName;

    public static String mongoDBUserName;
    public static String mongoDBPassword;

    public static String serverAddress;


    private EnvData()
    {
        logger.info("Start to get Env Info");
        envInfo = getYamlMap(envPath);
        getServerInfo();
        getServerAddress();
        getmongoDBInfo();
        getMongoDBAddress();
        getMongoDBDataBaseName();
        getMongoDBUserName();
        getMongoDBPassword();
    }

    private volatile static EnvData instance;

    public static EnvData getInstance()
    {
        if (instance == null)
        {
            synchronized (EnvData.class)
            {
                if (instance == null)
                {
                    instance = new EnvData();
                }
            }
        }
        return instance;
    }

    public Map<String, Object> getServerInfo() {
        if (envInfo.containsKey("Server"))
        {
            ServerInfo = (Map<String, Object>) envInfo.get("Server");
        }
        return ServerInfo;
    }

    public String getServerAddress() {
        try
        {
            serverAddress = (String) ServerInfo.get("address");
            return serverAddress;
        } catch (Exception  e) {
            logger.error("get server address failed", e);
        }
        return serverAddress;
    }

    public Map<String, Object> getmongoDBInfo() {
        if (envInfo.containsKey("MongoDB"))
        {
            mongoDBInfo = (Map<String, Object>) envInfo.get("MongoDB");
        }
        return mongoDBInfo;
    }


    public String getMongoDBAddress() {
        try
        {
            mongoDBAddress = (String) mongoDBInfo.get("address");
            return mongoDBAddress;
        } catch (Exception  e) {
            logger.error("get mongoDB address failed", e);
        }
        return mongoDBAddress;
    }

    public String getMongoDBDataBaseName() {
        try
        {
            mongoDBDataBaseName = (String) mongoDBInfo.get("dataBaseName");
            return mongoDBDataBaseName;
        } catch (Exception  e) {
            logger.error("get mongoDB data base name failed", e);
        }
        return mongoDBDataBaseName;
    }

    public String getMongoDBUserName() {
        try
        {
            mongoDBUserName = (String) mongoDBInfo.get("userName");
            return mongoDBUserName;
        } catch (Exception  e) {
            logger.error("get mongoDB user name failed", e);
        }
        return mongoDBUserName;
    }

    public String getMongoDBPassword() {
        try
        {
            mongoDBPassword = (String) mongoDBInfo.get("password");
            return mongoDBPassword;
        } catch (Exception  e) {
            logger.error("get mongoDB password failed", e);
        }
        return mongoDBPassword;
    }


}
