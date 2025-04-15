package Core;

import java.util.Map;

import static CucumberAutomation.Hooks.scenarioData;

public class ReplaceRequestUrlParamsInScenarioData implements ReplaceRequestUrlParamsStrategy
{
    @Override
    public void perform(Map<String, Object> replaceTarget, String replaceKey, String dataSourceKey)
    {
        replaceTarget.put(replaceKey, scenarioData.get(dataSourceKey));
    }
}
