package edu.fudan.se.abc;

import javax.xml.ws.Endpoint;

import edu.fudan.se.MyThreadPool;

public class RequestImagePublisher {

	private Endpoint endpoint;

    public static void main(String[ ] args) {
    	RequestImagePublisher self = new RequestImagePublisher();
        self.create_endpoint();
        self.configure_endpoint();
        self.publish();
    }
    private void create_endpoint() {
        endpoint = Endpoint.create(new RequestImageImpl());
    }
    private void configure_endpoint() {
        endpoint.setExecutor(new MyThreadPool());
    }
    private void publish() {
        int port = 9999;
        String url = "http://10.131.253.172:" + port + "/bimg";
        endpoint.publish(url);
        System.out.println("Publishing TimeServer on port " + port);
    }

}
