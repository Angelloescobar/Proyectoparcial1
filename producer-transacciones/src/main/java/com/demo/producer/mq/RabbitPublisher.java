package com.demo.producer.mq;

import java.nio.charset.StandardCharsets;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitPublisher {

    private final Connection connection;
    private final Channel channel;

    public RabbitPublisher(String host, int port, String username, String password) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setAutomaticRecoveryEnabled(true);

        this.connection = factory.newConnection();
        this.channel = connection.createChannel();
    }

    public void publicar(String nombreCola, String mensajeJson) throws Exception {
        channel.queueDeclare(nombreCola, true, false, false, null);

        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .contentType("application/json")
                .deliveryMode(2)
                .build();

        channel.basicPublish("", nombreCola, props, mensajeJson.getBytes(StandardCharsets.UTF_8));
        System.out.println("Enviado a cola: " + nombreCola);
    }

    public void cerrar() throws Exception {
        channel.close();
        connection.close();
    }
}