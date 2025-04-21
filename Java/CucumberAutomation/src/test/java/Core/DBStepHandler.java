package Core;

import Core.Utils.DBUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static Core.EnvData.envInfo;
import static Core.StepData.*;
import static Core.Utils.DBUtil.executeQuery;
import static Core.Utils.DBUtil.executeUpdate;
import static CucumberAutomation.Hooks.publicParams;
import static CucumberAutomation.Hooks.scenarioData;

public class DBStepHandler
{
    public static Map<String, String> DBDetails;

    public static String DBType;
    public static String DBAddress;
    public static String DBUserName;
    public static String DBPassWord;
    public static String DBServiceName;
    public static String DBSID;
    public static String DBUrl;

    public static List<Map<String, Object>> DBQueryResult;
    public static int DBUpdateResult;


    private static final Logger logger = LogManager.getLogger(DBStepHandler.class);


    public static Map<String, String> getDBDetails()
    {
        if (envInfo.containsKey(DBName))
        {
            DBDetails = (Map<String, String>) envInfo.get(DBName);
        }
        else
        {
            logger.error("Can not found DB: {} details in env data", DBName);
        }
        return DBDetails;
    }

    public static String getDBType()
    {
        if (DBDetails.containsKey("DBType"))
        {
            DBType = DBDetails.get("DBType");
        }
        else
        {
            logger.error("Can not found DB type in env data, please check env file");
        }
        return DBType;
    }


    public static String getDBAddress()
    {
        if (DBDetails.containsKey("address"))
        {
            DBAddress = DBDetails.get("address");
        }
        else
        {
            logger.error("Can not found DB address in env data, please check env file");
        }
        return DBAddress;
    }

    public static String getDBUserName()
    {
        if (DBDetails.containsKey("userName"))
        {
            DBUserName = DBDetails.get("userName");
        }
        else
        {
            logger.error("Can not found DB userName in env data, please check env file");
        }
        return DBUserName;
    }

    public static String getDBPassWord()
    {
        if (DBDetails.containsKey("password"))
        {
            DBPassWord = DBDetails.get("password");
        }
        else
        {
            logger.error("Can not found DB password in env data, please check env file");
        }
        return DBPassWord;
    }

    public static String getDBServiceName()
    {
        if (DBDetails.containsKey("serviceName"))
        {
            DBServiceName = DBDetails.get("serviceName");
        }
        return DBServiceName;
    }

    public static String getDBSID()
    {
        if (DBDetails.containsKey("SID"))
        {
            DBSID = DBDetails.get("SID");
        }
        return DBSID;
    }

    public static boolean isMysql()
    {
        return DBType.equalsIgnoreCase("mysql");
    }

    public static boolean isOracle()
    {
        return DBType.equalsIgnoreCase("oracle");
    }

    public static String buildDBUrl()
    {
        if (isMysql())
        {
            DBUrl = "jdbc:mysql://" + DBAddress;
        }
        else if (isOracle())
        {
            if (null != DBServiceName)
            {
                //serviceName 方式拼装url
                DBUrl = "jdbc:oracle:thin:@//" + DBAddress + "/" + DBServiceName;
            }
            else
            {
                //sid 方式拼装url
                DBUrl = "jdbc:oracle:thin:@" + DBAddress + ":" + DBSID;
            }
        }
        return DBUrl;
    }

    public static void DBDataInit()
    {
        getDBDetails();
        getDBAddress();
        getDBType();
        getDBUserName();
        getDBPassWord();
        getDBServiceName();
        getDBSID();
        buildDBUrl();
    }

    public static void DBStepPerform()
    {
        DBDataInit();
        replaceSQLParams();
        executeDBDML();
    }


