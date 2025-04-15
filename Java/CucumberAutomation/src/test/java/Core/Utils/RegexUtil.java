package Core.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil
{
    public static String extractContentInBrackets(String input) {
        String regex = "\\((.*?)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
//            LoggerUtil.info("Can not find brackets in tags");
            return "";
        }
    }
}
