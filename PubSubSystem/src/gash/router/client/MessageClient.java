/**
 * Copyright 2016 Gash.
 *
 * This file and intellectual content is protected under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package gash.router.client;

import routing.Pipe.Route;

/**
 * front-end (proxy) to our service - functional-based
 * 
 * @author gash
 * 
 */
public class MessageClient {
	// track requests
	private long curID = 0;
	public CommConnection commConnection;

	public MessageClient(String host, int port) {
		this.commConnection=new CommConnection(host,port);
		init(host, port);	
	}

	private void init(String host, int port) {
		commConnection.initConnection(host, port);
	}

	public void addListener(CommListener listener) {
		commConnection.getInstance().addListener(listener);
	}

	public void ping() {
		// construct the message to send
		Route.Builder rb = Route.newBuilder();
		rb.setId(nextId());
		rb.setPath("/ping");
		rb.setPayload("ping");

		try {
			// direct no queue
			// CommConnection.getInstance().write(rb.build());

			// using queue
			commConnection.getInstance().enqueue(rb.build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void postMessage(String msg) {
		// construct the message to send
		Route.Builder rb = Route.newBuilder();
		rb.setId(nextId());
		rb.setPath("/message");
		rb.setPayload(msg);

		try {
			System.out.println("My ip is "+commConnection.host+"My port is "+commConnection.port);
			commConnection.getInstance().enqueue(rb.build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void release() {
		commConnection.getInstance().release();
	}

	/**
	 * Since the service/server is asychronous we need a unique ID to associate
	 * our requests with the server's reply
	 * 
	 * @return
	 */
	private synchronized long nextId() {
		return ++curID;
	}
}