    public static void replaceSQLParams()
    {
        if (SQLParams != null)
        {
            for (Map.Entry<String, Object> entry : SQLParams.entrySet())
            {
                String key = entry.getKey();
                ArrayList<String> valueList = (ArrayList<String>) entry.getValue();
                String replaceSource = valueList.get(0).toLowerCase();
                String dataSourceKey = valueList.get(1);
                String replaceKey = valueList.get(2);
                Object replaceValue;
                switch (replaceSource)
                {
                    case "frompublicparams":
                        replaceValue = publicParams.get(dataSourceKey);
                        if (replaceValue instanceof String)
                        {
                            SQLStatement = SQLStatement.replace("${" + replaceKey + "}", "\'" + replaceValue + "\'");
                        }
                        else
                        {
                            SQLStatement = SQLStatement.replace("${" + replaceKey + "}", replaceValue.toString());
                        }
                        break;
                    case "fromscenariodata":
                        replaceValue = scenarioData.get(dataSourceKey);
                        if (replaceValue instanceof String)
                        {
                            SQLStatement = SQLStatement.replace("${" + replaceKey + "}", "\'" + replaceValue + "\'");

                        }
                        else
                        {
                            SQLStatement = SQLStatement.replace("${" + replaceKey + "}", replaceValue.toString());
                        }
                        break;
                    default:
                }

            }
        }
    }

    public static void executeDBDML()
    {
        Connection con = DBUtil.getConnection(DBUrl, DBUserName, DBPassWord);
        if (SQLStatement.toLowerCase().startsWith("select"))
        {
            //select 语句
            logger.info("execute SQL: {}", SQLStatement);
            DBQueryResult = executeQuery(con, SQLStatement);
        }
        else
        {
            //update, insert, delete语句
            logger.info("execute SQL: {}", SQLStatement);
            DBUpdateResult = executeUpdate(con, SQLStatement);
        }
    }


    public static void verifyDBResult()
    {
        if (verifyData != null)
        {
            logger.info("start to verify DB result");
            if (SQLStatement.toLowerCase().startsWith("select"))
            {
                //判断query结果是否为空
                if (DBQueryResult.isEmpty())
                {
                    logger.error("DB query result is empty, please check the sql");
                    setStepFailed();
                }
                //判断query结果是否只有一条记录
                else if (DBQueryResult.size() > 1)
                {
                    logger.error("DB query result is not unique, result size is : {}", DBQueryResult.size());
                    setStepFailed();
                }
                //query记录唯一，开始正常校验
                else
                {
                    for (Map.Entry<String, Object> entry : verifyData.entrySet())
                    {
                        String key = entry.getKey();
                        ArrayList<String> valueList = (ArrayList<String>) entry.getValue();
                        String verifyType = valueList.get(0).toLowerCase();
                        String expectedValue = valueList.get(1);
                        Object actualValue = DBQueryResult.get(0).get(key);
                        switch (verifyType)
                        {
                            case "int":
                                Assertions.assertEquals(Integer.valueOf(expectedValue), actualValue);
                                break;
                            case "double":
                                Assertions.assertEquals(Double.valueOf(expectedValue), actualValue);
                                break;
                            case "bigdecimal":
                                Assertions.assertEquals(new BigDecimal(expectedValue), actualValue);
                                break;
                            case "regex":
                                Assertions.assertTrue(Pattern.compile(expectedValue).matcher((String) actualValue).matches());
                                break;
                            case "string":
                                Assertions.assertEquals(expectedValue, actualValue.toString());
                                break;
                            default:
                        }
                    }
                }
            }
            //update语句
            else
            {
                ArrayList<String> valueList = (ArrayList<String>) verifyData.get("success");
                String verifyType = valueList.get(0).toLowerCase();
                int expectedValue = Integer.valueOf(valueList.get(1));
                int actualValue = DBUpdateResult;
                if (!verifyType.equalsIgnoreCase("int")) {
                    logger.error("update result type is not correct, only can be int");
                    setStepFailed();
                } else {
                    Assertions.assertEquals(expectedValue, actualValue);
                }
            }


        }
    }


}
