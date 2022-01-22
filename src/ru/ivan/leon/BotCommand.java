package ru.ivan.leon;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class BotCommand {

    String botAddress;

    BotCommand(String url){
        botAddress = url;
    }

    public String getDataBot(long offset){
        String getData = "";
        URL mybot = null;
        try {
            mybot = new URL(botAddress + "getupdates" + "?offset=" + offset);
            URLConnection tlg = mybot.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(tlg.getInputStream()));
            String inputData = "";
            while ((inputData = in.readLine())!=null){
                getData = getData + inputData + '\n';
            }

            /*
            if(getData.length() > 5) {
                parseData(getData);
            } else {
                System.out.println("Новых сообщений нет");
            }
            */
        } catch (IOException e) {
            System.out.println("Ошибка при установлении соединения с telegram(BotCommand" + "offset = " + offset);
            e.printStackTrace();
        }
        return getData;
    }

    public void storyToBD(){

    }

    public ArrayList<RequestStruct> parseData(String str) {
        ArrayList<RequestStruct> dataarr = new ArrayList<RequestStruct>();

        while (str.indexOf("update_id") != -1) {
            RequestStruct r = new RequestStruct();

            int indexIDst = str.indexOf("update_id");
            str = str.substring(indexIDst);
            int indexIDend = str.indexOf(",");
            String updID = str.substring(11, indexIDend);
            r.update_id = Long.parseLong(updID);
            System.out.println("Update Id = " + updID + " convert to Long = " + r.update_id);

            str = str.substring(str.indexOf(updID) + updID.length() + 1);
            //System.out.println(str);
            str = str.substring(str.indexOf("\"id\":")+ 6);
            r.user_id = Long.parseLong(str.substring(0, str.indexOf(",")));

            str = str.substring(str.indexOf("\"username\":") + 11);
            r.username = str.substring(1, str.indexOf(","));

            str = str.substring(str.indexOf("\"chat\":{\"id\":")+ 13); //от этой подстроки и до конца строки
            r.chat_id = Long.parseLong(str.substring(1, str.indexOf(",")));

            str = str.substring(str.indexOf("\"text\":") + 7);
            //System.out.println("ПОСЛЕДНИЙ ШАГ = " +str);
            r.text = str.substring(1, str.indexOf("}"));
            dataarr.add(r);
    }
        return dataarr;
    }

}

