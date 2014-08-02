/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pizza;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Vincent
 */
public class DBConnection {
    private static final String dbClassName = "com.mysql.jdbc.Driver";
    private static final String CONNECTION = "jdbc:mysql://mathlab.utsc.utoronto.ca:3306/zhengye1";
    private static Connection conn = null;
    private int connectStatus;
    public Connection connect() throws ClassNotFoundException{
        Class.forName(dbClassName);
        try{
            conn = DriverManager.getConnection(CONNECTION, "zhengye1", "Yukirin0715");
        }
        catch (SQLException e){
            System.err.println("Error occured");
            e.printStackTrace();
        }
        return conn;
    }
    
    public void disconnect(){
        try{
         conn.close();
        }
        catch(SQLException e){
            System.err.println("Can't close connection!");
            e.printStackTrace();
        }
        finally{
            conn = null;
        }
    }
    
    public int getConnectStatus(){
        return this.connectStatus;
    }
    
    public void setConnectStatus(int connectStatus){
        this.connectStatus = connectStatus;
    }
}
