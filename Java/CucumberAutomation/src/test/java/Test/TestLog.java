package Test;

import Core.Utils.HttpResult;
import Core.Utils.HttpUtil;
import Core.Utils.PrettyReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.*;

import static Core.HttpHandler.getAPIInfo;
import static Core.Utils.JsonUtil.*;
import static Core.Utils.MapReplace.replaceMap;

public class TestLog
{
    private static final Logger logger = LogManager.getLogger(TestLog.class);
    @Test
    public void logTest()
    {
        logger.debug("debug");
        logger.info("info");
        logger.error("error");
        logger.warn("warn");
    }

    @Test
    public void genReport()
    {
        PrettyReport.genReport();
    }

    @Test
    public void testWeixinGetToken() throws Exception
    {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
        Map<String, Object> params = new HashMap<>();
        params.put("corpid", "wwba399d9b250fee36");
        List<String> aList = new ArrayList<>();
        params.put("corpsecret", "HENIWYc4NWtrhLQAvmsTxYPIdTIKObvH2V5Oj7pFSfU");
        HttpResult httpResult = HttpUtil.getInstance().doGet(url, params);
    }

    @Test
    public void test33() throws Exception
    {
        String json = "{\n" +
                "  \"button\" : [ {\n" +
                "    \"type\" : \"click\",\n" +
                "    \"name\" : \"今日歌曲\",\n" +
                "    \"key\" : \"V1001_TODAY_MUSIC\"\n" +
                "  }, {\n" +
                "    \"name\" : \"菜单\",\n" +
                "    \"sub_button\" : [ {\n" +
                "      \"type\" : \"view\",\n" +
                "      \"name\" : \"搜索\",\n" +
                "      \"url\" : \"http://www.soso.com/\"\n" +
                "    }, {\n" +
                "      \"type\" : \"click\",\n" +
                "      \"name\" : \"赞一下我们\",\n" +
                "      \"key\" : \"V1001_GOOD\"\n" +
                "    } ]\n" +
                "  } ]\n" +
                "}";
        String name = searchValueByKey2(json, "$.button.[1].sub_button.[1].name");
        System.out.println(name);

    }

    @Test
    public void test34() throws Exception
    {
        String json1 = "{\n" +
                "  \"button\" : [ {\n" +
                "    \"type\" : \"click\",\n" +
                "    \"name\" : \"今日歌曲\",\n" +
                "    \"key\" : \"V1001_TODAY_MUSIC\"\n" +
                "  }, {\n" +
                "    \"name\" : \"菜单\",\n" +
                "    \"sub_button\" : [ {\n" +
                "      \"type\" : \"view\",\n" +
                "      \"name\" : \"搜索\",\n" +
                "      \"url\" : \"http://www.soso.com/\"\n" +
                "    }, {\n" +
                "      \"type\" : \"click\",\n" +
                "      \"name\" : \"赞一下我们\",\n" +
                "      \"key\" : \"V1001_GOOD\"\n" +
                "    } ]\n" +
                "  } ]\n" +
                "}";
        String json2 = " {\n" +
                "  \"button\" : [ {\n" +
                "    \"type\" : \"click\",\n" +
                "    \"name\" : \"今日歌曲\",\n" +
                "    \"key\" : \"V1001_TODAY_MUSIC\"\n" +
                "  }, {\n" +
                "    \"name\" : \"菜单\",\n" +
                "    \"sub_button\" : [ {\n" +
                "      \"type\" : \"view\",\n" +
                "      \"name\" : \"百度一下\",\n" +
                "      \"url\" : \"http://www.baidu.com/\"\n" +
                "    }, {\n" +
                "      \"type\" : \"click\",\n" +
                "      \"name\" : \"赞一下我们\",\n" +
                "      \"key\" : \"V1001_GOOD\"\n" +
                "    } ]\n" +
                "  } ]\n" +
                "}";
        Map<Object, Object> sourceMap = json2Map(json1);
        Map<Object, Object> replaceMap = json2Map(json2);
        replaceMap(sourceMap, replaceMap);

    }

    @Test
    public void replace1() {
        String path = "/Users/alex/Documents/MyProject/Java/CucumberAutomation/src/test/resources/API/WeCom/ApplicationManagement/createMenu.yaml";
        LinkedHashMap<String, Object> apiInfo = getAPIInfo(path);
        Map<String, Object> requestParams = (Map<String, Object>) apiInfo.get("Params");
        System.out.println(requestParams);
        Map<String, Object> newParams = replaceAccordingJsonPath(requestParams, "$.button.[1].sub_button.[1].name", "zanzanz!");
        System.out.println(newParams);
    }


}
