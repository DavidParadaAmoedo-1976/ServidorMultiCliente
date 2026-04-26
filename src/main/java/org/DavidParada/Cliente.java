package org.DavidParada;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;

class Cliente {
    private static volatile Instant marcaTiempoEnvio;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket("localhost", 5000);
                BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.print(entrada.readLine() + " ");
            salida.println(teclado.readLine());

            new Thread(() -> {
                try {
                    String msg;
                    while ((msg = entrada.readLine()) != null) {

                        if (msg.startsWith("MSG|")) {
                            System.out.println("\nSERVIDOR: " + msg.substring(4));
                        } else if (msg.startsWith("DATA|")) {
                            Instant instanteRecibido = Instant.now();
                            String contenido = msg.substring(5);
                            String[] partes = contenido.split("\\|");
                            String textoServidor = partes[0].split("=", 2)[1];
                            long tiempoProcServer = Long.parseLong(partes[1].split("=", 2)[1]);
                            long rtt = (marcaTiempoEnvio != null)
                                    ? Duration.between(marcaTiempoEnvio, instanteRecibido).toMillis()
                                    : 0;

                            System.out.println("\n" + "_".repeat(60));
                            System.out.println("       Servidor respondió: " + textoServidor);
                            System.out.println("Procesamiento en servidor: " + tiempoProcServer + " ns");
                            System.out.println("  Tiempo total ida/vuelta: " + rtt + " ms");
                            System.out.println("_".repeat(60));
                        }
                        System.out.print("> ");
                    }
                } catch (IOException e) {
                    System.out.println("\nConexión cerrada por el servidor.");
                }
            }).start();

            String texto;
            while (true) {
                texto = teclado.readLine();
                if (texto == null) break;
                marcaTiempoEnvio = Instant.now();
                salida.println(texto);
                if (texto.equalsIgnoreCase("salir")) {
                    Thread.sleep(500);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }
}