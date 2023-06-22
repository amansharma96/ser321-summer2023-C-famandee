/**
  File: Performer.java
  Author: Student in Fall 2020B
  Description: Performer class in package taskone.
*/

package taskone;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;

/**
 * Class: Performer 
 * Description: Threaded Performer for server tasks.
 */
class Performer {

    private StringList state;
    private Socket conn;

    public Performer(Socket sock, StringList strings) {
        this.conn = sock;
        this.state = strings;
    }

    public JSONObject add(String str) {
        JSONObject json = new JSONObject();
        json.put("ok", true);
        json.put("type", "add");
        state.add(str);
        json.put("data", state.toString());
        return json;
    }

    public JSONObject clear() {
        JSONObject json = new JSONObject();
        json.put("ok", true);
        json.put("type", "clear");
        state.clear();
        json.put("data", state.toString());
        return json;
    }

    public JSONObject find(String str) {
    	JSONObject json = new JSONObject();
    	json.put("ok", true);
    	json.put("type", "find");
    
    	int index = state.find(str);
    	if (index >= 0) {
        json.put("data", "String found at index: " + index);
    	} else {
        json.put("data", "String not found");
    	}
   	 return json;
    }

    public JSONObject display() {
        JSONObject json = new JSONObject();
        json.put("ok", true);
        json.put("type", "display");
        json.put("data", state.toString());
        return json;
    }

    public JSONObject sort() {
        JSONObject json = new JSONObject();
        json.put("ok", true);
        json.put("type", "sort");
        state.sort();
        json.put("data", state.toString());
        return json;
    }

    public JSONObject prepend(int index, String str) {
        JSONObject json = new JSONObject();
        json.put("ok", true);
        json.put("type", "prepend");
        state.prepend(index, str);
        json.put("data", state.toString());
        return json;
    }

    public JSONObject quit() {
        JSONObject json = new JSONObject();
        json.put("ok", true);
        json.put("type", "quit");
        return json;
    }

    public static JSONObject error(String err) {
        JSONObject json = new JSONObject();
        json.put("ok", false);
        json.put("message", err);
        return json;
    }

    public void doPerform() {
        boolean quit = false;
        OutputStream out = null;
        InputStream in = null;
        try {
            out = conn.getOutputStream();
            in = conn.getInputStream();
            System.out.println("Server connected to client:");
            while (!quit) {
                byte[] messageBytes = NetworkUtils.receive(in);
                JSONObject message = JsonUtils.fromByteArray(messageBytes);
                JSONObject returnMessage = new JSONObject();

                int choice = message.getInt("selected");
                switch (choice) {
                    case 1: // add
                        String addStr = message.getString("data");
                        returnMessage = add(addStr);
                        break;
                    case 2: // clear
                        returnMessage = clear();
                        break;
                    case 3: // find
                        String findStr = message.getString("data");
                        returnMessage = find(findStr);
                        break;
                    case 4: // display
                        returnMessage = display();
                        break;
                    case 5: // sort
                        returnMessage = sort();
                        break;
                    case 6: // prepend
                        String[] prependData = message.getString("data").split(" ");
                        int index = Integer.parseInt(prependData[0]);
                        String prependStr = prependData[1];
                        returnMessage = prepend(index, prependStr);
                        break;
                    case 0: // quit
                        returnMessage = quit();
                        quit = true;
                        break;
                    default:
                        returnMessage = error("Invalid request type.");
                }

                NetworkUtils.send(out, returnMessage.toString().getBytes());
            }
            System.out.println("Server disconnected from client.");
        } catch (IOException e) {
            System.out.println("IO Error: " + e.getMessage());
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
                conn.close();
            } catch (IOException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
