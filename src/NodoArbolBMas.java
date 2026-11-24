import java.util.ArrayList;
import java.util.List;

/**
 * Nodo de un árbol B+.
 *
 * Existen dos tipos de nodo:
 *  - Nodo interno: solo guarda claves que funcionan como separadores y la lista de hijos.
 *    Sus atributos datos y siguiente son siempre null.
 *  - Nodo hoja: guarda las claves junto con sus datos asociados (en la misma posición)
 *    y una referencia a la hoja siguiente, de modo que todas las hojas forman una lista enlazada.
 *    Su lista de hijos está siempre vacía.
 */
public class NodoArbolBMas {

    // Atributos
    private final boolean esHoja;
    private List<Integer> claves;
    private List<NodoArbolBMas> hijos;   // Vacía si es una hoja
    private List<String> datos;          // datos.get(i) corresponde a claves.get(i). Null si es un nodo interno
    private NodoArbolBMas siguiente;     // Hoja que sigue a la derecha. Null si es un nodo interno

    // Métodos
    // Constructor: solo las hojas reservan espacio para los datos
    public NodoArbolBMas(boolean esHoja) {
        this.esHoja = esHoja;
        this.claves = new ArrayList<>();
        this.hijos = new ArrayList<>();
        this.datos = esHoja ? new ArrayList<>() : null;
        this.siguiente = null;
    }

    // Getters
    public boolean esHoja() {
        return esHoja;
    }

    public List<Integer> getClaves() {
        return claves;
    }

    public List<NodoArbolBMas> getHijos() {
        return hijos;
    }

    public List<String> getDatos() {
        return datos;
    }

    public NodoArbolBMas getSiguiente() {
        return siguiente;
    }

    // Setters
    public void setClaves(List<Integer> claves) {
        this.claves = claves;
    }

    public void setHijos(List<NodoArbolBMas> hijos) {
        this.hijos = hijos;
    }

    // Los datos y el enlace a la siguiente hoja solo tienen sentido en las hojas
    public void setDatos(List<String> datos) {
        if (!esHoja) {
            throw new IllegalStateException("Un nodo interno no almacena datos.");
        }
        this.datos = datos;
    }

    public void setSiguiente(NodoArbolBMas siguiente) {
        if (!esHoja) {
            throw new IllegalStateException("Un nodo interno no tiene hoja siguiente.");
        }
        this.siguiente = siguiente;
    }

    // Utilidades
    // Posición de una clave dentro de este nodo, o -1 si no está
    public int indiceDe(int clave) {
        return claves.indexOf(clave);
    }

    // Cantidad de claves almacenadas en el nodo
    public int cantidadClaves() {
        return claves.size();
    }
}
