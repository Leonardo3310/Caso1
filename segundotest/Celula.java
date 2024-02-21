package segundotest;

import java.util.ArrayList;
import java.util.List;

public class Celula implements Runnable {
    private volatile boolean estado;
    private boolean proximoEstado; 
    //private final int fila, columna;
    private final Juego juego;
    private final Buzon<Boolean> buzon;
    private final List<Celula> vecinos = new ArrayList<>();

    public Celula(boolean estadoInicial, Juego juego) {
        this.estado = estadoInicial;
        //this.fila = fila;
        //this.columna = columna;
        this.juego = juego;
        // Ajuste en la capacidad del buzón según la fila
        this.buzon = new Buzon<>();
    }

    public void setVecinos(List<Celula> vecinos) {
        this.vecinos.addAll(vecinos);
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < juego.NUMERO_GENERACIONES; i++) {
                // envia el estado actual a todos los vecinos
                for (Celula vecino : vecinos) {
                    vecino.buzon.enviarMensaje(estado);
                }
                // recibe el estado de todos los vecinos
                List<Boolean> estadosVecinos = new ArrayList<>();
                for (int j = 0; j < vecinos.size(); j++) {
                    estadosVecinos.add(buzon.recibirMensaje());
                }
                // calcula el próximo estado basado en los estados recibidos
                proximoEstado = calcularProximoEstado(estadosVecinos);
    
                //juego.esperarEnBarrera(); // Sincroniza después de recibir y antes de actualizar
                //if (!juego.simulacionEnProgreso) break;
                // Actualiza el estado
                estado = proximoEstado;
    
                juego.esperarEnBarrera(); // sincroniza todo después de actualizar pa ver q las celulas esten listas para la siguiente generacion
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean calcularProximoEstado(List<Boolean> estadosVecinos) {
        int vecinosVivos = 0;
        for (Boolean estadoVecino : estadosVecinos) {
            if (estadoVecino) vecinosVivos++;
        }
    
        // Aplica las reglas para determinar el próximo estado
        if ((estado == false) && vecinosVivos == 3) {
            return true; // Nace
        } else if (estado && (vecinosVivos == 0 || vecinosVivos > 3)) {
            return false; // Muere por sobrepoblación o aislamiento
        } else {
            return estado; // Permanece en el estado actual
        }
    }

    public boolean estaViva() {
        return estado;
    }
}