package Core;

import java.util.Map;

import static CucumberAutomation.Hooks.publicParams;

public class ReplaceRequestUrlParamsInPublicParams implements ReplaceRequestUrlParamsStrategy
{
    @Override
    public void perform(Map<String, Object> replaceTarget, String replaceKey, String dataSourceKey) {
        replaceTarget.put(replaceKey, publicParams.get(dataSourceKey));
    }

}
