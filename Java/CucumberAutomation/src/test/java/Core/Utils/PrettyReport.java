package Core.Utils;

import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.Reportable;
import net.masterthought.cucumber.json.support.Status;
import net.masterthought.cucumber.presentation.PresentationMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrettyReport
{
    public static void genReport()
    {
        File reportOutputDir = new File("target");
        List<String> jsonFiles = new ArrayList<>();
        jsonFiles.add("/Users/alex/Documents/MyProject/Java/CucumberAutomation/target/cucumber-reports/cucumber.json");
        String buildNumber = "1";
        String porjectName = "cucumberProject";
        boolean runWithJenkins = false;
        Configuration configuration = new Configuration(reportOutputDir, porjectName);
        configuration.addPresentationModes(PresentationMode.EXPAND_ALL_STEPS);
        configuration.setNotFailingStatuses(Collections.singleton(Status.FAILED));
        configuration.setBuildNumber(buildNumber);

        ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
        Reportable result = reportBuilder.generateReports();

    }
}
