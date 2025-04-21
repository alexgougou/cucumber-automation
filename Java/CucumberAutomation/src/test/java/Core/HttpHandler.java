package Core;

import Core.Utils.HttpResult;
import Core.Utils.HttpUtil;
import Core.Utils.MongoDBUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static Core.EnvData.serverAddress;
import static Core.StepData.*;
import static Core.Utils.Config.APIDir;
import static Core.Utils.JsonUtil.*;
import static Core.Utils.YamlUtil.getYamlMap;
import static CucumberAutomation.Hooks.publicParams;
import static CucumberAutomation.Hooks.scenarioData;

public class HttpHandler
{
    public static String APIFileAbsolutePath;
    public static String requestPath;

    public static String url;
    public static String requestMethod;

    public static Map<String, String> requestHeaders;

    public static Map<String, Object> requestParams;

    public static Map<String, Object> getRequestParams;

    public static Map<String, Object> postRequestParams;

    public static Map<String, Object> requestUrlParams;

    public static Map<String, ReplaceRequestUrlParamsStrategy> replaceRequestUrlParamsStrategies =
            Map.of("fromPublicParams", new ReplaceRequestUrlParamsInPublicParams(), "fromScenarioData", new ReplaceRequestUrlParamsInScenarioData(), "fromMongoDB", new ReplaceRequestUrlParamsInMongoDB());
    public static Map<String, ReplaceRequestParamsStrategy> replaceRequestParamsStrategies =
            Map.of("fromPublicParams", new ReplaceRequestParamsInPublicParams(), "fromScenarioData", new ReplaceRequestParamsInScenarioData());
    public static Map<String, SaveDataStrategy> saveDataStrategies =
            Map.of("saveToScenarioData", new SaveToScenarioData(), "saveToMongoDB", new SaveToMongoDB());


    public static HttpResult httpResult;


    private static final Logger logger = LogManager.getLogger(HttpHandler.class);

    public static String getAPIAbsolutePath(Map<String, Object> stepInfo)
    {
        APIFileAbsolutePath = APIDir + getAPIFilePath(stepInfo) + ".yaml";
        return APIFileAbsolutePath;
    }

    public static LinkedHashMap<String, Object> getAPIInfo(String apiPath)
    {
        LinkedHashMap<String, Object> apiInfo = null;
        try
        {
            apiInfo = getYamlMap(apiPath);
            return apiInfo;
        }
        catch (Exception e)
        {
            logger.error("get API Info failed: ", e);
            return apiInfo;
        }
    }

    public static String getRequestPath(LinkedHashMap<String, Object> apiInfo)
    {
        if (apiInfo.containsKey("Path"))
        {
            requestPath = (String) apiInfo.get("Path");
        }
        return requestPath;
    }

    public static String getRequestUrl()
    {
        url = serverAddress + requestPath;
        return url;
    }

    public static String getRequestMethod(LinkedHashMap<String, Object> apiInfo)
    {
        if (apiInfo.containsKey("Method"))
        {
            requestMethod = (String) apiInfo.get("Method");
        }
        return requestMethod;
    }

    public static boolean isGetMethod()
    {
        return requestMethod.toLowerCase().equals("get");
    }

    public static boolean isPostMethod()
    {
        return requestMethod.toLowerCase().equals("post");
    }

    public static Map<String, String> getRequestHeaders(LinkedHashMap<String, Object> apiInfo)
    {
        if (apiInfo.containsKey("Headers"))
        {
            requestHeaders = (Map<String, String>) apiInfo.get("Headers");
        }
        return requestHeaders;
    }


    public static boolean isXml()
    {
        String contentType = requestHeaders.get("Content-Type");
        return contentType.toLowerCase().contains("application/xml");
    }

    public static boolean isJson()
    {
        String contentType = requestHeaders.get("Content-Type");
        return contentType.toLowerCase().contains("application/json");
    }


