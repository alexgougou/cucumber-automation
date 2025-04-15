package Core;

import java.util.Map;

public interface ReplaceRequestParamsStrategy
{
    <T> T perform(Map<String, Object> replaceTarget, String jsonPath, String dataSourceKey);

}
