package org.firefly.server.main;

import com.google.gson.JsonElement;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.protocol.HttpContext;

import java.util.List;

public interface BaseController  {
    List<NameValuePair> getPostParams();

    byte[] getRequestBody();

    JsonElement getRequestBodyAsJson();

    HttpRequest getRequest();

    HttpResponse getResponse();

    HttpContext getContext();

    List<String> getParams();
}
