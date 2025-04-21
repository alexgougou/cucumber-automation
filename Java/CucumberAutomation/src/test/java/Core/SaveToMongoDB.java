package Core;

import Core.Utils.MongoDBUtil;

import static Core.EnvData.mongoDBDataBaseName;
import static CucumberAutomation.Hooks.caseInfoName;

public class SaveToMongoDB implements SaveDataStrategy
{
    @Override
    public void save(String key, Object value)
    {
        MongoDBUtil.getInstance().updateOneByKey(mongoDBDataBaseName, caseInfoName, key, value);
    }
}
