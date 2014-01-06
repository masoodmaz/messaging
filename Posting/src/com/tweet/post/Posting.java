package com.tweet.post;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

public class Posting {
	public static final String USER_PROMPT = "Enter text in the format username->yourmessage";

	/**
	 * Posts Message to the Respective JMS Queues
	 * 
	 * @param userName
	 *            : Name of the User
	 * @param userMessage
	 *            : Message of the User
	 * @throws Exception
	 */
	public static void postMessage(String userName, String userMessage)
	throws Exception {
		Connection connection = null;
		try {
			Context ctx = new InitialContext();
			String topicJndi = "jms/" + userName + "Queue";
			Queue queue = (Queue) ctx.lookup(topicJndi);
			ConnectionFactory cf = (ConnectionFactory) ctx
			.lookup("jms/ConnectionFactory");
			connection = cf.createConnection();
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);

			MapMessage message = session.createMapMessage();
			message.setString("UserName", userName);
			message.setString("UserTweet", userMessage);
			message.setLong("Timestamp", System.currentTimeMillis());
			// create a blank output separator
			System.out.println("");

			MessageProducer messageProducer = session.createProducer(queue);
			messageProducer.send(message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
				e.printStackTrace();
			}
			// The System.exit(0) code below is a workaround for a bug GlassFish
			// JMS server
			System.exit(0);
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		System.out.println(USER_PROMPT);
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		String user_input_string;
		user_input_string = r.readLine();
		if (user_input_string != null && !user_input_string.equals("")) {
			String usertweet = user_input_string.substring(
					user_input_string.indexOf("->") + 2,
					user_input_string.length());
			String username = user_input_string.substring(0,
					user_input_string.indexOf("->"));
			postMessage(username, usertweet);

		}
	}
}
