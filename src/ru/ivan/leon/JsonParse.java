package ru.ivan.leon;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class JsonParse {

    long update_id = 0;
    long id_chat;
    String user_name;
    String text;



    public void parseString(String data){
        ArrayList<JSONObject> message = new ArrayList<JSONObject>();
        //System.out.println("Полученный ответ :\n" + data);
        String d = data.substring(data.indexOf("["), data.indexOf("]") +1);
        System.out.println("После обработки :\n" + d);
        JSONArray arr = new JSONArray(d);
        System.out.println("Количество записей: " + arr.length());

        for(int i = 0; i < arr.length(); i++){
            JSONObject o = arr.getJSONObject(i);
            //System.out.println(o.optString("update_id"));
            System.out.println("TESTS::::::");
            System.out.println(o.toString());
            //System.out.println(o.getJSONArray("message"));
            //System.out.println(o.getJSONArray("update_id"));
            //System.out.println("message = " +(String) o.getString("message"));
            //System.out.println("user = " + o.getString("chat"));
           // System.out.println("text msg = " + o.getString("text"));
            //System.out.println(o.getJSONArray("message"));
        }


    }

    //пишем в базу все сообщения, апдейты
    public boolean writeToBD(JSONObject... obj){
        boolean res = false;



        return res;
    }

    //получить из базы последний апдейт полученный для запроса новых
    public long getLastUpdateid(){

        return update_id;
    }
}
