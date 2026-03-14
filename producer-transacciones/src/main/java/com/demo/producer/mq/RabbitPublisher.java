package com.demo.producer.mq;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.demo.producer.model.Transaccion;
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

    public void publicar(String nombreCola, String mensajeJson, Transaccion transaccion) throws Exception {

        Map<String, Object> args = new HashMap<>();
        args.put("x-max-priority", 10);

        channel.queueDeclare(nombreCola, true, false, false, args);

        int prioridad = calcularPrioridad(transaccion);

        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .contentType("application/json")
                .deliveryMode(2)
                .priority(prioridad)
                .build();

        channel.basicPublish("", nombreCola, props, mensajeJson.getBytes(StandardCharsets.UTF_8));

        String tipoPrioridad = prioridad >= 8 ? "ALTA" : "NORMAL";

        System.out.println("Enviado a cola: " + nombreCola
                + " | TX: " + transaccion.idTransaccion
                + " | Prioridad: " + tipoPrioridad
                + " (" + prioridad + ")");
    }

    private int calcularPrioridad(Transaccion transaccion) {
        try {
            String numero = transaccion.idTransaccion.replaceAll("\\D", "");
            int valor = Integer.parseInt(numero);

            if (valor % 2 == 0) {
                return 9; 
            } else {
                return 4; 
            }

        } catch (Exception e) {
            return 4;
        }
    }

    public void cerrar() throws Exception {
        channel.close();
        connection.close();
    }
}