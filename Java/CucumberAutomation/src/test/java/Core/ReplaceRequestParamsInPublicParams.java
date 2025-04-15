package Core;

import java.util.Map;

import static Core.Utils.JsonUtil.replaceAccordingJsonPath;
import static CucumberAutomation.Hooks.publicParams;

public class ReplaceRequestParamsInPublicParams implements ReplaceRequestParamsStrategy
{
    @Override
    public <T> T perform(Map<String, Object> replaceTarget, String jsonPath, String dataSourceKey) {
        return replaceAccordingJsonPath(replaceTarget, jsonPath, publicParams.get(dataSourceKey));
    }

}
