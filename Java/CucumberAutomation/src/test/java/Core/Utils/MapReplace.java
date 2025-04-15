package Core.Utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class MapReplace
{
    private static final Logger logger = LogManager.getLogger(MapReplace.class);

    public static void replaceMap(Map<Object, Object> sourceMap, Map<Object, Object> replaceMap)
    {
        processMap(sourceMap, replaceMap);
        System.out.println(sourceMap);
    }

    public static void processMap(Map<Object, Object> sourceMap, Map<Object, Object> replaceMap)
    {
        for (Map.Entry<Object, Object> entry : replaceMap.entrySet())
        {
            Object key = entry.getKey();
            if (!sourceMap.containsKey(key))
            {
                logger.error("Key not found: {}", key);
            }
            Object sourceValue = sourceMap.get(key);
            Object replaceValue = entry.getValue();

            handleValue(sourceValue, replaceValue, () -> sourceMap.put(key, replaceValue));

        }
    }

    public static void processList(List<Object> sourceList, List<Object> replaceList)
    {
        if (sourceList.size() != replaceList.size())
        {
            logger.error("List size mismatch");
        }
        for (int i = 0; i < sourceList.size(); i++)
        {
            int index = i;
            Object sourceValue = sourceList.get(index);
            Object replaceValue = replaceList.get(index);

            handleValue(sourceValue, replaceValue, () -> sourceList.set(index, replaceValue));
        }
    }

    public static void handleValue(Object sourceValue, Object replaceValue, Runnable updater)
    {
        if (sourceValue instanceof Map)
        {
            if (replaceValue instanceof Map)
            {
                processMap((Map<Object, Object>) sourceValue, (Map<Object, Object>) replaceValue);
            }
            else
            {
                logger.error("Expected Map but found: {}", replaceValue.getClass().getSimpleName());
            }
        }
        else if (sourceValue instanceof List)
        {
            if (replaceValue instanceof List)
            {
                processList((List<Object>) sourceValue, (List<Object>) replaceValue);
            }
            else
            {
                logger.error("Expected List but found: {}", replaceValue.getClass().getSimpleName());
            }

        }
        else
        {
            if (replaceValue == null) {
                logger.error("replace value can not be null");
            }
        }
        updater.run();
    }


}
