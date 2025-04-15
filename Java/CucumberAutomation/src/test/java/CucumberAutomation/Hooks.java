package CucumberAutomation;

import Core.EnvData;
import Core.Utils.RegexUtil;
import io.cucumber.java.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static Core.Utils.Config.scenarioDir;
import static Core.Utils.YamlUtil.getYamlMap;

public class Hooks
{
    public Scenario scenario;
    public String scenarioName;
    public ArrayList<String> scenarioTags;
    public static ArrayList<Status> stepStatus;

    public static LinkedHashMap<String, Object> scenarioData;
    public static Map<String, String> publicParams;
    private static final Logger logger = LogManager.getLogger(Hooks.class);

    /***
     * 通过找到@CASE_INFO的tag来获取scenario的文件位置，再读取文件内容赋值给scenarioData
     * @param tags
     */
    public void getScenarioInfo(ArrayList<String> tags)
    {
        for (String tag : tags)
        {
            if (tag.startsWith("@CASE_INFO"))
            {
                String caseConfigName = RegexUtil.extractContentInBrackets(tag);
                String scenarioPath = scenarioDir + caseConfigName + ".yaml";
                scenarioData = getYamlMap(scenarioPath);
            }
        }
    }

    public Map<String, String> getPublicParams()
    {
        if (scenarioData.containsKey("PublicParams"))
        {
            publicParams = (Map<String, String>) scenarioData.get("PublicParams");
        }
        return publicParams;
    }

    /***
     * scenario（case）运行之前，获取scenario的信息
     * @param currentScenario
     */
    @Before
    public void beforeScenario(Scenario currentScenario)
    {
        this.scenario = currentScenario;
        stepStatus = new ArrayList<>();
        scenarioName = scenario.getName();
        scenarioTags = (ArrayList<String>) scenario.getSourceTagNames();
        getScenarioInfo(scenarioTags);
        getPublicParams();
        logger.info("***************************************Start to run the scenario: \"" + scenarioName + "\"***************************************");
//        logger.info("env data:\n" + envData);
//        logger.info("scenarioData:\n" + scenarioData);
//        logger.info("publicParams:\n" + publicParams);

    }

    @After
    public void afterScenario()
    {
        logger.info("***************************************The scenario: \"" + scenarioName + "\" run complete***************************************");
    }

    /***
     * 整个suite运行之前，获取测试环境的信息，如服务器地址，数据库地址等
     */
    @BeforeAll()
    public static void beforeSuite()
    {
//        logger.info("*******************************Before All*******************************");
        EnvData.getInstance();
    }

    @AfterAll
    public static void afterSuite()
    {
//        logger.info("*******************************After All*******************************");
    }

    @BeforeStep
    public void beforeStep()
    {
//        logger.info("***********************Before Step***********************");
    }

    @AfterStep
    public void afterStep()
    {
        if (scenario.isFailed()) {
            stepStatus.add(Status.FAILED);
        }
        else {
            stepStatus.add(Status.PASSED);
        }
    }
}