    public static void getRequestParams(LinkedHashMap<String, Object> apiInfo)
    {
        if (apiInfo.containsKey("Params"))
        {
            requestParams = (Map<String, Object>) apiInfo.get("Params");
            if (isGetMethod())
            {
                getRequestParams = (Map<String, Object>) apiInfo.get("Params");
            }
            else if (isPostMethod())
            {
                postRequestParams = (Map<String, Object>) apiInfo.get("Params");
            }
        }
    }


    public static Map<String, Object> getRequestUrlParams(LinkedHashMap<String, Object> apiInfo)
    {
        if (apiInfo.containsKey("URLParams"))
        {
            requestUrlParams = (Map<String, Object>) apiInfo.get("URLParams");
        }
        return requestUrlParams;
    }

    /***
     * 读取request的参数，包括request的path，method，header，params，URLParams， 拼接url
     */
    public static void requestDataInit()
    {
        String APIFilePath = getAPIAbsolutePath(stepInfo);
        LinkedHashMap<String, Object> APIInfo = getAPIInfo(APIFilePath);
        getRequestPath(APIInfo);
        getRequestUrl();
        getRequestMethod(APIInfo);
        getRequestHeaders(APIInfo);
        getRequestParams(APIInfo);
        getRequestUrlParams(APIInfo);
    }

    public static void httpStepPerform()
    {
        requestDataInit();
        replaceUrlParams();
        replaceHeaders();
        replaceRequestParams();
        sendHttpRequest();
        saveHttpResultData();
    }


    /***
     *
     * @param
     */

    public static void replaceUrlParams()
    {
        if (needReplacedUrlParams != null)
        {
            for (Map.Entry<String, Object> entry : needReplacedUrlParams.entrySet())
            {
                String key = entry.getKey();
                ArrayList<String> valueList = (ArrayList<String>) entry.getValue();
                String replaceSource = valueList.get(0).toLowerCase();
                String dataSourceKey = valueList.get(1);

                switch (replaceSource)
                {
                    case "frompublicparams":
                        replaceRequestUrlParamsStrategies.get("fromPublicParams").perform(requestUrlParams, key, dataSourceKey);
                        break;
                    case "fromscenariodata":
                        replaceRequestUrlParamsStrategies.get("fromScenarioData").perform(requestUrlParams, key, dataSourceKey);
                        break;
                    case "frommongodb":
                        replaceRequestUrlParamsStrategies.get("fromMongoDB").perform(requestUrlParams, key, dataSourceKey);
                        break;
                    default:
                }
            }

        }

    }

    public static void replaceHeaders()
    {
        if (needReplacedHeaders != null)
        {
            for (Map.Entry<String, Object> entry : needReplacedHeaders.entrySet())
            {
                String key = entry.getKey();
                ArrayList<String> valueList = (ArrayList<String>) entry.getValue();
                String replaceSource = valueList.get(0).toLowerCase();
                String dataSourceKey = valueList.get(1);
                if (key.equalsIgnoreCase("authorization"))
                {
                    //add token to header
                    switch (replaceSource)
                    {
                        case "fromscenariodata":
                            requestHeaders.put("Authorization", "Bearer " + scenarioData.get(dataSourceKey));
                            break;
                        case "frommongodb":
                            requestHeaders.put("Authorization", "Bearer " + getValueInMongoDB(dataSourceKey));
                            break;
                        case "fromredis":
                            //todo
                            break;
                        default:
                    }
                }
                else
                {
                    switch (replaceSource)
                    {
                        case "frompublicparams":
                            requestHeaders.put(key, (String) publicParams.get(dataSourceKey));
                            break;
                        case "fromscenariodata":
                            requestHeaders.put(key, (String) scenarioData.get(dataSourceKey));
                            break;
                        default:
                    }
                }
            }

        }
    }


