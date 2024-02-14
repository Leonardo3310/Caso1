package segundotest;

import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class Celula extends Thread {
    private boolean estado; // true para viva, false para muerta
    private List<Buffersito> buffersVecinos;
    private final Buffersito miBuffer;
    private final CyclicBarrier barrera;
    public int numeroGeneraciones;

    public Celula(boolean estadoInicial, Buffersito miBuffer, CyclicBarrier barrera, int numeroGeneraciones) {
        this.estado = estadoInicial;
        this.miBuffer = miBuffer;
        this.barrera = barrera;
        this.numeroGeneraciones = numeroGeneraciones;
    }

    public void agregarVecinos(List<Buffersito> buffers) {
        this.buffersVecinos = buffers;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < this.numeroGeneraciones; i++) {
                // Enviar estado actual a todos los vecinos
                enviarEstado();

                // Recibir estados de los vecinos y calcular el nuevo estado
                boolean nuevoEstado = calcularNuevoEstado();
                //System.out.println(nuevoEstado);

                // Esperar a que todas las células completen este turno
                barrera.await();

                // Actualizar el estado para el próximo turno
                this.estado = nuevoEstado;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enviarEstado() {
        for (Buffersito buffer : buffersVecinos) {
            buffer.enviar(estado);
        }
    }

    public Buffersito obtenerMiBuffer() {
        return miBuffer;
    }

    public boolean estaViva() {
        return estado;
    }

    private boolean calcularNuevoEstado() {
        int vecinosVivos = 0;
        for (Buffersito buffer : buffersVecinos) {
            if (buffer.recibir()) {
                vecinosVivos++;
            }
        }

        // Aplicar las reglas del Juego de la Vida
        if ((estado == false) && vecinosVivos == 3) {
            return true; // Nace
        } else if (estado && (vecinosVivos == 0 || vecinosVivos > 3)) {
            return false; // Muere por sobrepoblación o aislamiento
        } else {
            return estado; // Permanece en el estado actual
        }
    }
}

