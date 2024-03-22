package org.example;

import org.example.HttpServerModule;

import java.io.IOException;
import java.util.logging.Logger;

public class ServerStart {
    private static final Logger logger = Logger.getLogger(HttpServerModule.class.getName());
    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        logger.info("HTTP Server Start!!");

        new HttpServerModule();

        //DbCon.dbConnection();
    }

}
