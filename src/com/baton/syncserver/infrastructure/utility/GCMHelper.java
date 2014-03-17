package com.baton.syncserver.infrastructure.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;

import com.baton.syncserver.infrastructure.database.Datastore;
import com.baton.syncserver.infrastructure.servlet.baseservice.ApiKeyInitializer;
import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.android.gcm.server.Message.Builder;

public class GCMHelper {

	protected final Logger logger = Logger.getLogger(getClass().getName());

	private Sender sender;

	private static final Executor threadPool = Executors.newFixedThreadPool(5);
	
	
	public GCMHelper(ServletConfig config) {
		super();
		sender = newSender(config);
	}

	/**
	 * Creates the {@link Sender} based on the servlet settings.
	 */
	protected Sender newSender(ServletConfig config) {
		String key = (String) config.getServletContext().getAttribute(
				ApiKeyInitializer.ATTRIBUTE_ACCESS_KEY);
		return new Sender(key);
	}
	
	public void asyncSend(List<String> partialDevices,Map<String,String> msg) {
		final Map<String,String> msgMap = msg;
		if(null==partialDevices||0==partialDevices.size())
			return;
		final List<String> devices =partialDevices;
		threadPool.execute(new Runnable() {

			public void run() {
				Builder msgBuilder = new Message.Builder();
				Iterator<Entry<String, String>> it = msgMap.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry<String,String> pairs = (Map.Entry<String,String>)it.next();
			        msgBuilder.addData(pairs.getKey(), pairs.getValue());
			        it.remove(); // avoids a ConcurrentModificationException
			    }
				Message message = msgBuilder.build();

				MulticastResult multiResult;
				try {
					multiResult = sender.send(message, devices, 5);
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Error posting messages", e);
					return;
				}
				List<Result> results = multiResult.getResults();
				// analyze the results
				for (int i = 0; i < devices.size(); i++) {
					String regId = devices.get(i);
					Result result = results.get(i);
					String messageId = result.getMessageId();
					if (messageId != null) {
						logger.fine("Succesfully sent message to device: "
								+ regId + "; messageId = " + messageId);
						String canonicalRegId = result
								.getCanonicalRegistrationId();
						if (canonicalRegId != null) {
							// same device has more than on registration id:
							// update it
							logger.info("canonicalRegId " + canonicalRegId);
							Datastore.updateRegistration(regId, canonicalRegId);
						}
					} else {
						String error = result.getErrorCodeName();
						if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
							// application has been removed from device -
							// unregister it
							logger.info("Unregistered device: " + regId);
							Datastore.unregister(regId);
						} else {
							logger.severe("Error sending message to " + regId
									+ ": " + error);
						}
					}
				}
			}
		});
	}
}
