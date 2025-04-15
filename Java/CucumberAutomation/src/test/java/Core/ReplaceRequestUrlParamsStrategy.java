package Core;

import java.util.Map;

public interface ReplaceRequestUrlParamsStrategy
{
    void perform(Map<String, Object> replaceTarget, String replaceKey, String dataSourceKey);

}
