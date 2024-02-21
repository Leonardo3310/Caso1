package segundotest;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

public class Buffersito {
    public  BlockingQueue<Boolean> colaEstados; // Cola para almacenar los estados de las células
    private  int tamanio; // Capacidad máxima del buffer

    public Buffersito(int tamanioo) {
        this.tamanio = tamanioo;
        this.colaEstados = new ArrayBlockingQueue<>(tamanio);
    }

    public synchronized void enviar(boolean estado) {
        while (colaEstados.size() == tamanio) {
            try {
                wait(); // Esperar si el buffer está lleno
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        colaEstados.add(estado); // Agregar el estado al buffer
        notify(); // Notificar a los consumidores que hay un nuevo estado disponible
    }

    public synchronized boolean recibir() throws InterruptedException {
         
            
        boolean estadoRecibido = colaEstados.take(); // Esperar si el buffer está vacío
           
        notify(); // Notificar a los productores que hay espacio disponible en el buffer
        return estadoRecibido;
    }

    public synchronized void resetear() {
        colaEstados.clear(); // Limpiar la cola de estados
        notifyAll(); // Notificar a cualquier hilo que podría estar esperando cambios en el buffer
    }
    
}
