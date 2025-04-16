package Core;

import io.cucumber.java.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;

import java.util.Map;

import static CucumberAutomation.Hooks.*;


public class StepData
{
    public static Map<String, Object> stepInfo;
    public static String DBName;


    public static Map<String, Object> needReplacedParams;    //caseInfo 中replaceParams下的参数

    public static Map<String, Object> needReplacedUrlParams;    //caseInfo 中replaceUrlParams下的参数

    public static Map<String, Object> saveData;    //caseInfo 中SaveData下的参数
    public static Map<String, Object> verifyData;   //caseInfo 中Verify下的参数
    public static String SQLStatement;   //caseInfo 中sql
    public static Map<String, Object> SQLParams;   //caseInfo 中SQLParams


    private static final Logger logger = LogManager.getLogger(StepData.class);

    public static void getStepStatus(String stepIndex)
    {
        if (stepStatus != null || stepStatus.get(stepStatus.size() - 1) != Status.FAILED)
        {
            logger.info("last step is passed, start to next step");
            logger.info("***************************************step index:{}***************************************", stepIndex);
        }
    }

    public static void setStepFailed()
    {
        stepStatus.add(Status.FAILED);
    }

    public static Map<String, Object> getStepInfo(String stepName)
    {
        if (scenarioData.containsKey(stepName))
        {
            stepInfo = (Map<String, Object>) scenarioData.get(stepName);
        }
        else
        {
            logger.error("step not exist!!!");
        }
        return stepInfo;
    }

    public static String getDBName()
    {
        if (stepInfo.containsKey("DBName"))
        {
            DBName = (String) stepInfo.get("DBName");
        }
        return DBName;
    }

    public static String getAPIFilePath(Map<String, Object> stepInfo)
    {
        String filePath = "";
        try
        {
            filePath = (String) stepInfo.get("APIFile");
        }
        catch (ClassCastException e)
        {
            e.printStackTrace();
        }
        return filePath;
    }

    public static Map<String, Object> getReplaceParams()
    {

        if (stepInfo.containsKey("replaceParams"))
        {
            needReplacedParams = (Map<String, Object>) stepInfo.get("replaceParams");
        }
        return needReplacedParams;
    }

    public static Map<String, Object> getReplaceUrlParams()
    {

        if (stepInfo.containsKey("replaceUrlParams"))
        {
            needReplacedUrlParams = (Map<String, Object>) stepInfo.get("replaceUrlParams");
        }
        return needReplacedUrlParams;
    }

    public static String getSQLStatement()
    {

        if (stepInfo.containsKey("SQL"))
        {
            SQLStatement = (String) stepInfo.get("SQL");
        }
        return SQLStatement;
    }

    public static Map<String, Object> getSQLParams()
    {

        if (stepInfo.containsKey("SQLParams"))
        {
            SQLParams = (Map<String, Object>) stepInfo.get("SQLParams");
        }
        return SQLParams;
    }

    public static Map<String, Object> getSaveData()
    {

        if (stepInfo.containsKey("SaveData"))
        {
            saveData = (Map<String, Object>) stepInfo.get("SaveData");
        }
        return saveData;
    }

    public static Map<String, Object> getVerifyData()
    {

        if (stepInfo.containsKey("Verify"))
        {
            verifyData = (Map<String, Object>) stepInfo.get("Verify");
        }
        return verifyData;
    }

    /***
     * http类step的初始化
     * @param stepName
     */
    public static void httpStepInit(String stepName)
    {
        stepInfo = getStepInfo(stepName);
        getSaveData();
        getVerifyData();
        getReplaceParams();
        getReplaceUrlParams();

    }

    public static void DBStepInit(String stepName)
    {
        stepInfo = getStepInfo(stepName);
        getDBName();
        getSQLParams();
        getSQLStatement();
        getSaveData();
        getVerifyData();
    }
}
