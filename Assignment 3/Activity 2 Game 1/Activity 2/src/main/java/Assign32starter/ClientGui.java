package Assign32starter;

import java.awt.Dimension;

import org.json.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

public class ClientGui implements Assign32starter.OutputPanel.EventHandlers {
	JDialog frame;
	PicturePanel picPanel;
	OutputPanel outputPanel;
	String currentMess;

	Socket sock;
	OutputStream out;
	ObjectOutputStream os;
	BufferedReader bufferedReader;

	String host = "localhost";
	int port = 8080;


	public ClientGui(String host, int port) throws IOException {
		this.host = host; 
		this.port = port; 
	
		frame = new JDialog();
		frame.setLayout(new GridBagLayout());
		frame.setMinimumSize(new Dimension(500, 500));
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		picPanel = new PicturePanel();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0.25;
		frame.add(picPanel, c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 0.75;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		outputPanel = new OutputPanel();
		outputPanel.addEventHandlers(this);
		frame.add(outputPanel, c);

		picPanel.newGame(1);
		insertImage("img/hi.png", 0, 0);

		open();
		currentMess = "{'type': 'start'}";
		try {
			os.writeObject(currentMess);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String string = this.bufferedReader.readLine();
		System.out.println("Got a connection to server");
		JSONObject json = new JSONObject(string);
		outputPanel.appendOutput(json.getString("value"));

		close();

	}


	public void show(boolean makeModal) {
		frame.pack();
		frame.setModal(makeModal);
		frame.setVisible(true);
	}

	public void newGame(int dimension) {
		picPanel.newGame(1);
		outputPanel.appendOutput("Started new game with a " + dimension + "x" + dimension + " board.");
	}


	public boolean insertImage(String filename, int row, int col) throws IOException {
		System.out.println("Image insert");
		String error = "";
		try {
			if (picPanel.insertImage(filename, row, col)) {
				return true;
			}
			error = "File(\"" + filename + "\") not found.";
		} catch(PicturePanel.InvalidCoordinateException e) {
			error = e.toString();
		}
		outputPanel.appendOutput(error);
		return false;
	}

	@Override
	public void submitClicked() {
		try {
			open();
			System.out.println("submit clicked.. ");
			String name = "";
			String input = outputPanel.getInputText();
			final JSONObject request = new JSONObject();

			System.out.println("Received the JSON " + request);

			if(currentMess.equals("{'type': 'start'}")){
				request.put("type", "name");
				request.put("name", input);
				System.out.println(name);
				currentMess = "{'type': 'leaderboard'}";
				outputPanel.setInputText("");
			}

			else if(currentMess.equals("{'type': 'name'}")){
				request.put("type", "n_question");
				request.put("q_num", input);
				System.out.println( "sending----> " + request);
				outputPanel.setInputText("");
				currentMess =  "{'type': 'begin'}";
			}
			else if(currentMess.equals("{'type': 'leaderboard'}")){
				if(outputPanel.getInputText().equals("yes"))
				{
					String lead = "";
					request.put("type", "leaderboard");
					request.put("check", lead);
					System.out.println("sending----> " + request);
				}
				else {
					String lead = "no";
					request.put("type", "leaderboard");
					request.put("check", lead);
					System.out.println("sending----> " + request);
				}
				outputPanel.setInputText("");
				currentMess =  "{'type': 'name'}";
			}

			else if(currentMess.equals("{'type': 'begin'}")){
				request.put("type", "begin");
				request.put("payload", input);
				System.out.println( "sending----> " + request);
				outputPanel.setInputText("");

			}

			String string = "";
			try {
				os.writeObject(request.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				System.out.println("Waiting on response");
				string = this.bufferedReader.readLine();
				System.out.println(string);
			} catch (Exception e) {
				e.printStackTrace();
			}
			final JSONObject response = new JSONObject(string);
			if(response.getString("type").equals("error1")){
				outputPanel.appendOutput(response.getString("message"));
				currentMess =  "{'type': 'name'}";
			}
			if(response.getString("type").equals("error2")){
				outputPanel.appendOutput(response.getString("message"));
				currentMess =  "{'type': 'begin'}";
			}

			if (response.getString("type").equals("name")) {
				outputPanel.appendOutput("Hey " + response.getString("name") + "\nType yes if you want see leaderboard"
				+ "\nType no if you want to proceed with game");
			}
			if (response.getString("type").equals("leaderboard")) {
				System.out.println("leaderboard response");
				outputPanel.appendOutput( response.getString("score") + "\nLets play now! How many times would you like to play??");
			}
			else if(response.getString("type").equals("n_question")){
				outputPanel.appendOutput(response.getString("q_num"));
			}
			else if(response.getString("type").equals("begin")){
				if(response.getString("new_game").equals("new")){
					currentMess =  "{'type': 'start'}";
				}

				outputPanel.appendOutput(response.getString("Info!"));
				insertImage(response.getString("payload"), 0, 0);
			}

			close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void inputUpdated(String input) {
		if (input.equals("surprise")) {
			outputPanel.appendOutput("You found me!");
		}
	}

	public void open() throws UnknownHostException, IOException {
		this.sock = new Socket(host, port);

		this.out = sock.getOutputStream();
		this.os = new ObjectOutputStream(out);
		this.bufferedReader = new BufferedReader(new InputStreamReader(sock.getInputStream()));

	}
	
	public void close() {
        try {
            if (out != null)  out.close();
            if (bufferedReader != null)   bufferedReader.close(); 
            if (sock != null) sock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public static void main(String[] args) throws IOException {


		try {
			String host = "localhost";
			int port = 9000;

			ClientGui main = new ClientGui(host, port);
			main.show(true);

		} catch (Exception e) {e.printStackTrace();}



	}
}
