package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DbCon {

    private static final Logger logger = Logger.getLogger(ExecuteQuery.class.getName());

    // DB Driver
    static String dbDriver = HttpServerModule.props.getProperty("dbdriver");
    static String dbUrl = HttpServerModule.props.getProperty("dburl");
    // DB ID/PW
    static String dbUser = HttpServerModule.props.getProperty("dbusername");
    static String dbPassword = HttpServerModule.props.getProperty("dbpassword");
    static Connection dbconn = null;



    public static void dbConnection()
    {

        logger.info("dbDriver:"+dbDriver);
        logger.info("dbUrl:"+dbUrl);
        logger.info("dbUser:"+dbUser);

        Connection connection = null;

        try
        {
            Class.forName(dbDriver);
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            dbconn = connection;

            logger.info("DB Connection [성공]");

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public void dbClose()
    {
        try
        {
            if(dbconn != null)
            {
                dbconn.close();
                dbconn = null;
                logger.info("DB Close [성공]");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }



}
