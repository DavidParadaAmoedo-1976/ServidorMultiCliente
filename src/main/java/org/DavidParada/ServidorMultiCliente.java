package org.DavidParada;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class ServidorMultiCliente {
    public static void main(String[] args) {
        int puerto = 5000;
        System.out.println("Servidor iniciado...");

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            int contadorClientes = 0;

            while (contadorClientes < 3) {
                Socket socket = serverSocket.accept();
                contadorClientes++;
                System.out.println("Cliente " + contadorClientes + " conectado.");

                new HiloCliente(socket, contadorClientes).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
