package Test;

import Core.Utils.*;
import com.jayway.jsonpath.JsonPath;
import org.apache.hc.core5.http.Header;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Core.Utils.YamlTool.*;

public class TestYaml
{

    @Test
    public void logTest2()
    {
        LoggerUtil.configInit();
        LoggerUtil.info("ddddddd");
    }

    @Test
    public void yamlToJson()
    {
        try
        {
            String yamlPath = "/Users/alex/Documents/MyProject/Java/CucumberAutomation/src/test/resources/YamlCases/The example2.yaml";
            String json = convertYamlToJson(yamlPath);
            System.out.println(json);
        }
        catch (IOException e)
        {
            System.out.println("fail to convert yaml file");
        }
    }

    @Test
    public void readYamlTest() throws IOException
    {
        String yamlPath = "/Users/alex/Documents/MyProject/Java/CucumberAutomation/src/test/resources/YamlCases/demoTest001.yaml";
        Map<String, Object> yamlData = readYaml(yamlPath);
        yamlData.forEach((k, v) -> System.out.println(k + ":" + v));
    }

    @Test
    public void yaml2Json() throws IOException
    {
        String yamlPath = "/Users/alex/Documents/MyProject/Java/CucumberAutomation/src/test/resources/YamlCases/demoTest001.yaml";
        String json = YamlTool.convertYamlToJson(yamlPath);
        System.out.println(json);
    }

    @Test
    public void readYamlValueTest() throws IOException
    {
        String yamlPath = "/Users/alex/Documents/MyProject/Java/CucumberAutomation/src/test/resources/YamlCases/Test001.yaml";
        Object value = readValueInYaml(yamlPath, "password");
        System.out.println(value);
    }

    @Test
    public void readYamlValueTest2() throws IOException
    {
        String yamlPath = "/Users/alex/Documents/MyProject/Java/CucumberAutomation/src/test/resources/YamlCases/Test001.yaml";
        Object value = YamlUtil.getValueByKey(yamlPath, "Step1", "password");
        System.out.println(value);
    }

    @Test
    public void randomUser()
    {
        for (int i = 0; i < 10; i++)
        {
            String mobile = GenerateUserInfo.genPhoneNum();
            String email = GenerateUserInfo.genEmail();
            String sex = GenerateUserInfo.genSex();
            String name = GenerateUserInfo.genChineseName(sex);
            String idNo = GenerateUserInfo.generateIDCard(20, 50, sex);
            System.out.println("sex: " + sex);
            System.out.println("name: " + name);
            System.out.println("mobile: " + mobile);
            System.out.println("email: " + email);
            System.out.println("idNO: " + idNo);
            System.out.println("********************************************");
        }

    }

    @Test
    public void test000022() {
        String sequence = GenerateUserInfo.generateSequence("男");
        System.out.println(sequence);
    }

    @Test
    public void dbResult() throws SQLException
    {
        String uri = "jdbc:mysql://localhost:3306/test";
        String dbName = "root";
        String dbPass = "12345678";
        Connection con = DBUtil.getConnection(uri, dbName, dbPass);
        String sql1 = "Select * from Student;";
        List<Map<String, Object>> resultList = DBUtil.executeQuery(con, sql1);
        System.out.println(resultList.isEmpty());
        resultList.forEach(System.out::println);
        Connection con2 = DBUtil.getConnection(uri, dbName, dbPass);

        String sql2 = "update Student set Sname='赵四' where SID=01";
        int result2 = DBUtil.executeUpdate(con2, sql2);
        System.out.println(result2);

    }

    @Test
    public void httpTest() throws Exception
    {
        String url = "https://api.uomg.com/api/rand.qinghua";
        HttpResult httpResult = HttpUtil.getInstance().doGet(url);
        System.out.println(httpResult.getCode());
        for (Header header : httpResult.getHeaders())
        {
            System.out.println(header);
        }
        System.out.println(httpResult.getContent());
    }


    @Test
    public void testWeixinGetToken() throws Exception
    {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
        Map<String, Object> params = new HashMap<>();
        params.put("corpid", "wwba399d9b250fee36");
        params.put("corpsecret", "HENIWYc4NWtrhLQAvmsTxYPIdTIKObvH2V5Oj7pFSfU");
        HttpResult httpResult = HttpUtil.getInstance().doGet(url, params);
        String responseBody = httpResult.getContent();
        System.out.println(httpResult.getCode());
        System.out.println(responseBody);
        String token = JsonPath.read(responseBody, "$.access_token");
        System.out.println(token);
        String url2 = "https://qyapi.weixin.qq.com/cgi-bin/agent/get";
        Map<String, Object> params2 = new HashMap<>();
        params2.put("access_token", token);
        params2.put("agentid", "1000002");
        HttpResult httpResult2 = HttpUtil.getInstance().doGet(url2, params2);
        String responseBody2 = httpResult2.getContent();
        System.out.println(httpResult2.getCode());
        System.out.println(responseBody2);
    }

    @Test
    public void testWeixinAddMenu() throws Exception
    {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
        Map<String, Object> params = new HashMap<>();
        params.put("corpid", "wwba399d9b250fee36");
        params.put("corpsecret", "HENIWYc4NWtrhLQAvmsTxYPIdTIKObvH2V5Oj7pFSfU");
        HttpResult httpResult = HttpUtil.getInstance().doGet(url, params);
        String responseBody = httpResult.getContent();
        String token = (String) JsonUtil.searchValueByKey(responseBody, "$.access_token");
        System.out.println(token);
        String url2 = "https://qyapi.weixin.qq.com/cgi-bin/menu/create";
        String json = "{\n" +
                "   \"button\":[\n" +
                "       {    \n" +
                "           \"type\":\"click\",\n" +
                "           \"name\":\"今日歌曲\",\n" +
                "           \"key\":\"V1001_TODAY_MUSIC\"\n" +
                "       },\n" +
                "       {\n" +
                "           \"name\":\"菜单\",\n" +
                "           \"sub_button\":[\n" +
                "               {\n" +
                "                   \"type\":\"view\",\n" +
                "                   \"name\":\"搜索\",\n" +
                "                   \"url\":\"http://www.soso.com/\"\n" +
                "               },\n" +
                "               {\n" +
                "                   \"type\":\"click\",\n" +
                "                   \"name\":\"赞一下我们\",\n" +
                "                   \"key\":\"V1001_GOOD\"\n" +
                "               }\n" +
                "           ]\n" +
                "      }\n" +
                "   ]\n" +
                "}";
        Map<String, Object> paramsPath = new HashMap<>();
        paramsPath.put("access_token", token);
        paramsPath.put("agentid", "1000002");
        HttpResult httpResult2 = HttpUtil.getInstance().postJson(url2, null, paramsPath, json);
    }


}
