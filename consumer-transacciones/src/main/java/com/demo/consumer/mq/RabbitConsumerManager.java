package com.demo.consumer.mq;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.demo.consumer.api.GuardarTransaccionClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class RabbitConsumerManager {

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final GuardarTransaccionClient postClient;

    
    private final Set<String> idsProcesados = ConcurrentHashMap.newKeySet();

    public RabbitConsumerManager(String host, int port, String username, String password,
            GuardarTransaccionClient postClient) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.postClient = postClient;
    }

    public void iniciar() throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setAutomaticRecoveryEnabled(true);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.basicQos(1);

        List<String> bancos = Arrays.asList(
                "BANRURAL",
                "BAC",
                "GYT",
                "INDUSTRIAL",
                "FICOHSA",
                "BI"
        );

        
        channel.queueDeclare("cola_duplicados", true, false, false, null);

        ObjectMapper mapper = new ObjectMapper();

        for (String banco : bancos) {

            channel.queueDeclare(banco, true, false, false, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {

                String mensajeJson = new String(delivery.getBody(), StandardCharsets.UTF_8);
                long deliveryTag = delivery.getEnvelope().getDeliveryTag();

                try {
                    System.out.println("======================================");
                    System.out.println("Cola atendida: " + banco);
                    System.out.println("JSON original recibido:");
                    System.out.println(mensajeJson);

                    ObjectNode jsonNode = (ObjectNode) mapper.readTree(mensajeJson);

                    String idTransaccion = jsonNode.get("idTransaccion").asText();

                    System.out.println("ID de la solicitud procesada: " + idTransaccion);

                   
                    if (idsProcesados.contains(idTransaccion)) {

                        
                        enviarAColaDuplicados(channel, mensajeJson);

                       
                        channel.basicAck(deliveryTag, false);

                        System.out.println("Estado: DUPLICADA");
                        System.out.println("Cola destino: cola_duplicados");
                        System.out.println("======================================");
                        return;
                    }

                   
                    jsonNode.put("nombre", "Angello Escobar");
                    jsonNode.put("carnet", "0905-24-22482");
                    jsonNode.put("correo", "aescobarg21@miumg.edu.gt");

                    String nuevoJson = mapper.writeValueAsString(jsonNode);

                    System.out.println("JSON enviado al POST:");
                    System.out.println(nuevoJson);

                    int status = postClient.guardar(nuevoJson);

                    if (status == 200 || status == 201) {

                       
                        idsProcesados.add(idTransaccion);

                        channel.basicAck(deliveryTag, false);

                        System.out.println("Estado: PROCESADA");
                        System.out.println("Cola destino: POST /guardarTransacciones");
                        System.out.println("POST exitoso. ACK enviado. Status: " + status);

                    } else {

                        System.out.println("POST falló con status: " + status + ". Reintentando...");

                        int segundoIntento = postClient.guardar(nuevoJson);

                        if (segundoIntento == 200 || segundoIntento == 201) {

                            
                            idsProcesados.add(idTransaccion);

                            channel.basicAck(deliveryTag, false);

                            System.out.println("Estado: PROCESADA");
                            System.out.println("Cola destino: POST /guardarTransacciones");
                            System.out.println("POST exitoso en reintento. ACK enviado. Status: " + segundoIntento);

                        } else {

                            channel.basicNack(deliveryTag, false, true);

                            System.out.println("Estado: ERROR");
                            System.out.println("Cola destino: requeue");
                            System.out.println("Falló otra vez. NACK con requeue. Status: " + segundoIntento);
                        }
                    }

                    System.out.println("======================================");

                } catch (Exception e) {

                    channel.basicNack(deliveryTag, false, true);
                    System.out.println("Estado: ERROR");
                    System.out.println("Cola destino: requeue");
                    System.out.println("Error procesando mensaje: " + e.getMessage());
                    System.out.println("======================================");
                }
            };

            CancelCallback cancelCallback = consumerTag -> {
                System.out.println("Consumer cancelado: " + consumerTag);
            };

            channel.basicConsume(banco, false, deliverCallback, cancelCallback);

            System.out.println("Escuchando cola: " + banco);
        }

        System.out.println("Escuchando cola adicional: cola_duplicados");
        System.out.println("Consumer activo. Esperando mensajes...");
    }

    private void enviarAColaDuplicados(Channel channel, String mensajeJson) throws Exception {
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .contentType("application/json")
                .deliveryMode(2)
                .build();

        channel.basicPublish("", "cola_duplicados", props, mensajeJson.getBytes(StandardCharsets.UTF_8));
    }
}