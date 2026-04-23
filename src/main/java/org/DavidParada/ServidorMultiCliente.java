package org.DavidParada;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class ServidorMultiCliente {
    public static Set<HiloCliente> clientes = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        int puerto = 5000;
        System.out.println("Servidor iniciado en el puerto: " + puerto);

        new Thread(() -> {
            try {
                BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
                String mensaje;
                System.out.print("> ");
                while ((mensaje = teclado.readLine()) != null) {
                    for (HiloCliente c : clientes) {
                        c.enviarMensaje("MSG|" + mensaje);
                    }
                    System.out.print("> ");
                }
            } catch (Exception e) {
                System.out.println("Error en consola del servidor");
            }
        }).start();

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {

            int contador = 0;

            while (true) {
                Socket socket = serverSocket.accept();
                contador++;

                HiloCliente cliente = new HiloCliente(socket, "Cliente-" + contador);
                clientes.add(cliente);
                cliente.start();
            }
        } catch (Exception e) {
            System.out.println("Error servidor: " + e.getMessage());
        }
    }
}