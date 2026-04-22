package org.DavidParada;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;

class HiloCliente extends Thread {
    private Socket socket;
    private int idCliente;

    public HiloCliente(Socket socket, int idCliente) {
        this.socket = socket;
        this.idCliente = idCliente;
    }

    public void run() {
        try (
                BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
        ) {
            String mensaje;

            while ((mensaje = entrada.readLine()) != null) {
                System.out.println("[Servidor] Atendiendo cliente " + idCliente + ": " + mensaje);

                Instant inicio = Instant.now();

                // Simulación de procesamiento
                Thread.sleep(1000);

                String respuesta = "Respuesta a cliente " + idCliente + ": " + mensaje.toUpperCase();
                salida.println(respuesta);

                Instant fin = Instant.now();
                long tiempo = Duration.between(inicio, fin).toMillis();

                System.out.println("Tiempo de procesamiento cliente " + idCliente + ": " + tiempo + " ms");
            }

        } catch (Exception e) {
            System.out.println("Cliente " + idCliente + " desconectado.");
        } finally {
            try {
                socket.close();
                System.out.println("Hilo cliente " + idCliente + " finalizado.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
