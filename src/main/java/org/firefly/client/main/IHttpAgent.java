package org.firefly.client.main;

import java.io.IOException;
import java.util.Map;

public interface IHttpAgent {

    /**
     * Get http page with GET method
     * @param url Url
     * @return HttpResponse
     */
    org.firefly.client.main.HttpResponse get(String url) throws IOException;

    /**
     * Get http page with GET method
     * @param url Url
     * @param queryParams Query params
     */
    org.firefly.client.main.HttpResponse get(String url, Map<String, Object> queryParams) throws IOException;

    /**
     * Get http page with POST method
     * @param url Url
     * @param params Post params
     * @return HttpResponse
     */
    org.firefly.client.main.HttpResponse post(String url, Map<String, Object> params) throws IOException;

    /**
     * Get http page with POST method
     * @param url URL
     * @return HttpResponse
     */
    org.firefly.client.main.HttpResponse post(String url) throws IOException;

    /**
     * Get http page with POST method
     * @param url Url
     * @param body Request body
     * @return HttpResponse
     */
    org.firefly.client.main.HttpResponse post(String url, byte[] body) throws IOException;

    /**
     * Clear all cookies
     * @return self
     */
    IHttpAgent clearCookies();

    /**
     * Add new temporary header to next request
     * @param headerKey Header key
     * @param headerValue Header value
     * @return self
     */
    IHttpAgent addTempHeader(String headerKey, String headerValue);

    /**
     * Remove temporary header by header key
     * @param headerKey Header key
     * @return self
     */
    IHttpAgent removeTempHeaderByKey(String headerKey);

    /**
     * Add new header to all requests
     * @param headerKey Header key
     * @param headerValue Header value
     * @return self
     */
    IHttpAgent addHeader(String headerKey, String headerValue);

    /**
     * Remove header by key
     * @param headerKey Header key
     * @return self
     */
    IHttpAgent removeHeader(String headerKey);

    /**
     * Add new cookie
     */
    IHttpAgent addCookie(String key, String value, String path);

    /**
     * Remove cookie
     */
    IHttpAgent removeCookie(String key);

    /**
     * Set connection timeout in milliseconds
     */
    void setConnectionTimeout(int milliseconds);
}
