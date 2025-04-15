package Core.Utils;

import org.apache.hc.core5.http.Header;

public class HttpResult
{
    private int code;

    private String content;

    private Header[] headers;

    public HttpResult(int code)
    {
        this.code = code;
    }


    public HttpResult(int code, String content, Header[] headers)
    {
        this.code = code;
        this.content = content;
        this.headers = headers;
    }

    public int getCode()
    {
        return code;
    }

    public String getContent()
    {
        return content;
    }

    public Header[] getHeaders()
    {
        return headers;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public void setHeaders(Header[] headers)
    {
        this.headers = headers;
    }
}
