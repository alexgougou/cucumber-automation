package Core.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.jayway.jsonpath.JsonPath;
import org.apache.hc.core5.http.Header;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonUtil
{
    private static final Logger logger = LogManager.getLogger(JsonUtil.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final XmlMapper xmlMapper = new XmlMapper();


    public static String headers2Json(Header[] headers)
    {
        ObjectNode jsonNodes = mapper.createObjectNode();
        for (Header header : headers)
        {
            jsonNodes.put(header.getName(), header.getValue());
        }
        try
        {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNodes);
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String prettyJson(String jsonString)
    {
        try
        {
            Object json = mapper.readTree(jsonString);

            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String Key2LowerCase(String jsonString)
    {
        String resultJson = "";
        try
        {
            JsonNode jsonNode = mapper.readTree(jsonString);
            Map<String, Object> lowerCaseMap = convertKeysToLowerCase(jsonNode);
            resultJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(lowerCaseMap);
            return resultJson;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultJson;
    }

    public static Map<String, Object> convertKeysToLowerCase(JsonNode jsonNode) {
        Map<String, Object> result = new HashMap<>();
        jsonNode.fields().forEachRemaining(entry->{
            String lowerCaseKey = entry.getKey().toLowerCase();
            result.put(lowerCaseKey, entry.getValue());
        });
        return result;
    }

    public static Object searchValueByKey(String json, String keyPath){
        String value = "";
        try
        {
            value = JsonPath.read(json, keyPath);

        } catch (Exception e) {
            logger.error("No results for jsonPath:", e);
        }

        return value;
    }

    public static <T> T searchValueByKey2(String json, String keyPath){
        return JsonPath.read(json, keyPath);
    }

    public static String replaceValueByJsonPath (String json, String keyPath, Object replaceValue){
        String newJson = "";
        try
        {
            newJson = JsonPath.parse(json)
                    .set(keyPath, replaceValue)
                    .jsonString();
        }
        catch (Exception e)
        {
            logger.error("replace value by jsonPath failed", e);
        }
        return newJson;
    }



    public static Object getStringValueByLowerCase(String json, String keyPath){
        String value = "";
        try
        {
            String lowerCaseJson = Key2LowerCase(json);
            logger.info(lowerCaseJson);
            value = JsonPath.read(lowerCaseJson, keyPath);
            return value;
        } catch (Exception e) {
            logger.error("No results for jsonPath: {},{}", keyPath, e);
        }

        return value;
    }

    public static Map<String, Object> getMapValueByLowerCase(String json, String keyPath){
        Map<String, Object> result = new LinkedHashMap<>();
        try
        {
            String lowerCaseJson = Key2LowerCase(json);
            result = JsonPath.read(lowerCaseJson, keyPath);
            return result;
        } catch (Exception e) {
            logger.error("No results for jsonPath: {},{}", keyPath, e);
        }

        return result;
    }

    public static String map2Json(Map<String, Object> map) {
        String result = "";
        try
        {
            result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
            return result;
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
            return result;
        }
    }

    public static <T> T json2Map(String jsonString)
    {
        try
        {
            return (T) mapper.readValue(jsonString, Map.class);
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static <T> T json2Map2(String jsonString)
    {
        try
        {
            return (T) mapper.readValue(jsonString, Map.class);
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String map2xml(Map<String, Object> map) {
        String result = "";
        try
        {
            result = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
            return result;
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
            return result;
        }
    }


    public static String replaceValueAccordingJsonPath(String json, String jsonPath, Object value) {
        return JsonPath.parse(json).set(jsonPath, value).jsonString();
    }

    public static <T> T replaceAccordingJsonPath(Map<String, Object> sourceMap, String jsonPath, Object value){
        String json = map2Json(sourceMap);
        String updatedJson = replaceValueAccordingJsonPath(json, jsonPath, value);
        return json2Map2(updatedJson);
    }

}
