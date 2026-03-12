package com.demo.producer;

import java.util.HashMap;
import java.util.Map;

import com.demo.producer.api.TransaccionesGetClient;
import com.demo.producer.model.LoteTransacciones;
import com.demo.producer.model.Transaccion;
import com.demo.producer.mq.RabbitPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProducerApp {

    public static void main(String[] args) {

        String getUrl = "https://hly784ig9d.execute-api.us-east-1.amazonaws.com/default/transacciones";

        String rabbitHost = "localhost";
        int rabbitPort = 5672;
        String rabbitUser = "guest";
        String rabbitPass = "guest";

        ObjectMapper mapper = new ObjectMapper();
        RabbitPublisher publisher = null;

        try {
            TransaccionesGetClient client = new TransaccionesGetClient(getUrl);
            LoteTransacciones lote = client.obtenerTransacciones();

            publisher = new RabbitPublisher(rabbitHost, rabbitPort, rabbitUser, rabbitPass);

            System.out.println("======================================");
            System.out.println("Lote recibido: " + lote.loteId);
            System.out.println("Fecha generación: " + lote.fechaGeneracion);
            System.out.println("TOTAL TRANSACCIONES: " + lote.transacciones.size());
            System.out.println("======================================");

            Map<String, Integer> conteoPorBanco = new HashMap<>();

            for (Transaccion t : lote.transacciones) {
                String banco = t.bancoDestino;
                String json = mapper.writeValueAsString(t);

                publisher.publicar(banco, json);

                conteoPorBanco.put(banco, conteoPorBanco.getOrDefault(banco, 0) + 1);

                System.out.println("Enviado a cola: " + banco + " | TX: " + t.idTransaccion);
            }

            System.out.println("======================================");
            System.out.println("RESUMEN POR BANCO:");
            for (Map.Entry<String, Integer> entry : conteoPorBanco.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            System.out.println("Producer finalizado.");
            System.out.println("======================================");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (publisher != null) {
                    publisher.cerrar();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}