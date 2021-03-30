package org.geektimes.rest.demo;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

public class RestClientDemo {

    public static void main(String[] args) {
        Client client = ClientBuilder.newClient();
//        Response response = client
//                .target("http://127.0.0.1:8080/hello/world")      // WebTarget
//                .request() // Invocation.Builder
//                .get();                                     //  Response
//
//        String content = response.readEntity(String.class);
//        System.out.println(content);

        Response postResponse = client.target("http://127.0.0.1:8080/echo")
                .request()
                .post(Entity.json("{\"value\":\"Hello,World\"}"));

        String echo = postResponse.readEntity(String.class);
        System.out.println(echo);
    }
}
