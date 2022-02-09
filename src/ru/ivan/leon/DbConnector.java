package ru.ivan.leon;

import java.nio.charset.StandardCharsets;
import java.sql.*;

class DbConnector {
    String dbaddress;
    String dbName;
    private static Connection dbCon;
    private Statement stmt;
    private ResultSet rs;

    public DbConnector(String address, String dbName) {
        this.dbaddress = address;
        this.dbName = dbName;
    }

    public boolean connectToDB() throws SQLException {
        System.out.print("Connect to DB(9 try): ");
        for(int t = 0; t < 9;) {
            try {
                dbCon = DriverManager.getConnection(dbaddress, "BotApplication", "IamBottelegramm");
            } catch (SQLException e) {
                t++;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException u) {
                    u.printStackTrace();
                }
            }
            if(dbCon!= null) break;
            System.out.print(t + "...");
        }
        System.out.println();
        if(dbCon == null){
            System.out.println("Do not connected to DB");
            System.exit(1);
        }
        if (dbCon != null) {
            stmt = dbCon.createStatement();
            return true;
        }
        else {
            return false;
        }
    }

    public String getBotAddress() throws SQLException {
        if(dbCon == null) {
            System.out.println("Нет соединения с БД");
            return "Нет соединения с БД";
        }
        stmt = dbCon.createStatement();
        rs = stmt.executeQuery("SELECT value FROM botconfig WHERE (param = 'botAddress')");
        rs.next();
        return rs.getString(1);
    }

    public String getWeatherAddress() throws SQLException {
        stmt = dbCon.createStatement();
        rs = stmt.executeQuery("SELECT value FROM botconfig WHERE (param = 'weatherAPI')");
        rs.next();
        return rs.getString(1);
    }


    public void writetoDB(RequestStruct s) throws SQLException {
        if(dbCon == null) {
            System.out.println("Нет соединения с БД");
            return;
        }
        stmt = dbCon.createStatement();
        String sqlInsertdata = "INSERT INTO botusers (`idmsg`,`chatid`,`username`, `text`) VALUES ('" + s.update_id + "', '" + s.chat_id + "', '" + s.username + "', '" + s.text + "');";
        System.out.print("writetodb:    " + s.update_id + "..." + s.username + "..." + s.text +"\n");
        stmt.executeUpdate(sqlInsertdata);
        //executeQuery - для получения данных, executeUpdate для вставки, модификации, удаления
        stmt.close();
        //"INSERT INTO `mydbforbot`.`botusers` (`idmsg`, `username`, `text`) VALUES ('1', 'LEON', 'hello');"
    }


    public long getLastupdID() throws SQLException {
        long lastID = 0;
        stmt = dbCon.createStatement();
        rs = stmt.executeQuery("SELECT max(idmsg) FROM botusers");
        rs.next(); //КУРСОР НА ПЕРВУЮ СТРОКУ!!!
        lastID = rs.getLong(1);
        return lastID;
    }

    public void writeLOG(String msg) throws SQLException {
        stmt = dbCon.createStatement();
        stmt.executeUpdate("INSERT INTO logtable (message) VALUES ('" + msg + "');");
        stmt.close();
    }
}
