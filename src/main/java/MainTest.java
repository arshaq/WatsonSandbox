import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class MainTest {

    public static void main(String... args) {
        ConversationService service = new ConversationService("2017-07-24", "f8c72813-e7b4-4e9d-850e-a7aae6f1277c", "XztkSNGylvlx");
        String workspaceId = "f81af58c-1492-4a8e-8ffa-7f71f2b23f89";

        Map<String, Object> context = null;
        boolean fin = false;

        String input = "";
        do {
            MessageRequest request = new MessageRequest.Builder().inputText(input).context(context).build();
            MessageResponse response = service.message(workspaceId, request).execute();

            context = response.getContext();

            /*for(String s:response.getOutput().keySet()) {
                System.out.println("String " + s + " Object " + response.getOutput().get(s));
            }*/

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

        } while(!"fin".equalsIgnoreCase(input) && !fin);
    }
}
