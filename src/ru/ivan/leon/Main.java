package ru.ivan.leon;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main {

    //Идентификационные данные вынесены в БД
    static String botAddress;
    static String bdAddress;
    static String bdPort;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Start programm v:1.2"); //настроить лог в БД (logtable)
            //args = new String[]{"localhost", "50770"};
        if(args.length>0){
            bdAddress = args[0];
            bdPort = args[1];
            System.out.println("Address BD mysql from cmd: "+ bdAddress + ":" + bdPort);
        } else{
            bdAddress = "localhost";
            bdPort = "50770";
        }
        WorkThread wtr = new WorkThread(bdAddress, bdPort);
        Thread wtrTread = new Thread(wtr);
        wtrTread.start();
        wtrTread.join(); //Ждем завершения работы потока wtr
        System.out.println("Exit from programm");
    }

}

class WorkThread implements Runnable{

    static String botAddress;
    long lastidupdate = 0;
    static DbConnector mydatabase;
    static BotCommand bot;
    private String dbaddr, dbport;

    WorkThread(String addr, String port){
        dbaddr = addr;
        dbport = port;
    }

    @Override
    public void run() {
        boolean threadIsRun = true;
        try {
            init(dbaddr, dbport);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        while(threadIsRun){
            String data = bot.getDataBot(lastidupdate + 1); // Запрашиваем еще не обработанные сообщения
            ArrayList<RequestStruct> d = null; //разбираем данные от бота в структуру
            try {
                d = bot.parseData(data);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            for(int i = 0; i < d.size(); i++) {
                try {
                    mydatabase.writetoDB(d.get(i));
                } catch (SQLException e) {
                    System.out.println("Ошибка с записью в БД (Run())");
                }
                bot.sendtoChat(d.get(i).chat_id, "Сообщение: " + d.get(i).text + " принято ботом.");
                try {
                    String st = bot.searchusercommand(d.get(i), mydatabase);
                    if(!(st.equals(""))) {
                        bot.sendtoChat(d.get(i).chat_id, st);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            //Если было получено хоть одно сообщение в этом цикле - Обновляем индекс последнего апдейта
            if(d.size() >0) {
                lastidupdate = d.get(d.size() - 1).update_id; //в процессе работы актуальный update_id берем из запросов
                if((d.get(d.size() - 1).text.compareTo("Stopbot")) == 0) {
                    bot.sendtoChat(d.get(d.size() - 1).chat_id, "Bot has stopped!");
                    threadIsRun = false;
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            mydatabase.writeLOG("Завершение потока run().Выход из программы");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void init(String dbaddr, String dbport) throws SQLException {
        if(dbaddr != "") {
            mydatabase = new DbConnector("jdbc:mysql://" + dbaddr + ":" + dbport + "/myDBforbot", "myDBforbot");
        }else {
            mydatabase = new DbConnector("jdbc:mysql://localhost:3306/myDBforbot?enabledTLSProtocol=TLSv1.2", "myDBforbot.db3");
        }
        System.out.println("Connected to bot DBase:(init()) ====>" + mydatabase.connectToDB() +"<==== Address_db: "+ mydatabase.dbaddress);
        botAddress = mydatabase.getBotAddress();
        bot = new BotCommand(botAddress); //инициализация бота
        if (!bot.statusConnect){
            System.out.println("Ожидаем восстановления подключения к телеграму");
            bot.stateConnect(); //метод проверяет состояние подключени к телеграму.
        }
        try {
            lastidupdate = mydatabase.getLastupdID(); //id последнего обновления от бота(берем из БД при инициализации потока, запуске программы)
        } catch (SQLException e) {
            System.out.println("Ошибка с чтением lastupdate из БД (Run())");
        }
        mydatabase.writeLOG("Инициализация выполнена. init()");
    }
}

class RequestStruct{
    long update_id;
    long user_id;
    String username;
    long chat_id;
    String text;
}