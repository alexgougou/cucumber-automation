package Core.Utils;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class YamlUtil
{
    private static LinkedHashMap<String, Object> properties;

    public static LinkedHashMap<String, Object> getYamlMap(String yamlPath)
    {
        InputStream in;
        try
        {
            Yaml yaml = new Yaml();
            in = new FileInputStream(yamlPath);
            properties = yaml.loadAs(in, LinkedHashMap.class);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return properties;
    }

    public static Object getValueByKey(String yamlPath, String root, String key)
    {
        getYamlMap(yamlPath);
        Object value = null;
        if (root.equals(key))
        {
            Iterator it = properties.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry entry = (Map.Entry) it.next();
                if (key.equals(entry.getKey()))
                {
                    value = (String) entry.getValue();
                    break;
                }
            }
        }
        else
        {
            LinkedHashMap<String, String> rootProperty = (LinkedHashMap<String, String>) properties.get(root);
            value = iter(rootProperty, key);
        }
        return value;
    }

    public static Object iter(LinkedHashMap<String, String> rootProperty, String key) {
        Iterator it = rootProperty.entrySet().iterator();
        Object value = null;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (key.equals(entry.getKey())) {
                return entry.getValue();
            }
            if (!(entry.getValue() instanceof LinkedHashMap)) {
                continue;
            }
            value = iter((LinkedHashMap<String, String>) entry.getValue(), key);
            if (value!=null) {
                break;
            }

        }
        return value;
    }



}
