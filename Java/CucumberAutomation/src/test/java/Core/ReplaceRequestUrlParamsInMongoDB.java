package Core;

import Core.Utils.MongoDBUtil;
import java.util.Map;
import static Core.EnvData.mongoDBDataBaseName;
import static CucumberAutomation.Hooks.caseInfoName;

public class ReplaceRequestUrlParamsInMongoDB implements ReplaceRequestUrlParamsStrategy
{
    @Override
    public void perform(Map<String, Object> replaceTarget, String replaceKey, String dataSourceKey)
    {
        Object value = MongoDBUtil.getInstance().findOneByKey(mongoDBDataBaseName, caseInfoName, dataSourceKey);
        replaceTarget.put(replaceKey, value);
    }
}
