package com.demo.consumer;

import com.demo.consumer.api.GuardarTransaccionClient;
import com.demo.consumer.mq.RabbitConsumerManager;

public class ConsumerApp {

    public static void main(String[] args) {

        String postUrl = "https://7e0d9ogwzd.execute-api.us-east-1.amazonaws.com/default/guardarTransacciones";

        try {

            GuardarTransaccionClient client = new GuardarTransaccionClient(postUrl);

            RabbitConsumerManager manager =
                    new RabbitConsumerManager("localhost", 5672, "guest", "guest", client);

            manager.iniciar();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}