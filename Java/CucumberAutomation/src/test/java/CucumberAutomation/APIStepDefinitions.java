package CucumberAutomation;

import Core.Utils.GenerateUserInfo;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Core.DBStepHandler.*;
import static Core.HttpHandler.*;
import static Core.StepData.*;


public class APIStepDefinitions
{
    private static final Logger logger = LogManager.getLogger(APIStepDefinitions.class);

    @ParameterType("<(.+)>")
    public List<String> listParam(String input){
        return Arrays.asList(input.split(","));
    }
    @Given("initial the user data<{string}>, and save<{string}>")
    public void initialUserData(String data, String stepIndex)
    {
        Map<String, String> userInfo = new HashMap<>();
        String mobile = GenerateUserInfo.genPhoneNum();
        String email = GenerateUserInfo.genEmail();
        String sex = GenerateUserInfo.genSex();
        String name = GenerateUserInfo.genChineseName(sex);
        String idNo = GenerateUserInfo.generateIDCard(20, 50, sex);
        userInfo.put("mobile", mobile);
        userInfo.put("email", email);
        userInfo.put("sex", sex);
        userInfo.put("name", name);
        userInfo.put("idNo", idNo);
        System.out.println(userInfo);
    }

    @When("execute the common http request<{string}>{string}<{string}>")
    public void sendRequest(String stepName, String stepIntro, String stepIndex)
    {
        getStepStatus(stepIndex);
        httpStepInit(stepName);
        httpStepPerform();
    }

    @Then("verify the http response<{string}>{string}<{string}>")
    public void requestVerify(String stepName, String stepIntro, String stepIndex)
    {
        getStepStatus(stepIndex);
        verifyHttpResult();
    }

    @Then("connect DB to execute SQL<{string}>{string}<{string}>")
    public void DBVerify(String stepName, String stepIntro, String stepIndex)
    {
        getStepStatus(stepIndex);
        DBStepInit(stepName);
        DBStepPerform();
        verifyDBResult();

    }


}
