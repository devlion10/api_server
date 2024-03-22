package org.example;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class HttpServerModule {

    // 로거 인스턴스 생성
    private static final Logger logger = Logger.getLogger(HttpServerModule.class.getName());
    HttpServer server = null;
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    InputStream globalsPropIs = loader.getResourceAsStream("globlas.properties");
    public static Properties props = new Properties();

    public HttpServerModule() throws IOException {

        String strClassPath = System.getProperty("java.class.path");
        logger.info("classpath___:"+strClassPath);

        this.props.load(this.globalsPropIs);
        String calsAp_context =props.getProperty("calsAp_context");
        String calsApi_port =props.getProperty("calsApi_port");
        Integer listenPort = Integer.parseInt(calsApi_port); //동작시킬 포트 번호

        try{

            logger.info("listenPort:"+listenPort);
            server = HttpServer.create(new InetSocketAddress(listenPort), 0);
            server.createContext(calsAp_context, new executeHandler()); //컨텍스트 명
            // 루트 경로에 대한 에러 핸들러 설정
            server.createContext("/", new ErrorHandler());
            // 스레드 풀 설정 (null로 설정 시 기본 executor 사용)
            server.setExecutor(null);
            server.start(); //httpServer 시작
            logger.info("serverStart__");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public class executeHandler implements HttpHandler{
        public void handle(HttpExchange exchange) throws IOException {
            logger.info("Received request: " + exchange.getRequestMethod() + " " + exchange.getRequestURI());

            if ("GET".equals(exchange.getRequestMethod())) {
                handleGetRequest(exchange);
            } else if ("POST".equals(exchange.getRequestMethod())) {
                handlePostRequest(exchange);
            } else {
                logger.warning("Unsupported request method: " + exchange.getRequestMethod());
                exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
            }


        }

        private void handleGetRequest(HttpExchange exchange) throws IOException {
            String response = "Hello, HTTP!";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private void handlePostRequest(HttpExchange exchange) throws IOException {
            DbCon.dbConnection();
            // POST 데이터 읽기
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            String data = br.readLine();
            logger.info("Received POST data: " + data);
            ExecuteQuery exequery=new ExecuteQuery();
            HashMap<String, Object> resultMap=exequery.insertCalsdata(data);
            String resultStr=resultMap.toString();
            byte[] resultStr_bytes = resultStr.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, resultStr_bytes.length);
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(resultStr_bytes);
            outputStream.flush();
            outputStream.close();

        }
    }

    // 루트 경로 외의 요청 핸들러 (에러 처리)
    static class ErrorHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            //logger.warning("Invalid request path: " + exchange.getRequestURI());

            String errorResponse = "404 Error: Invalid path!";
            exchange.sendResponseHeaders(404, errorResponse.length());
            OutputStream os = exchange.getResponseBody();
            os.write(errorResponse.getBytes());
            os.close();
        }
    }


}


