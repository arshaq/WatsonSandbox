import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.net.URLConnection;

public class MainTest {

    private static Map<String, Object> outputMap;

    public static void main(String... args) throws IOException {

        String input = "";
        String context = "{}";
        JSONObject textJson = new JSONObject().put("text", input);
        JSONObject dataJson = new JSONObject().put("input", textJson);
//        dataJson.put("context", context);
//        List<JSONObject> dataList = new ArrayList<JSONObject>();
//        dataList.add(textJson);
//        dataList.add(contextJson);


        System.out.println(dataJson.toString());
        URL url = null;
        try {
            url = new URL("https://gateway.watsonplatform.net/conversation/api/v1/workspaces/f81af58c-1492-4a8e-8ffa-7f71f2b23f89/message?version=2017-05-26");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection conn = null;

        do {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-type", "application/json");
            conn.setRequestProperty("Authorization", new String("Basic " + Base64.getEncoder().encodeToString("f8c72813-e7b4-4e9d-850e-a7aae6f1277c:XztkSNGylvlx".getBytes())));
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            osw.write(dataJson.toString());
            osw.flush();
            conn.disconnect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String output = reader.readLine();

//            System.out.println(output);
            JSONObject jsonObject = new JSONObject(output);
            outputMap = parseJSONObjectToMap(jsonObject);
            System.out.println("Bot: "+ ((List)((Map)outputMap.get("output")).get("text")).get(0));

            System.out.print("User: ");
            try {
                input = (new BufferedReader(new InputStreamReader(System.in))).readLine().trim();
            } catch (IOException e) {
                e.printStackTrace();
            }

            textJson.put("text", input);
            dataJson.put("context", outputMap.get("context"));

            System.out.println(dataJson.toString());
            System.out.println(input);
        } while (!input.equalsIgnoreCase("fin"));


 //       System.out.println("Context: "+ new JSONObject().put("context", outputMap.get("context")));
/*
        ConversationService service = new ConversationService("2017-07-24", "f8c72813-e7b4-4e9d-850e-a7aae6f1277c", "XztkSNGylvlx");
        String workspaceId = "f81af58c-1492-4a8e-8ffa-7f71f2b23f89"

        Map<String, Object> context = null;
        boolean fin = false;

        String input = "";
        do {
            MessageRequest request = new MessageRequest.Builder().inputText(input).context(context).build();
            MessageResponse response = service.message(workspaceId, request).execute();

            context = response.getContext();

            *//*for(String s:response.getOutput().keySet()) {
                System.out.println("String " + s + " Object " + response.getOutput().get(s));
            }*//*

            List<String> responseText = (List<String>) response.getOutput().get("text");
            if (responseText.size() > 0) {
                System.out.println("Bot: " + responseText.get(0));
            }

            if(context.get("AccountNumber") != null) {
                if(context.get("Answer") == null){
                    System.out.println("Got the account number = " + ((Double)context.get("AccountNumber")).intValue());
                   // context.put("Answer", "Balance is Rs xxxx");
                    fin = true;
                } else {
//                    fin = true;
                }
            } else {
                System.out.print("User: ");
                try {
                    input = (new BufferedReader(new InputStreamReader(System.in))).readLine().trim();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } while(!"fin".equalsIgnoreCase(input) && !fin);*/
    }

    public static Map<String,Object> parseJSONObjectToMap(JSONObject jsonObject) throws JSONException{
        Map<String, Object> mapData = new HashMap<String, Object>();
        Iterator<String> keysItr = jsonObject.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = jsonObject.get(key);

            if(value instanceof JSONArray) {
                value = parseJSONArrayToList((JSONArray) value);
            }else if(value instanceof JSONObject) {
                value = parseJSONObjectToMap((JSONObject) value);
            }
            mapData.put(key, value);
        }
        return mapData;
    }

    public static List<Object> parseJSONArrayToList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = parseJSONArrayToList((JSONArray) value);
            }else if(value instanceof JSONObject) {
                value = parseJSONObjectToMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}
