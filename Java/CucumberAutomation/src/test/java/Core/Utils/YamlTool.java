package Core.Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlTool
{
    private static final ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
    public static final ObjectMapper jsonWriter = new ObjectMapper();

    /***
     * 将yaml文件转为json字符串
     * @param yamlPath file path
     * @return json字符串
     */
    public static String convertYamlToJson(String yamlPath) throws IOException
    {
        JsonNode yamlNode = yamlReader.readTree(new File(yamlPath));
        return jsonWriter.writerWithDefaultPrettyPrinter().writeValueAsString(yamlNode);
    }

    public static Map<String, Object> readYaml(String yamlPath) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Map<String, Object> data = new HashMap<>();
        try
        {
            // 读取文件到Map对象
            data = mapper.readValue(new File(yamlPath), Map.class);

            // 修改数据
            //data.put("keyToChange", "newValue");

            // 写回文件
            //mapper.writeValue(new File("path/to/your/file.yaml"), data);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return data;
    }

    public static Object readValueInYaml(String yamlPath, String key) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Object value = null;
        try {
            Map<String, Object> yamlMap = mapper.readValue(new File(yamlPath), Map.class);
            value = searchAndPrint(yamlMap, key, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static Object searchAndPrint(Map<String, Object> map, String key, String indent)
    {
        if (map.containsKey(key))
        {
            System.out.println(indent + key + ": " + map.get(key));
        }
        else
        {
            for (Map.Entry<String, Object> entry : map.entrySet())
            {
                if (entry.getValue() instanceof Map)
                {
                    // 递归查找嵌套的Map
                    searchAndPrint((Map<String, Object>) entry.getValue(), key, indent + "  ");
                }
                else if (entry.getValue() instanceof List)
                {
                    for (Object item : (List<?>) entry.getValue())
                    {
                        if (item instanceof Map)
                        {
                            // 递归查找列表中的Map
                            searchAndPrint((Map<String, Object>) item, key, indent + "  ");
                        }
                    }
                }
            }
        }
        return map.get(key);
    }


}




