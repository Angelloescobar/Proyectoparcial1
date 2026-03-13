# 🧪 Proyecto: Procesamiento de Transacciones Bancarias con RabbitMQ y Java

Video explicativo del proyecto: https://drive.google.com/file/d/1ZgN2vL7gDDo86hDatz8ZGiWlg1scV9Cg/view?usp=sharing

## 📖 Descripción
Este proyecto implementa un sistema distribuido para el procesamiento de transacciones bancarias utilizando **Java, Maven y RabbitMQ**, aplicando el patrón **Producer–Consumer**.

El sistema obtiene transacciones desde una API externa, las distribuye en colas de RabbitMQ según el banco destino y posteriormente un consumidor procesa cada transacción enviándola a otra API para su almacenamiento.

Este enfoque permite desacoplar el sistema, mejorar la escalabilidad y garantizar el procesamiento independiente por cada entidad bancaria.

---

# 🏗 Arquitectura del Sistema

El sistema sigue el siguiente flujo:

API (GET transacciones)  
        │  
        ▼  
Producer (Java)  
Obtiene transacciones  
        │  
        ▼  
RabbitMQ  
Colas por banco destino  
        │  
        ▼  
Consumer (Java)  
Procesa transacciones  
        │  
        ▼  
API (POST guardarTransacciones)

---

# ⚙ Tecnologías Utilizadas

- Java 11  
- Maven  
- RabbitMQ  
- Jackson (procesamiento JSON)  
- HTTP Client (Java)  
- Eclipse IDE  

---

# 🧩 Componentes del Sistema

## Producer

El **Producer** se encarga de:

1. Consumir la API de transacciones mediante un **GET**.
2. Convertir las transacciones recibidas a formato JSON.
3. Analizar el campo **bancoDestino**.
4. Publicar cada transacción en la cola correspondiente de RabbitMQ.

Cada banco tiene su propia cola para permitir procesamiento independiente.

Ejemplo de colas utilizadas:

- BANRURAL  
- BAC  
- BI  
- GYT  

---

## RabbitMQ

RabbitMQ funciona como sistema de mensajería que permite:

- Desacoplar el producer del consumer.
- Distribuir las transacciones por banco.
- Garantizar que las transacciones no se pierdan.

Las colas utilizadas en el sistema son:

BANRURAL  
BAC  
BI  
GYT  

Cada cola recibe aproximadamente **25 transacciones**, para un total de **100 transacciones procesadas**.

---

## Consumer

El **Consumer** se encarga de:

1. Escuchar múltiples colas en RabbitMQ.
2. Recibir cada mensaje de transacción.
3. Agregar los atributos requeridos al JSON:

nombre: "Angello Escobar"  
carnet: "0905-24-22482"

4. Enviar la transacción mediante **POST** a la API de almacenamiento.
5. Confirmar el procesamiento mediante **ACK**.

### Manejo de errores

El consumidor implementa:

- ACK manual  
- Reintento automático  
- NACK con requeue si falla el procesamiento  

Esto garantiza que ninguna transacción se pierda.

---

# 🌐 APIs Utilizadas

## Obtener Transacciones

GET  
https://hly784ig9d.execute-api.us-east-1.amazonaws.com/default/transacciones

Esta API devuelve un lote de transacciones bancarias que luego son distribuidas en las colas según el banco destino.

---

## Guardar Transacciones

POST  
https://7e0d9oqwdz.execute-api.us-east-1.amazonaws.com/default/guardarTransacciones

Esta API recibe las transacciones procesadas desde el consumer.

---

# 📊 Flujo de Procesamiento

1. El **Producer** obtiene transacciones desde la API mediante GET.
2. Cada transacción se envía a RabbitMQ según el banco destino.
3. RabbitMQ distribuye los mensajes en las colas correspondientes.
4. El **Consumer** escucha todas las colas.
5. El Consumer recibe cada mensaje.
6. Se agregan los campos **nombre** y **carnet** al JSON.
7. El Consumer envía la transacción al endpoint POST.
8. Si el POST responde **200 o 201**, se envía **ACK** a RabbitMQ.
9. Si ocurre un error, el mensaje se reintenta.

---

# ▶ Cómo Ejecutar el Proyecto

## 1️⃣ Iniciar RabbitMQ

Abrir RabbitMQ Management:

http://localhost:15672

Credenciales por defecto:

usuario: guest  
password: guest  

---

## 2️⃣ Ejecutar el Consumer

Desde Eclipse ejecutar:

ConsumerApp.java

El consumer quedará escuchando las colas.

---

## 3️⃣ Ejecutar el Producer

Desde Eclipse ejecutar:

ProducerApp.java

El producer enviará las transacciones a RabbitMQ.

---

## 4️⃣ Verificar en RabbitMQ

Ir a:

Queues and Streams

Ahí se podrá observar el flujo de mensajes por cada banco.

---

# 📌 Características Implementadas

✔ Distribución automática por banco  
✔ Procesamiento independiente por entidad  
✔ Uso de colas RabbitMQ  
✔ Producer–Consumer pattern  
✔ Confirmación de mensajes (ACK manual)  
✔ Reintento automático en fallos  
✔ Manejo de errores  
✔ Integración con APIs externas  
✔ Modificación dinámica del JSON antes del POST  

---

# 👨‍💻 Autor

Angello Escobar  
Carnet: 0905-24-22482

---

# 📎 Notas

Este proyecto demuestra cómo utilizar **RabbitMQ para desacoplar sistemas** y distribuir procesamiento entre diferentes consumidores, garantizando que las transacciones se procesen de forma confiable sin pérdida de información.
