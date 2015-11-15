package org.firefly.client.main;

import org.apache.http.StatusLine;

import java.nio.charset.Charset;

public class HttpResponse {

    private byte[] body;
    private org.apache.http.HttpResponse apacheHttpResponse;

    public HttpResponse(org.apache.http.HttpResponse apacheHttpResponse, byte[] body) {
        this.apacheHttpResponse = apacheHttpResponse;
        this.body = body;
    }

    @Override
    public String toString() {
        return new String(body);
    }

    public byte[] getBody() {
        return body;
    }

    public String toString(Charset charset) {
        return new String(body, charset);
    }

    public int getStatusCode() {
        return apacheHttpResponse.getStatusLine().getStatusCode();
    }

    public StatusLine getStatusLine() {
        return apacheHttpResponse.getStatusLine();
    }

}
