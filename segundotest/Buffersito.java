package segundotest;

import java.util.LinkedList;
import java.util.Queue;

public class Buffersito {
    private final Queue<Boolean> colaEstados = new LinkedList<>(); // Cola para almacenar los estados de las células
    private final int capacidadMaxima; // Capacidad máxima del buffer

    public Buffersito(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public synchronized void enviar(boolean estado) {
        while (colaEstados.size() == capacidadMaxima) {
            try {
                wait(); // Esperar si el buffer está lleno
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        colaEstados.add(estado); // Agregar el estado al buffer
        notifyAll(); // Notificar a los consumidores que hay un nuevo estado disponible
    }

    public synchronized boolean recibir() {
        while (colaEstados.isEmpty()) {
            try {
                wait(); // Esperar si el buffer está vacío
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        boolean estado = colaEstados.poll(); // Obtener y remover el estado del buffer
        notifyAll(); // Notificar a los productores que hay espacio disponible en el buffer
        return estado;
    }

    public synchronized void resetear() {
        colaEstados.clear(); // Limpiar la cola de estados
        notifyAll(); // Notificar a cualquier hilo que podría estar esperando cambios en el buffer
    }
    
}
