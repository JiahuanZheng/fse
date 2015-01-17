package edu.fudan.se;

import javax.xml.ws.Endpoint;

public class ImageReceiverPublisher {

	private Endpoint endpoint;

    public static void main(String[ ] args) {
    	ImageReceiverPublisher self = new ImageReceiverPublisher();
        self.create_endpoint();
        self.configure_endpoint();
        self.publish();
    }
    private void create_endpoint() {
        endpoint = Endpoint.create(new ImageReceiverImpl());
    }
    private void configure_endpoint() {
        endpoint.setExecutor(new MyThreadPool());
    }
    private void publish() {
        int port = 8888;
        String url = "http://10.131.253.172:" + port + "/img";
        endpoint.publish(url);
        System.out.println("Publishing TimeServer on port " + port);
    }

}
