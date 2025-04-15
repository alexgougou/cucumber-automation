package Core;

import static CucumberAutomation.Hooks.scenarioData;

public class SaveToScenarioData implements SaveDataStrategy
{
    @Override
    public void save(String key, Object value)
    {
        scenarioData.put(key, value);
    }
}
