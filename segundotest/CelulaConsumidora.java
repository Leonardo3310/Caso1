package segundotest;

import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class CelulaConsumidora extends Thread {
    private boolean estado; // true para viva, false para muerta
    //private List<Buffersito> buffersVecinos;
    private  Buffersito miBuffer;
    private final CyclicBarrier barrera1;
    private final CyclicBarrier barrera2;
    public int numeroGeneraciones;
    public int posicionx;
    public int posiciony;

    public int vecinos;

    public int vecinoVivitosYColeando;;
   

    public CelulaConsumidora(Buffersito miBuffer, CyclicBarrier barrera1, CyclicBarrier barrera2,
     int numeroGeneraciones, int x, int y) {
        //this.estado = estadoInicial;
        this.miBuffer = miBuffer;
        this.barrera1 = barrera1;
        this.barrera2 = barrera2;
        this.numeroGeneraciones = numeroGeneraciones;
        this.posicionx = x;
        this.posiciony = y;
    }

    private boolean recibirEstado(Buffersito vecino){
        try {
            boolean estadoVecino = vecino.recibir();
            if(estadoVecino == true){
                vecinoVivitosYColeando++;
            }
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < this.numeroGeneraciones; i++) {
                // Enviar estado actual a todos los vecinos
                Juego.barrera.await();

                vecinoVivitosYColeando = 0;
                int x = 0;
                boolean condicion = true;
                // Recibir estados de los vecinos y calcular el nuevo estado
                boolean nuevoEstado = calcularNuevoEstado();
                //System.out.println(nuevoEstado);

                // Esperar a que todas las células completen este turno
                

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

    private boolean calcularNuevoEstado() throws InterruptedException {
        int vecinosVivos = 0;
        for (Buffersito buffer : buffersVecinos) {
            //System.out.println(buffer.recibir());
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

