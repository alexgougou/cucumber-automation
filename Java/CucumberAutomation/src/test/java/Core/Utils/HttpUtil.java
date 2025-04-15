package Core.Utils;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static Core.Utils.Config.*;

public class HttpUtil
{
    private static final Logger logger = LogManager.getLogger(HttpUtil.class);

    private HttpUtil()
    {
    }

    private volatile static HttpUtil instance = null;

    public static HttpUtil getInstance()
    {
        if (instance == null)
        {
            synchronized (HttpUtil.class)
            {
                if (instance == null)
                {
                    instance = new HttpUtil();
                }
            }
        }
        return instance;
    }

    private volatile CloseableHttpClient httpClient = null;

    private CloseableHttpClient getHttpClient()
    {
        if (httpClient == null)
        {
            synchronized (HttpUtil.class)
            {
                if (httpClient == null)
                {
                    try
                    {
                        PoolingHttpClientConnectionManager poolingManager = new PoolingHttpClientConnectionManager();
                        poolingManager.setMaxTotal(HTTP_POOL_MAX_TOTAL);
                        poolingManager.setDefaultMaxPerRoute(HTTP_POOL_PER_ROUTE_MAX_SIZE);
                        SocketConfig socketConfig = SocketConfig.custom()
                                .setSoTimeout(SOCKET_TIMEOUT, TimeUnit.MICROSECONDS)
                                .build();
                        poolingManager.setDefaultSocketConfig(socketConfig);
                        RequestConfig requestConfig = RequestConfig.custom()
                                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT, TimeUnit.MICROSECONDS)
                                .setConnectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                                .build();
                        DefaultHttpRequestRetryStrategy retryStrategy = new DefaultHttpRequestRetryStrategy(HTTP_RETRY_TIME, TimeValue.ofSeconds(1L));
                        httpClient = HttpClients.custom()
                                .setConnectionManager(poolingManager)
                                .setDefaultRequestConfig(requestConfig)
                                .setRetryStrategy(retryStrategy)
                                .build();
                    }
                    catch (Exception e)
                    {
                        logger.error("create http client failed: ", e);
                    }
                }
            }
        }
        return httpClient;
    }

    /***
     * 设置请求头
     * @param headers
     * @param httpUriRequestBase
     */
    public static void replaceHeaders(Map<String, String> headers, HttpUriRequestBase httpUriRequestBase)
    {
        if (headers != null)
        {
            Set<Map.Entry<String, String>> headersEntrySet = headers.entrySet();
            for (Map.Entry<String, String> entry : headersEntrySet)
            {
                httpUriRequestBase.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /***
     * 设置json格式请求的请求头
     * @param httpUriRequestBase
     */
    public static void setJsonHeader(HttpUriRequestBase httpUriRequestBase)
    {
        httpUriRequestBase.setHeader("Content-Type", "application/json; charset=UTF-8");
    }

    public static void setXmlHeader(HttpUriRequestBase httpUriRequestBase)
    {
        httpUriRequestBase.setHeader("Content-Type", "application/xml; charset=UTF-8");
    }

    /***
     * 设置请求的body
     * @param body 可以是json字符串，也可以是xml字符串
     * @param httpUriRequestBase
     */
    public static void setRequestBody(String body, HttpUriRequestBase httpUriRequestBase)
    {
        StringEntity stringEntity = new StringEntity(body, StandardCharsets.UTF_8);
        httpUriRequestBase.setEntity(stringEntity);
    }

    /***
     * 替换url中参数，如https://qyapi.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN&agentid=AGENTID
     * @param url
     * @param params
     * @return
     */
    public URIBuilder replaceUrlParam(String url, Map<String, Object> params)
    {
        URIBuilder uriBuilder = null;
        try
        {
            uriBuilder = new URIBuilder(url);
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException(e);
        }
        if (params != null)
        {
            Set<Map.Entry<String, Object>> paramsEntrySet = params.entrySet();
            for (Map.Entry<String, Object> entry : paramsEntrySet)
            {
                uriBuilder.setParameter(entry.getKey(), (String) entry.getValue());
            }
        }
        return uriBuilder;
    }

    public URI buildUrL(String url, Map<String, Object> params)
    {
        //替换url参数
        URIBuilder uriBuilder = replaceUrlParam(url, params);
        URI sendUrl = null;
        try
        {
            sendUrl = uriBuilder.build();
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException(e);
        }
        return sendUrl;
    }


    /***
     * 设置post请求的body参数
     * @param params
     * @param httpUriRequestBase
     */
    public void replacePostParam(Map<String, Object> params, HttpUriRequestBase httpUriRequestBase)
    {
        if (params != null)
        {
            List<NameValuePair> nvps = new ArrayList<>();
            Set<Map.Entry<String, Object>> paramsEntrySet = params.entrySet();
            for (Map.Entry<String, Object> entry : paramsEntrySet)
            {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
            httpUriRequestBase.setEntity(new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8));
        }
    }

    /***
     * send request and obtain http response
     * @param httpClient
     * @param httpUriRequestBase
     * @return
     */
    public HttpResult executeRequest(HttpClient httpClient, HttpUriRequestBase httpUriRequestBase)
    {
        CloseableHttpResponse response = null;
        try
        {
            response = (CloseableHttpResponse) httpClient.execute(httpUriRequestBase);
            HttpEntity entity = response.getEntity();
            Header[] responseHeaders = response.getHeaders();
            String headersString = JsonUtil.headers2Json(responseHeaders);
            logger.info("response headers:\n{}", headersString);
            String data = EntityUtils.toString(entity, CHAR_SET);
            logger.info("response content:\n {}", JsonUtil.prettyJson(data));
            EntityUtils.consume(entity);
            return new HttpResult(response.getCode(), data, response.getHeaders());
        }
        catch (IOException | ParseException e)
        {
            e.printStackTrace();
            return new HttpResult(-1);
        }
        finally
        {
            try
            {
                if (response != null)
                {
                    response.close();
                }
            }
            catch (Exception e)
            {
                logger.error("close response failed:", e);
            }
        }

    }

    /***
     * 不带请求头和请求参数
     * @param url
     * @return
     * @throws Exception
     */
    public HttpResult doGet(String url)
    {
        return doGet(url, null, null);
    }

    /***
     * 带参数，不带请求头
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public HttpResult doGet(String url, Map<String, Object> params)
    {
        return doGet(url, null, params);
    }

    /***
     * 带请求头和请求参数
     * @param url
     * @param headers
     * @param params
     * @return
     * @throws Exception
     */
    public HttpResult doGet(String url, Map<String, String> headers, Map<String, Object> params)
    {
        if (url == null || url.isEmpty())
        {
            logger.error("url is not correct");
            return new HttpResult(-1);
        }
        //创建httpclient对象
        CloseableHttpClient httpClient = this.getHttpClient();
        URI sendUrl = buildUrL(url, params);
        HttpGet httpGet = new HttpGet(sendUrl);
        logger.info("send the get request to: {}", sendUrl);
        //设置请求头
        replaceHeaders(headers, httpGet);
        return executeRequest(httpClient, httpGet);
    }


    /***
     *
     * @param url
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public HttpResult doPost(String url)
    {
        return doPost(url, null, null, null);
    }

    /***
     *
     * @param url
     * @param params
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public HttpResult doPost(String url, Map<String, Object> params)
    {
        return doPost(url, null, null, params);
    }

    /***
     *
     * @param url
     * @param headers
     * @param urlParams url中需要替换的参数
     * @param bodyParams 请求body中的参数
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */

    public HttpResult doPost(String url, Map<String, String> headers, Map<String, Object> urlParams, Map<String, Object> bodyParams)
    {
        if (url == null || url.isEmpty())
        {
            logger.error("url is not correct");
            return new HttpResult(-1);
        }
        //创建httpclient对象
        CloseableHttpClient httpClient = this.getHttpClient();
        URI sendUrl = buildUrL(url, urlParams);
        HttpPost httpPost = new HttpPost(sendUrl);
        logger.info("send the post request to: {}", sendUrl);
        //设置请求头
        replaceHeaders(headers, httpPost);
        //设置请求body
        replacePostParam(bodyParams, httpPost);
        return executeRequest(httpClient, httpPost);
    }

    /***
     *
     * @param url
     * @param json
     * @return
     */
    public HttpResult postJson(String url, String json)
    {
        return postJson(url, null, null, json);
    }

    /***
     *
     * @param url
     * @param headers
     * @param json
     * @return
     */
    public HttpResult postJson(String url, Map<String, String> headers, String json)
    {
        return postJson(url, headers, null, json);
    }

    /***
     *
     * @param url
     * @param headers
     * @param urlParams
     * @param json
     * @return
     */
    public HttpResult postJson(String url, Map<String, String> headers, Map<String, Object> urlParams, String json)
    {
        if (url == null || url.isEmpty())
        {
            logger.error("url is not correct");
            return new HttpResult(-1);
        }
        //创建httpclient对象
        CloseableHttpClient httpClient = this.getHttpClient();
        URI sendUrl = buildUrL(url, urlParams);
        HttpPost httpPost = new HttpPost(sendUrl);
        logger.info("send the post request to: {}", sendUrl);
        //设置请求头
        replaceHeaders(headers, httpPost);
        setJsonHeader(httpPost);
        logger.info("request json body:\n {}", json);
        //设置json对象request body
        setRequestBody(json, httpPost);
        return executeRequest(httpClient, httpPost);
    }

    public HttpResult postXml(String url, String xml)
    {
        return postXml(url, null, null, xml);
    }

    public HttpResult postXml(String url, Map<String, String> headers, String xml)
    {
        return postXml(url, headers, null, xml);
    }

    /***
     *
     * @param url
     * @param headers
     * @param urlParams
     * @param xml
     * @return
     */
    public HttpResult postXml(String url, Map<String, String> headers, Map<String, Object> urlParams, String xml)
    {
        if (url == null || url.isEmpty())
        {
            logger.error("url is not correct");
            return new HttpResult(-1);
        }
        //创建httpclient对象
        CloseableHttpClient httpClient = this.getHttpClient();
        URI sendUrl = buildUrL(url, urlParams);
        HttpPost httpPost = new HttpPost(sendUrl);
        logger.info("send the post request to: {}", sendUrl);
        //设置请求头
        replaceHeaders(headers, httpPost);
        setXmlHeader(httpPost);
        setRequestBody(xml, httpPost);
        return executeRequest(httpClient, httpPost);
    }

    /***
     * get请求下载文件
     * @param url
     * @param headers
     * @param params
     * @param localPath 本地文件路径
     * @return
     */
    public HttpResult downloadFile(String url, Map<String, String> headers, Map<String, Object> params, String localPath)
    {
        if (url == null || url.isEmpty())
        {
            logger.error("url is not correct");
            return new HttpResult(-1);
        }
        CloseableHttpClient httpClient = this.getHttpClient();
        URI sendUrl = buildUrL(url, params);
        HttpGet httpGet = new HttpGet(sendUrl);
        logger.info("send the get request to: {}", sendUrl);
        replaceHeaders(headers, httpGet);
        CloseableHttpResponse response = null;
        InputStream is = null;
        ByteArrayOutputStream bos = null;
        FileOutputStream fos = null;
        try
        {
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            long length = entity.getContentLength();
            is = entity.getContent();
            byte[] buffer = new byte[4096];
            int read = 0;
            while ((read = is.read(buffer)) > 0)
            {
                bos.write(buffer, 0, read);
            }
            fos = new FileOutputStream(localPath);
            bos.writeTo(fos);
            bos.flush();
            fos.flush();
            EntityUtils.consume(entity);
            return new HttpResult(response.getCode(), length + "", response.getHeaders());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new HttpResult(-1);
        }
        finally
        {
            try
            {
                if (response != null) response.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            try
            {
                if (bos != null) bos.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            try
            {
                if (fos != null) fos.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            try
            {
                if (is != null) is.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /***
     *
     * @param url
     * @param inputStream 文件流
     * @param fileName 原始文件名
     * @return
     */
    public HttpResult uploadFile(String url, InputStream inputStream, String fileName)
    {
        if (url == null || url.isEmpty())
        {
            logger.error("url is not correct");
            return new HttpResult(-1);
        }
        CloseableHttpClient httpClient = this.getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.EXTENDED);
        builder.addBinaryBody("file", inputStream, ContentType.MULTIPART_FORM_DATA, fileName);
        httpPost.setEntity(builder.build());
        return executeRequest(httpClient, httpPost);
    }


    public HttpResult postFile(String url, String filePath)
    {
        return postFile(url, null, null, null, filePath);
    }


    public HttpResult postFile(String url, String fileParams, String filePath)
    {
        return postFile(url, null, null, fileParams, filePath);
    }

    public HttpResult postFile(String url, Map<String, String> headers, Map<String, String> params, String fileParams, String filePath)
    {
        if (url == null || url.isEmpty())
        {
            logger.error("url is not correct");
            return new HttpResult(-1);
        }
        if (filePath == null || filePath.isEmpty())
        {
            logger.error("file path is not correct");
            return new HttpResult(-1);
        }
        //judge file exist
        File localFile = new File(filePath);
        if (!localFile.exists())
        {
            logger.error("The file can not be found");
            return new HttpResult(-1);
        }
        fileParams = (fileParams == null || fileParams.isEmpty()) ? "file" : fileParams;
        CloseableHttpClient httpClient = this.getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        replaceHeaders(headers, httpPost);

        //set request entity
        MultipartEntityBuilder multipartBuilder = MultipartEntityBuilder.create();
        multipartBuilder.setMode(HttpMultipartMode.EXTENDED);
        multipartBuilder.setCharset(StandardCharsets.UTF_8);
        multipartBuilder.addBinaryBody(fileParams, localFile);
        if (params != null)
        {
            for (String key : params.keySet())
            {
                String value = params.get(key);
                if (key != null && value != null)
                {
                    multipartBuilder.addTextBody(key, value);
                }
            }
        }
        HttpEntity requestEntity = multipartBuilder.build();
        httpPost.setEntity(requestEntity);
        return executeRequest(httpClient, httpPost);
    }


}