    public static void replaceRequestParams()
    {
        if (needReplacedParams != null)
        {
            for (Map.Entry<String, Object> entry : needReplacedParams.entrySet())
            {
                String key = entry.getKey();
                ArrayList<String> valueList = (ArrayList<String>) entry.getValue();
                String replaceSource = valueList.get(0).toLowerCase();
                String dataSourceKey = valueList.get(1);
                String jsonPath = valueList.get(2);
                if (isGetMethod())
                {
                    switch (replaceSource)
                    {
                        case "frompublicparams":
                            getRequestParams = replaceRequestParamsStrategies.get("fromPublicParams").perform(getRequestParams, jsonPath, dataSourceKey);
                            break;
                        case "fromscenariodata":
                            getRequestParams = replaceRequestParamsStrategies.get("fromScenarioData").perform(getRequestParams, jsonPath, dataSourceKey);
                            break;
                        default:
                    }
                }
                else if (isPostMethod())
                {
                    switch (replaceSource)
                    {
                        case "frompublicparams":
                            postRequestParams = replaceRequestParamsStrategies.get("fromPublicParams").perform(postRequestParams, jsonPath, dataSourceKey);
                            break;
                        case "fromscenariodata":
                            postRequestParams = replaceRequestParamsStrategies.get("fromScenarioData").perform(postRequestParams, jsonPath, dataSourceKey);
                            break;
                        default:
                    }
                }
                else
                {
                    //暂时未做测试
                    switch (replaceSource)
                    {
                        case "frompublicparams":
                            replaceRequestParamsStrategies.get("fromPublicParams").perform(requestParams, jsonPath, dataSourceKey);
                            break;
                        case "fromscenariodata":
                            replaceRequestParamsStrategies.get("fromScenarioData").perform(requestParams, jsonPath, dataSourceKey);
                            break;
                        default:
                    }
                }
            }
        }
    }

    public static void sendHttpRequest()
    {
        if (isGetMethod())
        {
            httpResult = HttpUtil.getInstance().doGet(url, requestHeaders, getRequestParams);
        }
        else if (isPostMethod())
        {
            if (isJson())
            {
                String json = map2Json(postRequestParams);
                httpResult = HttpUtil.getInstance().postJson(url, requestHeaders, requestUrlParams, json);

            }
            else if (isXml())
            {
                String xml = map2xml(postRequestParams);
                httpResult = HttpUtil.getInstance().postXml(url, requestHeaders, requestUrlParams, xml);

            }
            else
            {
                httpResult = HttpUtil.getInstance().doPost(url, requestHeaders, requestUrlParams, postRequestParams);
            }
        }
    }

    /***
     * 保存http response的data
     * 保存地址为scenarioData
     */
    public static void saveHttpResultData()
    {
        if (saveData != null)
        {
            logger.info("start to save data");
            for (Map.Entry<String, Object> entry : saveData.entrySet())
            {
                ArrayList<String> valueList = (ArrayList<String>) entry.getValue();

                String saveDataSource = valueList.get(0).toLowerCase();
                String dataSourceKey = valueList.get(1);
                String jsonPath = valueList.get(2);
                Object saveValue = searchValueByKey2(httpResult.getContent(), jsonPath);
                switch (saveDataSource)
                {
                    case "savetoscenariodata":
                        saveDataStrategies.get("saveToScenarioData").save(dataSourceKey, saveValue);
                        break;
                    case "savetomongodb":
                        saveDataStrategies.get("saveToMongoDB").save(dataSourceKey, saveValue);
                        break;
                    default:
                }
            }
        }
    }


    public static void verifyHttpResult()
    {
        if (verifyData != null)
        {
            logger.info("start to verify http result");
            for (Map.Entry<String, Object> entry : verifyData.entrySet())
            {
                String key = entry.getKey();

                if (key.toLowerCase().equals("statuscode")) //校验statusCode
                {
                    Object expectedValue = entry.getValue();
                    Assertions.assertEquals(expectedValue, httpResult.getCode());
                }
                else
                {
                    ArrayList<String> valueList = (ArrayList<String>) entry.getValue();
                    String jsonPath = valueList.get(0);
                    String verifyType = valueList.get(1).toLowerCase();
                    String expectedValue = valueList.get(2);
                    Object actualValue = searchValueByKey2(httpResult.getContent(), jsonPath);
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
    }


}
