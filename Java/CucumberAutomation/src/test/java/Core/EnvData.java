package Core;

import Core.Utils.YamlTool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;

import static Core.Utils.Config.envPath;
import static Core.Utils.JsonUtil.getMapValueByLowerCase;

public class EnvData
{

    private static final Logger logger = LogManager.getLogger(EnvData.class);

    public static Map<String, Object> ServerInfo;

    public static String serverAddress;
    public static String envInfo;

    private EnvData()
    {
        try
        {
            logger.info("Start to get Env Info");
            envInfo = YamlTool.convertYamlToJson(envPath);
            getServerInfo();
            getServerAddress();
        }
        catch (IOException e)
        {
            logger.error("get Env Info failed: ", e);
        }
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
        ServerInfo = getMapValueByLowerCase(envInfo, "$.server");
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
