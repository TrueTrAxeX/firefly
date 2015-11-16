Firefly is library for writing REST API.

Simple example of HTTP server:
```java

@Controller(path="test")
public class TestController {

    @Resource(method = RequestMethod.GET)
    public void index(Context ctx) throws UnsupportedEncodingException {
        ctx.getResponse().setEntity(new StringEntity(ctx.getPostParams().get(0).getValue()));
        ctx.getResponse().setStatusCode(200);
    }

    @Resource(path = "get/([0-9]+)", method = RequestMethod.GET)
    public void get(Context ctx) throws UnsupportedEncodingException {
        ctx.getResponse().setEntity(new StringEntity(ctx.getContext().getParams(0)));
        ctx.getResponse().setStatusCode(200);
    }

}

public class Main {
    public static void main(String[] args) {
        try {
            HttpServer server = new HttpServer(4253, 10000);
            server.addControllerPath("org.mypackage.controllers");

            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

You can access for your pages using urls: http://localhost:4253/test (TestController.index) and http://localhost:4253/test/get/123 (TestController.get)

Simple HTTP client:
```java
class MyClass {
  public static void main(String[] args) {
    IHttpAgent agent = new HttpAgent();

    try {
      org.firefly.client.main.HttpResponse response = agent.get("http://google.ru");

      System.out.println(response.toString(Charset.forName("cp1251")));

      resposne = agent.post("http://google.ru", new HashMap<String, Object>() {{ put("param1", "value1"); }});
    } catch (IOException e) {
        e.printStackTrace();
    }
  }
}
```

Cookies are always saved!

You can clear cookies with agent method: clearCookies() or add new cookies with agent method: addCookie.
