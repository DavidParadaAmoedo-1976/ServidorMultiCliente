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
    private String id;
    private String nombre;
    private PrintWriter salida;
    private long tiempoTotalProcesamiento = 0;
    private int totalMensajes = 0;
    private Instant inicioSesion;

    public HiloCliente(Socket socket, String id) {
        this.socket = socket;
        this.id = id;
    }

    @Override
    public void run() {
        inicioSesion = Instant.now();
        try (
                BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            salida = new PrintWriter(socket.getOutputStream(), true);
            salida.println("Introduce tu nombre: ");
            nombre = entrada.readLine();
            if (nombre == null || nombre.isBlank()) {
                nombre = id;
            }
            System.out.println("Cliente conectado: " + nombre);

            String mensaje;
            while ((mensaje = entrada.readLine()) != null) {
                Instant inicioProc = Instant.now();
                if (mensaje.equalsIgnoreCase("salir")) {
                    salida.println("DATA|RESPUESTA=Conexión cerrada correctamente|TIEMPO_PROCESAMIENTO_NANOSEGUNDOS=0");
                    break;
                }
                System.out.println(nombre + "-> " + mensaje);
                String respuesta = "Recibido: " + mensaje;
                Instant finProc = Instant.now();
                long tiempoProc = Duration.between(inicioProc, finProc).toNanos();
                tiempoTotalProcesamiento += tiempoProc;
                totalMensajes++;
                salida.println("DATA|RESPUESTA=" + respuesta + "|TIEMPO_PROCESAMIENTO_NANOSEGUNDOS=" + tiempoProc);
            }

        } catch (IOException e) {
            System.out.println("Error con cliente " + nombre);
        } finally {
            cerrar();
            mostrarResumen();
        }
    }

    public void enviarMensaje(String msg) {
        if (salida != null) {
            salida.println(msg);
        }
    }

    private void cerrar() {
        try {
            ServidorMultiCliente.clientes.remove(this);
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println(nombre + " se ha desconectado");
        } catch (IOException ignored) {
        }
    }

    private void mostrarResumen() {
        long duracion = Duration.between(inicioSesion, Instant.now()).toSeconds();

        System.out.println("\n      * RESUMEN DEL CLIENTE *");
        System.out.println("                      Nombre: " + nombre);
        System.out.println("Duración conexión (segundos): " + duracion);
        System.out.println("              Total mensajes: " + totalMensajes);

        if (totalMensajes > 0) {
            double promedio = (tiempoTotalProcesamiento / (double) totalMensajes) / 1000.0;
            System.out.println("Tiempo promedio de procesamiento (microsegundos): " + promedio);
        }
        System.out.println("_".repeat(50) + "\n");
    }
}