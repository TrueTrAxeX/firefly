package org.firefly.client.main;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpAgent implements IHttpAgent {

    public static void main(String[] args) {
        IHttpAgent agent = new HttpAgent();

        try {
            org.firefly.client.main.HttpResponse response = agent.get("http://google.ru");

            System.out.println(response.toString(Charset.forName("cp1251")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Header> headers = new ArrayList<>();
    private List<Header> tempHeaders = new ArrayList<>();
    private CookieStore cookieStore = new BasicCookieStore();

    private int connectionTimeoutMs = 30000; // default 30 seconds

    private org.firefly.client.main.HttpResponse doRequest(String url, byte[] body, Map<String, Object> params, RequestMethod method) throws IOException {

        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(cookieStore);

        CloseableHttpClient httpClient = builder.build();

        CloseableHttpResponse response = null;

        final RequestConfig config = RequestConfig.custom().
                setConnectTimeout(connectionTimeoutMs).
                build();

        switch(method) {
            case GET:
                synchronized (this) {

                    HttpGet httpGet = null;

                    if(params != null) {

                        URI uri = null;

                        try {
                            uri = new URIBuilder(url).addParameters(params.entrySet().stream().map(e -> new BasicNameValuePair(e.getKey(), e.getValue().toString())).collect(Collectors.toList())).build();
                        } catch (URISyntaxException e) {
                            throw new IOException(e.getMessage());
                        }

                        httpGet = new HttpGet(uri);
                    } else {
                        httpGet = new HttpGet(url);
                    }

                    final HttpGet finalHttpGet = httpGet;

                    headers.stream().forEach(h -> finalHttpGet.addHeader(h.getName(), h.getValue()));
                    tempHeaders.stream().forEach(h -> finalHttpGet.addHeader(h.getName(), h.getValue()));

                    tempHeaders.clear();

                    httpGet.setConfig(config);

                    response = httpClient.execute(httpGet);

                    try {
                        return new org.firefly.client.main.HttpResponse(response, EntityUtils.toByteArray(response.getEntity()));
                    } finally {
                        response.close();
                    }
                }

            case POST:

                synchronized (this) {
                    HttpPost httpPost = new HttpPost(url);

                    if(params != null) {
                        List<NameValuePair> payload = params.entrySet().stream().
                                map(entry ->
                                new BasicNameValuePair(entry.getKey(), entry.getValue().toString())).
                                collect(Collectors.toList());

                        httpPost.setEntity(new UrlEncodedFormEntity(payload, Charset.forName("UTF-8")));
                    }

                    if(body != null) {
                        httpPost.setEntity(new ByteArrayEntity(body));
                    }

                    headers.stream().forEach(h -> httpPost.addHeader(h.getName(), h.getValue()));
                    tempHeaders.stream().forEach(h -> httpPost.addHeader(h.getName(), h.getValue()));

                    tempHeaders.clear();

                    httpPost.setConfig(config);

                    response = httpClient.execute(httpPost);

                    try {

                        try {
                            return new org.firefly.client.main.HttpResponse(response, EntityUtils.toByteArray(response.getEntity()));
                        } finally {
                            response.close();
                        }
                    } finally {
                        response.close();
                    }
                }
        }

        return null;
    }

    @Override
    public org.firefly.client.main.HttpResponse get(String url) throws IOException {
        return doRequest(url, null, null, RequestMethod.GET);
    }

    @Override
    public org.firefly.client.main.HttpResponse get(String url, Map<String, Object> queryParams) throws IOException {
        return doRequest(url, null, queryParams, RequestMethod.GET);
    }

    @Override
    public org.firefly.client.main.HttpResponse post(String url, Map<String, Object> params) throws IOException {
        return doRequest(url, null, params, RequestMethod.POST);
    }

    @Override
    public org.firefly.client.main.HttpResponse post(String url) throws IOException {
        return doRequest(url, null, null, RequestMethod.POST);
    }

    @Override
    public org.firefly.client.main.HttpResponse post(String url, byte[] body) throws IOException {
        return doRequest(url, body, null, RequestMethod.POST);
    }

    @Override
    public IHttpAgent clearCookies() {
        cookieStore.getCookies().clear();

        return this;
    }

    @Override
    public IHttpAgent addTempHeader(String headerKey, String headerValue) {
        tempHeaders.add(new BasicHeader(headerKey, headerValue));

        return this;
    }

    @Override
    public IHttpAgent removeTempHeaderByKey(String headerKey) {
        Iterator<Header> it = tempHeaders.iterator();

        while(it.hasNext()) {
            Header h = it.next();

            if(h.getName().equals(headerKey))
                it.remove();
        }

        return this;
    }

    @Override
    public IHttpAgent addHeader(String headerKey, String headerValue) {

        headers.add(new BasicHeader(headerKey, headerValue));

        return this;
    }

    @Override
    public IHttpAgent removeHeader(String headerKey) {

        Iterator<Header> it = headers.iterator();

        while(it.hasNext()) {
            Header h = it.next();

            if(h.getName().equals(headerKey))
                it.remove();
        }

        return this;
    }

    @Override
    public IHttpAgent addCookie(String key, String value, String path) {

        BasicClientCookie c = new BasicClientCookie(key, value);

        if(path != null) c.setPath(path);

        cookieStore.addCookie(c);

        return this;
    }

    @Override
    public IHttpAgent removeCookie(String key) {

        Iterator<Cookie> it = cookieStore.getCookies().iterator();

        while(it.hasNext()) {
            Cookie c = it.next();

            if(c.getName().equals(key)) {
                it.remove();
            }
        }

        return this;
    }

    @Override
    public void setConnectionTimeout(int milliseconds) {
        this.connectionTimeoutMs = milliseconds;
    }

}
