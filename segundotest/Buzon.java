package segundotest;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


class Buzon<T> {
    private final List<T> mensajes = new CopyOnWriteArrayList<>();

    public void enviarMensaje(T mensaje) {
        mensajes.add(mensaje);
    }

    public T recibirMensaje() {
        while (mensajes.isEmpty()) {
            // Espera semi-activa
            Thread.yield();
        }
        return mensajes.remove(0);
    }
}