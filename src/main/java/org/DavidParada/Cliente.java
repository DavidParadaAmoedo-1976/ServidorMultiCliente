package org.DavidParada;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;

class Cliente {
    public static void main(String[] args) {
        String host = "localhost";
        int puerto = 5000;

        try (
                Socket socket = new Socket(host, puerto);
                BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
        ) {
            String mensaje;

            while (true) {
                System.out.print("Mensaje: ");
                mensaje = teclado.readLine();

                Instant envioInicio = Instant.now();
                salida.println(mensaje);

                String respuesta = entrada.readLine();
                Instant envioFin = Instant.now();

                long tiempoRespuesta = Duration.between(envioInicio, envioFin).toMillis();

                System.out.println("Servidor: " + respuesta);
                System.out.println("Tiempo de respuesta: " + tiempoRespuesta + " ms");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
