package org.firefly.server.net;

import com.google.common.reflect.ClassPath;
import com.sun.istack.internal.NotNull;
import org.apache.http.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.firefly.server.main.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpServer {

    private List<String> controllersPaths = new ArrayList<>();

    private final org.apache.http.impl.bootstrap.HttpServer server;

    public void addControllerPath(String path) {
        controllersPaths.add(path);
    }

    private Charset charset = Charset.forName("UTF-8");

    public void setCharset(@NotNull Charset charset) {
        this.charset = charset;
    }

    public class HttpHandler implements HttpRequestHandler {

        @Override
        public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {

            final String uri = request.getRequestLine().getUri();

            ClassPath classpath = ClassPath.from(Thread.currentThread()
                    .getContextClassLoader());

            for(String path : controllersPaths) {

                Set<Class<?>> annotated = new HashSet<>();

                for(ClassPath.ClassInfo classInfo : classpath.getTopLevelClassesRecursive(path)) {
                    Class<?> c = classInfo.load();

                    if(c.isAnnotationPresent(Controller.class)) {
                        annotated.add(c);
                    }
                }

                for(Class<?> c : annotated) {

                    for(Annotation a : c.getAnnotations()) {
                        if(a instanceof Controller) {
                            Controller controller = (Controller) a;

                            try {
                                Object ctr = c.newInstance();

                                MutableBaseController baseController = new MutableBaseController();
                                baseController.setContext(context);
                                baseController.setRequest(request);
                                baseController.setResponse(response);

                                if(uri.contains(controller.path())) {

                                    for(Method method : c.getMethods()) {
                                        for(Annotation an : method.getAnnotations()) {

                                            if(an instanceof Resource) {
                                                Resource rs = (Resource) an;

                                                if(rs.method().name().equalsIgnoreCase(request.getRequestLine().getMethod())) {

                                                    if(rs.method() == RequestMethod.POST) {

                                                        HttpEntity entity = null;
                                                        if (request instanceof HttpEntityEnclosingRequest) {
                                                            entity = ((HttpEntityEnclosingRequest) request).getEntity();

                                                            // For some reason, just putting the incoming entity into
                                                            // the response will not work. We have to buffer the message.
                                                            byte[] data;
                                                            if (entity == null) {
                                                                data = new byte[0];
                                                            } else {
                                                                data = EntityUtils.toByteArray(entity);
                                                            }

                                                            baseController.setBody(data);
                                                            baseController.setPostParams(extractPostParam(data));
                                                        }
                                                    }

                                                    String resourcePath = rs.path();

                                                    Pattern pattern = Pattern.compile("^/"+controller.path()+"/"+resourcePath+"(\\?(.*))?$");
                                                    Matcher matcher = pattern.matcher(uri);

                                                    boolean isFound = false;

                                                    if(matcher.find()) {
                                                        isFound = true;

                                                        for(int i=1; i<=matcher.groupCount(); i++) {
                                                            String param = matcher.group(i);

                                                            context.setAttribute(String.valueOf(i), param);
                                                            baseController.addParam(param);
                                                        }

                                                        method.invoke(ctr, ((Context) baseController));
                                                    }

                                                    // Extract params from URI
                                                    String[] s = uri.split("\\?");

                                                    if(s.length >= 2) {
                                                        String[] ic = s[1].split("&");

                                                        for(String i : ic) {
                                                            String[] p = i.split("=");
                                                            if(p.length == 2) {
                                                                context.setAttribute(p[0], p[1]);
                                                            }
                                                        }

                                                    }

                                                    if(isFound) {
                                                        return;
                                                    }
                                                }

                                            }
                                        }
                                    }


                                    try {
                                        if(uri.matches("^/"+controller.path()+"(\\?(.*))?")) {
                                            Method index = c.getMethod("index", Context.class);
                                            Resource rs = index.getAnnotation(Resource.class);

                                            if (index != null && rs.method().name().equalsIgnoreCase(request.getRequestLine().getMethod()) && rs.path().isEmpty()) {
                                                index.invoke(ctr, ((Context) baseController));
                                                return;
                                            }
                                        }
                                    } catch(NoSuchMethodException e) {
                                        e.printStackTrace();
                                    }

                                }

                            } catch(InvocationTargetException e) {
                                response.setStatusCode(500);
                                response.setEntity(new StringEntity("Exception: " + e.getTargetException().getMessage()));
                                return;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            break;
                        }
                    }
                }
            }

            response.setStatusCode(404);
            response.setEntity(new StringEntity("Not found"));
            //response.setEntity(new StringEntity("text"));
        }
    }

    private List<NameValuePair> extractPostParam(byte[] data) {
        String text = new String(data, charset);

        try {
            text = URLDecoder.decode(text, charset.name());

            return URLEncodedUtils.parse(text, charset);
        } catch (Exception e) {
            return null;
        }
    }

    public HttpServer(final int port, final int soTimeout) throws IOException, InterruptedException {

        SocketConfig cfg = SocketConfig.custom().
                setTcpNoDelay(true).
                setSoTimeout(soTimeout).build();

         server = ServerBootstrap.bootstrap().
                setListenerPort(port).
                setServerInfo("Firefly Server/1.0").
                setSocketConfig(cfg).
                registerHandler("*", new HttpHandler()).
                create();
    }

    public void start() throws IOException {
        server.start();
    }
}
