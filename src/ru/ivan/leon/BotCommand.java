package ru.ivan.leon;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
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
        } catch (IOException e) {
            System.out.println("Ошибка при установлении соединения с telegram(BotCommand" + "offset = " + offset);
            e.printStackTrace();
        }
        return getData;
    }

    public void sendtoChat(long chatId, String text){
        String urlcommand = botAddress + "sendMessage" + "?chat_id=" + chatId + "&text="+ text + "";
        try {
            URL url = new URL(urlcommand);
            URLConnection urlcon = url.openConnection();
            urlcon.setRequestProperty("Content-Type", "charset=Windows-1251");
            BufferedReader in = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));
            in.read();
            in.close();
        } catch (MalformedURLException e) {
            System.out.println("Не получается ответить юзеру (BotCommand.sendtoChat)");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Не получается ответить юзеру (BotCommand.sendtoChat)");
            e.printStackTrace();
        }

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
            r.username = str.substring(1, str.indexOf(",")-1);

            str = str.substring(str.indexOf("\"chat\":{\"id\":")+ 12); //от этой подстроки и до конца строки
            r.chat_id = Long.parseLong(str.substring(1, str.indexOf(",")));

            str = str.substring(str.indexOf("\"text\":") + 7);
            //System.out.println("ПОСЛЕДНИЙ ШАГ = " +str);
            r.text = str.substring(1, str.indexOf("}")-1);
            //////////
            //String tmp = System.getProperty("console.encoding","Cp866");
            //tmp = r.text;//.getBytes(StandardCharsets.UTF_8);
            /////////

            dataarr.add(r);
    }
        return dataarr;
    }

}

