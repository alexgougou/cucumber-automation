package Core;

import Core.Utils.YamlTool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;

import static Core.StepData.stepInfo;
import static Core.Utils.Config.envPath;
import static Core.Utils.JsonUtil.getMapValueByLowerCase;
import static Core.Utils.YamlUtil.getYamlMap;

public class EnvData
{

    private static final Logger logger = LogManager.getLogger(EnvData.class);

    public static Map<String, Object> ServerInfo;

    public static String serverAddress;
    public static Map<String, Object> envInfo;

    private EnvData()
    {
        logger.info("Start to get Env Info");
        envInfo = getYamlMap(envPath);
        getServerInfo();
        getServerAddress();
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
}
