package org.pokenet.server.network;

/*
 * Simple MySQL Java Class
 * Makes it similair to PHP
 */
import java.sql.*;

/**
 * Handles MySql connections
 * @author Daniel Morante
 */
public class MySqlManager {
    private Connection mysql_connection;
    private ResultSet mysql_result;    
    private String mysql_connectionURL;
    private String mysql_driver;
    
    /**
     * Connects to the server. Returns true on success.
     * @param server
     * @param username
     * @param password
     * @return
     */
    public boolean connect(String server, String username, String password) {
        try {
            //Load MySQL JDBC Driver
            mysql_driver = "com.mysql.jdbc.Driver";
            Class.forName(mysql_driver);

            //Open Connection
            mysql_connectionURL = "jdbc:mysql://" + server;
            mysql_connection = DriverManager.getConnection(mysql_connectionURL, username, password);
            return true;
        }
        catch( Exception x ) {
          x.printStackTrace();
          return false;
        }
    }
    
    /**
     * Selects the current database. Returns true on success
     * @param database
     * @return
     */
    public boolean selectDatabase(String database) {
    	try {
        	Statement stm = mysql_connection.createStatement();
        	stm.executeQuery("USE " + database);
        	return true;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    }
    
    /**
     * Closes the connection to the mysql server. Returns true on success.
     * @return
     */
    public boolean close(){
        try{
            mysql_connection.close();
            return true;
        }
        catch (Exception x) {
             x.printStackTrace();
             return false;
        }
    }
    
    /**
     * Returns a result set for a query
     * @param query
     * @return
     */
    public ResultSet query(String query){
        //Create Stament object
        Statement stmt;
        
        /*
         * We want to keep things simple, so...
         *
         * Detect whether this is an INSERT, DELETE, or UPDATE statement      
         * And use the executeUpdate() function
         *
         * Or...
         * 
         * Detect whether this is a SELECT statment and use the executeQuery()
         * Function. 
         * 
        */  
        
        if (query.startsWith("SELECT")) {
            //Use the "executeQuery" function becuase we have to retrive data
            //Return the data as a resultset
            try{
                //Execute Query
                stmt = mysql_connection.createStatement();
                mysql_result = stmt.executeQuery(query);
            }
            catch(Exception x) {
                x.printStackTrace();
            }
            
            //Return Result
            return mysql_result;
        }
        else {
            //It's an UPDATE, INSERT, or DELETE statement
            //Use the"executeUpdaye" function and return a null result
            try{
                //Execute Query
                stmt = mysql_connection.createStatement();
                stmt.executeUpdate(query);
            }
            catch(Exception x) {
                x.printStackTrace();
            }
            
            //Return nothing
            return null;
        }
    }    
}
