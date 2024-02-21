package segundotest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;



public class Juego {
    int NUMERO_GENERACIONES;
    private Celula[][] tablero;
    private int n; // Tamaño del tablero NxN
    private CyclicBarrier barrera;
    private int generacionActual = 0;
    //public volatile boolean simulacionEnProgreso = true;


    public Juego(String archivoEstadoInicial, int numeroGeneraciones) {
        this.NUMERO_GENERACIONES = numeroGeneraciones;
        cargarEstadoInicial(archivoEstadoInicial);
        // Se ajusta la barrera para incluir todas las células más el hilo principal que espera.
        this.barrera = new CyclicBarrier(n * n + 1, () -> {
            imprimirTablero();
            generacionActual++;
            System.out.println("Generación completada.\n");
            
        });
    }

    private void cargarEstadoInicial(String archivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            n = Integer.parseInt(br.readLine().trim()); // lee el tamaño del tablero
            tablero = new Celula[n][n];
            for (int i = 0; i < n; i++) {
                String[] estados = br.readLine().trim().split(" ");
                for (int j = 0; j < n; j++) {
                    boolean estadoInicial = Boolean.parseBoolean(estados[j]);
                    tablero[i][j] = new Celula(estadoInicial, this);
                }
                
            }
            imprimirTablero();
            generacionActual++;
            // pone los vecinos despues de inicializar las celulas
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    tablero[i][j].setVecinos(obtenerVecinos(i, j));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Celula> obtenerVecinos(int fila, int columna) {
        ArrayList<Celula> vecinos = new ArrayList<>();
        int[] desplazamientos = {-1, 0, 1};
        for (int desplazamientoFila : desplazamientos) {
            for (int desplazamientoColumna : desplazamientos) {
                if (desplazamientoFila == 0 && desplazamientoColumna == 0) continue;
                int vecinoFila = fila + desplazamientoFila;
                int vecinoColumna = columna + desplazamientoColumna;
                if (vecinoFila >= 0 && vecinoFila < n && vecinoColumna >= 0 && vecinoColumna < n) {
                    vecinos.add(tablero[vecinoFila][vecinoColumna]);
                }
            }
        }
        return vecinos;
    }

    public void iniciarSimulacion() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                new Thread(tablero[i][j]).start();
            }
        }
        // Este ciclo asegura que el hilo principal también espera en la barrera,
        // permitiendo que cada generación se complete antes de continuar.
        for (int i = 0; i < NUMERO_GENERACIONES; i++) {
            esperarEnBarrera();
            
        }
        
        //esperarEnBarrera();
    }

    public void esperarEnBarrera() {
        try {
            barrera.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void imprimirTablero() {
        System.out.println("Generación: " + generacionActual);
        for (Celula[] fila : tablero) {
            for (int i = 0; i < fila.length; i++) {
                System.out.print(fila[i].estaViva() ? "■ " : "□ ");
                
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: Juego <archivoEstadoInicial> <numeroGeneraciones>");
            return;
        }
        new Juego(args[0], Integer.parseInt(args[1])-1).iniciarSimulacion();
    }
}