package Core.Utils;

import java.util.HashMap;
import java.util.Map;

public  class Config
{
    public static String projectPath = System.getProperty("user.dir");
    public static String envPath = projectPath + "/src/test/resources/Env/UAT1.yaml";

    public static String scenarioDir = projectPath + "/src/test/resources/YamlCases/";

    public static String APIDir = projectPath + "/src/test/resources/API/";


    //DB pool config
    public static final int DB_POOL_INITIAL_SIZE = 1;
    public static final int DB_POOL_MAX_ACTIVE = 10;
    public static final int DB_POOL_MIN_IDLE = 3;
    public static final int DB_POOL_MAX_IDLE = 5;
    public static final long DB_POOL_MAX_WAIT = 60000;

    //http pool config
    public static final int HTTP_POOL_MAX_TOTAL = 100;
    public static final int HTTP_POOL_PER_ROUTE_MAX_SIZE = 200;

    public static final long HTTP_POOL_DEFAULT_TIMEOUT = 3000;
    public static final long HTTP_POOL_WAIT_TIME = 1000;

    //建立连接后，读取数据的timeout
    public static final int SOCKET_TIMEOUT = 2;
    //客户端和服务器建立连接的timeout
    public static final long CONNECTION_TIMEOUT = 1000;
    //连接池获取连接的timeout时间
    public static final long CONNECTION_REQUEST_TIMEOUT = 1000;

    public static final int HTTP_RETRY_TIME = 3;
    public static final String CHAR_SET = "UTF-8";

    public static final Map<String, String> DEFAULT_HEADER = new HashMap<String, String>(){{
        put("Content-Type", "text/plain; charset=UTF-8");
        put("", "");
    }};
}
