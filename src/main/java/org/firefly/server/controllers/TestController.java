package org.firefly.server.controllers;

import org.apache.http.entity.StringEntity;
import org.firefly.server.main.Context;
import org.firefly.server.main.Controller;
import org.firefly.server.main.RequestMethod;
import org.firefly.server.main.Resource;

import java.io.UnsupportedEncodingException;

@Controller(path="test")
public class TestController {

    @Resource(method = RequestMethod.GET)
    public void index(Context ctx) throws UnsupportedEncodingException {
        ctx.getResponse().setEntity(new StringEntity("hello "));
        ctx.getResponse().setStatusCode(200);
    }

    @Resource(path = "lano([0-9]+)/([0-9]+)")
    public void test(Context ctx) throws UnsupportedEncodingException {
        ctx.getResponse().setEntity(new StringEntity("hel123slo " + ctx.getContext().getAttribute("1")));
        ctx.getResponse().setStatusCode(200);
    }

    @Resource(path="get/?([A-z]+)?")
    public void get(Context ctx) throws UnsupportedEncodingException {
        ctx.getResponse().setEntity(new StringEntity("suka"));
        ctx.getResponse().setStatusCode(200);
    }

}
