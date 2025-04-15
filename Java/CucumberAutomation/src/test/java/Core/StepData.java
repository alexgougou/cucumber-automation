package Core;

import io.cucumber.java.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Map;
import static CucumberAutomation.Hooks.scenarioData;
import static CucumberAutomation.Hooks.stepStatus;


public class StepData
{
    public static Map<String, Object> stepInfo;

    public static Map<String, Object> needReplacedParams;    //caseInfo 中replaceParams下的参数

    public static Map<String, Object> needReplacedUrlParams;    //caseInfo 中replaceUrlParams下的参数

    public static Map<String, Object> saveData;    //caseInfo 中SaveData下的参数
    public static Map<String, Object> verifyData;   //caseInfo 中Verify下的参数


    private static final Logger logger = LogManager.getLogger(StepData.class);

    public static void getStepStatus(String stepIndex)
    {
        if (stepStatus != null || stepStatus.get(stepStatus.size() - 1) != Status.FAILED)
        {
            logger.info("last step is passed, start to next step");
            logger.info("***************************************step index:{}***************************************", stepIndex);
        }
    }

    public static Map<String, Object> getStepInfo(String stepName)
    {
        if (scenarioData.containsKey(stepName))
        {
            stepInfo = (Map<String, Object>) scenarioData.get(stepName);
        } else {
            logger.error("step not exist!!!");
        }
        return stepInfo;
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
}
