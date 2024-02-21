package segundotest;

import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class CelulaProductora extends Thread {
    private boolean estado; // true para viva, false para muerta
    private int posicionx;
    private int posiciony;
    public int numeroGeneraciones;
    private CyclicBarrier barrera;

    public CelulaProductora(boolean estadoInicial, int x, int y, int numeroGeneraciones, CyclicBarrier barrera) {
        this.estado = estadoInicial;
        this.posicionx = x;
        this.posiciony = y;
        this.numeroGeneraciones = numeroGeneraciones;
        this.barrera = barrera;
    }

    

    @Override
    public void run() {
        
        try {
            int[][] diferenciasVecinos = {
                {-1, 0}, // Arriba
                {1, 0},  // Abajo
                {0, 1},  // Derecha
                {0, -1}, // Izquierda
                {-1, 1}, // Arriba derecha
                {-1, -1},// Arriba izquierda
                {1, 1},  // Abajo derecha
                {1, -1}  // Abajo izquierda
            };

            for (int i = 0; i < this.numeroGeneraciones; i++) {
                
                Juego.barrera.await();

                for (int[] diferencia : diferenciasVecinos) {
                    int di = diferencia[0];
                    int dj = diferencia[1];
                    
                    
                    try {
                        enviarEstado(Juego.buzones[posicionx + di][posiciony + dj]);
                    } catch (Exception e) {
                        // Manejar la excepción específica aquí
                    }
                }

                // Esperar a que todas las células completen este turno
                barrera.await();

                // Actualizar el estado para el próximo turno
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enviarEstado(Buffersito vecino) {
        vecino.enviar(estado);
    }

    public void actualizar (){
        boolean estadoMatriz = Juego.matriz[posicionx][posiciony]; 
        this.estado = estadoMatriz;
    }
}

