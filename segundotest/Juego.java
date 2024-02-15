package segundotest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

public class Juego {
    public int NUMERO_GENERACIONES = 5; // Definir según necesidad
    private Celula[][] tablero;
    private int n; // Tamaño del tablero NxN
    private CyclicBarrier barrera;

    public Juego(String archivoEstadoInicial, int numeroGeneraciones) {
        this.NUMERO_GENERACIONES = numeroGeneraciones;
        //this.barrera = new CyclicBarrier(n * n + 1);
        cargarEstadoInicial(archivoEstadoInicial);
        
    }

    private void cargarEstadoInicial(String archivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String line = br.readLine().trim();
            n = Integer.parseInt(line); // Leer el tamaño del tablero
            tablero = new Celula[n][n];
            this.barrera = new CyclicBarrier(n * n +1);
    
            // Paso 1: Inicializar todas las células sin sus vecinos
            for (int i = 0; i < n; i++) {
                line = br.readLine().trim();
                String[] estados = line.split(" ");
                for (int j = 0; j < n; j++) {
                    boolean estadoInicial = Boolean.parseBoolean(estados[j]);
                    //System.out.println(estadoInicial);
                    Buffersito miBuffer = new Buffersito(i + 1);
                    tablero[i][j] = new Celula(estadoInicial, miBuffer, barrera, this.NUMERO_GENERACIONES);
                }
            }
    
            // Paso 2: Asignar vecinos ahora que todas las células están inicializadas
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    ArrayList<Buffersito> buffersVecinos = obtenerBuffersVecinos(i, j);
                    //System.out.println(tablero[i][j].estaViva());
                    tablero[i][j].agregarVecinos(buffersVecinos); // Asume que Celula tiene un método para establecer sus vecinos
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private ArrayList<Buffersito> obtenerBuffersVecinos(int fila, int columna) {
        ArrayList<Buffersito> buffers = new ArrayList<>();
    
        // Definir los posibles desplazamientos respecto a la posición actual
        int[] desplazamientos = {-1, 0, 1};
    
        for (int desplazamientoFila : desplazamientos) {
            for (int desplazamientoColumna : desplazamientos) {
                // Ignorar la posición actual (0,0)
                if (desplazamientoFila == 0 && desplazamientoColumna == 0) {
                    continue;
                }
    
                int vecinoFila = fila + desplazamientoFila;
                int vecinoColumna = columna + desplazamientoColumna;
    
                // Verificar si la posición del vecino está dentro de los límites del tablero
                if (vecinoFila >= 0 && vecinoFila < tablero.length && vecinoColumna >= 0 && vecinoColumna < tablero[vecinoFila].length) {
                    // Añadir el buffer de la célula vecina a la lista
                    buffers.add(tablero[vecinoFila][vecinoColumna].obtenerMiBuffer());
                }
            }
        }
    
        return buffers;
    }
    

    public void iniciarSimulacion() {
        for (int gen = 0; gen < this.NUMERO_GENERACIONES; gen++) {
            // Iniciar o reanudar todos los threads de las células
            if (gen == 0) { // Solo iniciar los threads en la primera generación
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        //System.out.println(tablero[i][j].estaViva());
                        tablero[i][j].start();
                    }
                }
                
            }
    
            // Esperar a que todas las células alcancen la barrera
            awaitBarrier(barrera);
    
            // Mostrar el tablero después de que todas las células han alcanzado la barrera
            mostrarTablero();
            

            //resetearBuffers();
            
            try {
                Thread.sleep(750); // Pausa de 1 segundo entre generaciones
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private void awaitBarrier(CyclicBarrier barrera) {
        try {
            barrera.await(); // Esperar a que todas las células alcancen la barrera
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void resetearBuffers() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tablero[i][j].obtenerMiBuffer().resetear();
            }
        }
    }

    public void mostrarTablero() {
        for (int i = 0; i < tablero.length; i++) {
            for (int j = 0; j < tablero[i].length; j++) {
                //System.out.print(board[i][j].estaVivo());
                System.out.print(tablero[i][j].estaViva() ? "■ " : "□ ");
            }
            System.out.println(); // Nueva línea al final de cada fila
        }
        System.out.println(); // Línea adicional para separar las generaciones
    }

    public static void main(String[] args) {
        Juego juego = new Juego(args[0], Integer.parseInt(args[1]));
        juego.iniciarSimulacion();
        // Llamar a mostrarTablero() después de que todas las células hayan terminado
    }
}   

