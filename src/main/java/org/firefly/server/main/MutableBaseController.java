package org.firefly.server.main;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.protocol.HttpContext;

import java.util.ArrayList;
import java.util.List;

public class MutableBaseController extends Context implements BaseController {

    private static JsonParser parser = new JsonParser();

    private List<NameValuePair> postParams;
    private byte[] body;
    private HttpRequest request;
    private HttpResponse response;
    private HttpContext context;
    private List<String> params = new ArrayList<>();

    public List<NameValuePair> getPostParams() {
        return postParams;
    }

    public byte[] getRequestBody() {
        return body;
    }

    public JsonElement getRequestBodyAsJson() {
        try {
            return parser.parse(new String(body));
        } catch(Exception e) {
            return null;
        }
    }

    public HttpRequest getRequest() {
        return request;
    }

    public HttpResponse getResponse() {
        return response;
    }

    public void addParam(String value) {
        params.add(value);
    }

    public List<String> getParams() {
        return params;
    }

    public HttpContext getContext() {
        return context;
    }

    public void setContext(HttpContext context) {
        this.context = context;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public void setResponse(HttpResponse response) {
        this.response = response;
    }

    public void setPostParams(List<NameValuePair> postParams) {
        this.postParams = postParams;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
