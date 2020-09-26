//Name: Sanchit Jain
//UTA ID - 1001746569
//REF: https://www.udemy.com/course/java-socket-programming-build-a-chat-application/learn/lecture/5763190?components=slider_menu%2Cbuy_button%2Cdeal_badge%2Cdiscount_expiration%2Cprice_text%2Credeem_coupon#overview
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;

import java.io.*;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ChatClient {

	//String to hold username
	String username;

	//HashSet to hold list of uniques usernames
	volatile static Set<String> userNames = new HashSet();

	//UI declarations
	static JFrame chatWindow = new JFrame("Chat Application");

	static JTextArea chatArea = new JTextArea(22, 40);

	static JTextField textField = new JTextField(40);

	static JLabel blankLabel = new JLabel("           ");

	static JButton sendButton = new JButton("Send");

	//input from server
	static BufferedReader in;

	//output to server
	static PrintWriter out;

	static JLabel nameLabel = new JLabel("         ");

	static JButton close = new JButton("Close");

	ChatClient()

	{

		chatWindow.setLayout(new FlowLayout());

		chatWindow.add(nameLabel);

		chatWindow.add(new JScrollPane(chatArea));

		chatWindow.add(blankLabel);

		chatWindow.add(textField);

		chatWindow.add(sendButton);

		chatWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		chatWindow.setSize(475, 500);

		chatWindow.setVisible(true);

		chatWindow.add(close, BorderLayout.SOUTH);

		textField.setEditable(false);

		chatArea.setEditable(false);

		//action listener for send button
		sendButton.addActionListener(new Listener(this.username));

		//action listener for text field if enter is pressed
		textField.addActionListener(new Listener(this.username));

	}

	void startChat() throws Exception

	{

		//ip address hardcoded to localhost
		Socket soc = new Socket("localhost", 9806);

		in = new BufferedReader(new InputStreamReader(soc.getInputStream()));

		out = new PrintWriter(soc.getOutputStream(), true);
		
		// REF:https://www.tutorialspoint.com/how-to-add-action-listener-to-jbutton-in-java
		//action listener for close button
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				out.println("LEFT" + username);
				userNames.remove(username);
				System.exit(0);
			}
		});
		while (true)

		{

			String str = in.readLine();

			if (str.equals("NAMEREQUIRED"))

			{

				String name = JOptionPane.showInputDialog(

						chatWindow,

						"Enter a unique name:",

						"Name Required!!",

						JOptionPane.PLAIN_MESSAGE);

				out.println(name);

			}

			//if unique name is not entered
			else if (str.equals("NAMEALREADYEXISTS"))

			{

				String name = JOptionPane.showInputDialog(

						chatWindow,

						"Enter another name:",

						"Name Already Exits!!",

						JOptionPane.WARNING_MESSAGE);

				out.println(name);

			}

			//when user enters valid username
			else if (str.startsWith("NAMEACCEPTED"))

			{

				textField.setEditable(true);
				nameLabel.setText("You are logged in as: " + str.substring(12));
				this.username = str.substring(12);
				//request to show logs of active users to client
				getActiveUsersList();

			}

			//getting active userlist from server
			else if (str.startsWith("USERLIST")) {
				String names_str = str.substring(9);
				String username[] = names_str.split(",");
				System.out.println("inside userlist");
				userNames.clear();
				for (String text : username) {
					userNames.add(text);
				}
				//System.out.println("active users");
			}

			else if (str.startsWith("USERJOINED")) {
				String joinedUser = str.substring(10);
				chatArea.append(joinedUser + " joined the chat" + "\n");
			}

			else if (str.startsWith("USERLEFT")) {
				String leftUser = str.substring(8);
				chatArea.append(leftUser + " left the chat" + "\n");
			} else if (str.startsWith("USERSLOG")) {

				String names_str = str.substring(8);
				String usernames[] = names_str.split(",");
				userNames.clear();
				for (String text : usernames) {
					userNames.add(text);
				}
			//	System.out.println("active users in log");

				for (String text : userNames) {
				//	System.out.println(text);
					if (text.equals(username))
						continue;
					chatArea.append(text + " joined the chat" + "\n");
				}

			}

			else

			{

				chatArea.append(str + "\n");

			}

		}

	}

	public static void getActiveUsersList() throws IOException {
		out.println("REQUESTUSERS");
		String list = in.readLine();

	}

	public static void main(String[] args) throws Exception {

		// TODO Auto-generated method stub

		ChatClient client = new ChatClient();

		client.startChat();

	}

}

class Listener implements ActionListener

{
	String username;

	public Listener(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		ArrayList<String> options = new ArrayList<>();
		options.add("1-1");
		options.add("1-N");
		options.add("Broadcast");
		JList list = new JList(options.toArray());
		// This will provide a list of options to client, and client will the sending option
		String optionChosen = (String) JOptionPane.showInputDialog(ChatClient.chatWindow, "Choose sending option",
				"Menu", JOptionPane.PLAIN_MESSAGE, null, options.toArray(), list);


		//user will enter a single online username
		if (optionChosen.equals("1-1")) {
			String name = JOptionPane.showInputDialog(

					ChatClient.chatWindow,

					"Enter a  name:",

					"Name Required!!",

					JOptionPane.PLAIN_MESSAGE);
			if (ChatClient.userNames.contains(name)) {
				ChatClient.out.println("Me" + ":" + "1-1:" + name + ":" + ChatClient.textField.getText());

				ChatClient.chatArea.append("Me" + ":1-1:" + name + ":" + ChatClient.textField.getText() + "\n");
			} else {

				JOptionPane.showMessageDialog(ChatClient.chatWindow, "Recipient not valid or is offline");
			}
			ChatClient.textField.setText("");

			
		}
		//user will enter username with comma separation
		else if (optionChosen.equals("1-N")) {

			String name = JOptionPane.showInputDialog(

					ChatClient.chatWindow,

					"Enter recipient names comma separated:",

					"Names Required!!",

					JOptionPane.PLAIN_MESSAGE);
			String str[] = name.split(",");

			for (String str1 : str) {

				if (ChatClient.userNames.contains(str1)) {
					ChatClient.out.println("Me" + ":" + "1-N:" + str1 + ":" + ChatClient.textField.getText());

					ChatClient.chatArea.append("Me" + ":1-N:" + str1 + ":" + ChatClient.textField.getText() + "\n");
				} else {

					JOptionPane.showMessageDialog(ChatClient.chatWindow,
							"Recipient " + str1 + " not valid or is offline");
				}

			}
			ChatClient.textField.setText("");

		}

		//broadcast the message to all online users
		else if (optionChosen.equals("Broadcast")) {
			ChatClient.out.println("Me:Broadcast" + ":" + ChatClient.textField.getText());

		}
	}

}
