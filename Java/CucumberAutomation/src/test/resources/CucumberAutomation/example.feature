@example @APITest
Feature: An demo api test example

  @test001 @CASE_INFO(demoTest001)
  Scenario: Run the database DML
    Given initial the user data<"name, mobile">, and save<"1-1">
    Then connect DB to execute SQL<"queryStudent">"数据库数据校验"<"1-2">
    Then connect DB to execute SQL<"updateStudent">"数据库数据校验"<"1-3">

  @test002 @CASE_INFO(demoTest002)
  Scenario: Run the api test
    Given initial the user data<"name, mobile">, and save<"1-1">
    When execute the common http request<"getAccessToken">"获取accessToken"<"2-1">
    Then verify the http response<"getAccessToken">"http数据校验"<"2-2">
    When execute the common http request<"createMenu">"创建menu"<"3-1">
    Then verify the http response<"createMenu">"http数据校验"<"3-2">
    When execute the common http request<"getMenu">"查看menu"<"4-1">
    Then verify the http response<"getMenu">"http数据校验"<"4-2">
    When execute the common http request<"deleteMenu">"删除menu"<"5-1">
    Then verify the http response<"deleteMenu">"http数据校验"<"5-2">

  @test003 @CASE_INFO(demoTest003)
  Scenario: Test the header replace
    Given initial the user data<"name, mobile">, and save<"1-1">
    When execute the common http request<"getAccessToken">"获取accessToken"<"2-1">
    Then verify the http response<"getAccessToken">"http数据校验"<"2-2">
    When execute the common http request<"createMenu">"创建menu"<"3-1">
    Then verify the http response<"createMenu">"http数据校验"<"3-2">