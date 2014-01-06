package com.tweet.read;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;

public class Reading {

	private static final String USER_PROMPT = "Enter your <username>";
	private String[] mConsumerQueueNames = null;

	/**
	 * Overloaded Constructor
	 * 
	 * @param consumerQueueNames
	 *            : Array of all the consumerQueues
	 */
	public Reading(String[] consumerQueueNames) {
		mConsumerQueueNames = consumerQueueNames;
	}

	/**
	 * Creates a new thread and executes the run method
	 */
	public void start() {
		Thread thread = new Thread(new Runnable() {

			public void run() {
				Reading.this.run();
			}
		});
		thread.setName("Topic Dispatcher");
		thread.start();
	}

	/**
	 * Iterates over the consumer queues Looks up the JNDI for the Queue and
	 * Opens a connection to the queue
	 */
	private void run() {
		Queue q = null;
		Connection connection = null;

		for (int index = 0; index < mConsumerQueueNames.length; ++index) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("jms/");
			stringBuilder.append(mConsumerQueueNames[index]);
			stringBuilder.append("Queue");
			String queueJNDI = stringBuilder.toString();
			try {
				InitialContext ctx = new InitialContext();
				q = (Queue) ctx.lookup(queueJNDI);

				ConnectionFactory cf = (ConnectionFactory) ctx
						.lookup("jms/ConnectionFactory");
				connection = cf.createConnection();
				Session session = connection.createSession(true,
						Session.AUTO_ACKNOWLEDGE);
				MessageConsumer consumer = session.createConsumer(q);
				// consumer.setMessageListener(new TextMessageListener());
				consumer.setMessageListener(new MapMessageListener());
				connection.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//
		System.out.println(USER_PROMPT);
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		String user_input_string;
		user_input_string = r.readLine();
		if (user_input_string != null && !user_input_string.equals("")) {
			if (user_input_string.equalsIgnoreCase("Bob")) {
				String[] queueNames = { user_input_string.trim(), "Alice" };
				new Reading(queueNames).start();

			} else {
				String[] queueNames = { user_input_string.trim() };
				new Reading(queueNames).start();

			}
		}

	}

}
