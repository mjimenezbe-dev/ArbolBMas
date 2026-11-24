import java.util.ArrayList;
import java.util.List;

/**
 * Árbol B+ de orden m.
 *
 * Reglas que se mantienen después de cada operación:
 *  - Cada nodo tiene como máximo m hijos (m - 1 claves).
 *  - Cada nodo, excepto la raíz, tiene como mínimo ceil(m/2) hijos (ceil(m/2) - 1 claves).
 *  - La raíz, si es interna, tiene entre 2 y m hijos.
 *  - Todos los datos se guardan en las hojas, y las hojas están enlazadas de izquierda a derecha.
 *  - En un nodo interno, la clave i separa al hijo i (claves menores) del hijo i + 1 (claves mayores o iguales).
 */
public class ArbolBMas {

    // Atributos
    private final int orden;
    private NodoArbolBMas raiz;

    // Métodos
    // Constructor
    public ArbolBMas(int orden) {
        if (orden < 3) {
            throw new IllegalArgumentException("El orden del árbol debe ser al menos 3.");
        }
        this.orden = orden;
        this.raiz = new NodoArbolBMas(true);
    }

    public int getOrden() {
        return orden;
    }

    public boolean estaVacio() {
        return raiz.esHoja() && raiz.cantidadClaves() == 0;
    }

    // Cantidad mínima de claves que debe tener un nodo que no sea la raíz: ceil(m/2) - 1
    private int minimoClaves() {
        return (orden + 1) / 2 - 1;
    }

    // Índice del hijo por el que se debe bajar para encontrar una clave
    // (cantidad de claves del nodo que son menores o iguales a la clave buscada)
    private int indiceHijo(NodoArbolBMas nodo, int clave) {
        int i = 0;
        while (i < nodo.cantidadClaves() && clave >= nodo.getClaves().get(i)) {
            i++;
        }
        return i;
    }

    // Hoja en la que está (o debería estar) una clave
    private NodoArbolBMas buscarHoja(int clave) {
        NodoArbolBMas actual = raiz;
        while (!actual.esHoja()) {
            actual = actual.getHijos().get(indiceHijo(actual, clave));
        }
        return actual;
    }

    // Menor clave de un subárbol: se baja siempre por el hijo de más a la izquierda
    private int minimo(NodoArbolBMas nodo) {
        while (!nodo.esHoja()) {
            nodo = nodo.getHijos().getFirst();
        }
        return nodo.getClaves().getFirst();
    }

    // ---------------------------------------------------------------------------------
    // Inserción
    // ---------------------------------------------------------------------------------

    // Inserción de una clave con su dato. Devuelve false si la clave ya existía.
    public boolean insertar(int clave, String dato) {
        if (buscar(clave)) {
            return false;
        }

        // Se inserta primero de forma recursiva
        insertarRecursivo(this.raiz, clave, dato);

        // Se verifica si la raíz se desbordó; si es así, el árbol crece un nivel
        if (this.raiz.cantidadClaves() == orden) {
            NodoArbolBMas nuevaRaiz = new NodoArbolBMas(false);
            nuevaRaiz.getHijos().add(this.raiz);
            dividirHijo(nuevaRaiz, 0);
            this.raiz = nuevaRaiz;
        }
        return true;
    }

    // Inserción recursiva de una clave
    // Permite que los nodos se desborden temporalmente y los divide al regresar de la recursión
    private void insertarRecursivo(NodoArbolBMas nodo, int clave, String dato) {
        int i = indiceHijo(nodo, clave);

        if (nodo.esHoja()) {
            // En la hoja, la clave y su dato se guardan en la misma posición
            nodo.getClaves().add(i, clave);
            nodo.getDatos().add(i, dato);
        } else {
            NodoArbolBMas hijo = nodo.getHijos().get(i);

            // Llamada recursiva para insertar la clave primero en el hijo correspondiente
            insertarRecursivo(hijo, clave, dato);

            // Evaluación de desbordamiento al retornar de la recursividad
            if (hijo.cantidadClaves() == orden) {
                dividirHijo(nodo, i);
            }
        }
    }

    // Dividir un nodo que está lleno
    private void dividirHijo(NodoArbolBMas padre, int indice) {
        NodoArbolBMas nodoLleno = padre.getHijos().get(indice);
        NodoArbolBMas nuevoNodo = new NodoArbolBMas(nodoLleno.esHoja());
        int mitad = orden / 2;
        int total = nodoLleno.cantidadClaves();

        if (nodoLleno.esHoja()) {
            // En nodos hoja, la mitad derecha de claves y datos pasa al nuevo nodo
            nuevoNodo.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(mitad, total)));
            nuevoNodo.setDatos(new ArrayList<>(nodoLleno.getDatos().subList(mitad, total)));
            nodoLleno.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(0, mitad)));
            nodoLleno.setDatos(new ArrayList<>(nodoLleno.getDatos().subList(0, mitad)));

            // El nuevo nodo se intercala en la lista enlazada de hojas
            nuevoNodo.setSiguiente(nodoLleno.getSiguiente());
            nodoLleno.setSiguiente(nuevoNodo);

            // El padre recibe como separador una copia de la primera clave del nuevo nodo
            padre.getClaves().add(indice, nuevoNodo.getClaves().getFirst());
        } else {
            // En nodos internos, la clave media sube al padre y se elimina del hijo
            int claveMedia = nodoLleno.getClaves().get(mitad);
            nuevoNodo.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(mitad + 1, total)));
            nodoLleno.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(0, mitad)));

            // Mover los hijos del nodo dividido
            int totalHijos = nodoLleno.getHijos().size();
            nuevoNodo.setHijos(new ArrayList<>(nodoLleno.getHijos().subList(mitad + 1, totalHijos)));
            nodoLleno.setHijos(new ArrayList<>(nodoLleno.getHijos().subList(0, mitad + 1)));

            padre.getClaves().add(indice, claveMedia);
        }
        padre.getHijos().add(indice + 1, nuevoNodo);
    }

    // ---------------------------------------------------------------------------------
    // Impresión
    // ---------------------------------------------------------------------------------

    // Impresión del árbol como parte de la interfaz pública del árbol
    public void imprimirArbol() {
        if (estaVacio()) {
            System.out.println("(El árbol está vacío)");
            return;
        }
        imprimirNodo(raiz, "", true);
    }

    // Impresión recursiva privada de los nodos a partir de uno inicial
    private void imprimirNodo(NodoArbolBMas nodo, String indentacion, boolean esUltimo) {
        System.out.println(indentacion + "+- " + (nodo.esHoja() ? "Hoja > " : "Interno > ") + nodo.getClaves());
        indentacion += esUltimo ? "   " : "|  ";
        for (int i = 0; i < nodo.getHijos().size(); i++) {
            imprimirNodo(nodo.getHijos().get(i), indentacion, i == (nodo.getHijos().size() - 1));
        }
    }

    // Impresión de todos los registros recorriendo la lista enlazada de hojas
    public void imprimirHojas() {
        if (estaVacio()) {
            System.out.println("(El árbol está vacío)");
            return;
        }
        NodoArbolBMas hoja = raiz;
        while (!hoja.esHoja()) {
            hoja = hoja.getHijos().getFirst();
        }
        StringBuilder enlaces = new StringBuilder();
        while (hoja != null) {
            for (int i = 0; i < hoja.cantidadClaves(); i++) {
                imprimirRegistro(hoja.getClaves().get(i), hoja.getDatos().get(i));
            }
            enlaces.append(hoja.getClaves()).append(hoja.getSiguiente() != null ? " -> " : "");
            hoja = hoja.getSiguiente();
        }
        System.out.println("Hojas enlazadas: " + enlaces);
    }

    private void imprimirRegistro(int clave, String dato) {
        System.out.printf("  [%d] %s%n", clave, dato);
    }

    // ---------------------------------------------------------------------------------
    // Búsqueda
    // ---------------------------------------------------------------------------------

    // Búsqueda de una clave como parte de la interfaz pública del árbol
    // Siempre se baja hasta la hoja, porque los nodos internos solo contienen separadores
    public boolean buscar(int clave) {
        return buscarHoja(clave).indiceDe(clave) != -1;
    }

    // Devuelve el dato asociado a una clave, o null si la clave no existe
    public String obtenerDato(int clave) {
        NodoArbolBMas hoja = buscarHoja(clave);
        int i = hoja.indiceDe(clave);
        return i == -1 ? null : hoja.getDatos().get(i);
    }

    // ---------------------------------------------------------------------------------
    // Eliminación
    // ---------------------------------------------------------------------------------

    // Eliminación de una clave (y su dato). Devuelve false si la clave no existía.
    public boolean eliminar(int clave) {
        // Se elimina primero de forma recursiva; los nodos se rebalancean al regresar
        boolean eliminado = eliminarRecursivo(this.raiz, clave);
        if (!eliminado) {
            return false;
        }

        // Si la raíz interna se quedó sin claves, su único hijo pasa a ser la raíz
        // y el árbol pierde un nivel
        if (!raiz.esHoja() && raiz.cantidadClaves() == 0) {
            raiz = raiz.getHijos().getFirst();
        }

        // La clave eliminada pudo quedar como separador en algún nodo interno:
        // se reemplaza por la menor clave real de su subárbol derecho
        actualizarSeparador(clave);
        return true;
    }

    // Eliminación recursiva de una clave
    // Permite que los nodos queden por debajo del mínimo temporalmente y los corrige al regresar
    private boolean eliminarRecursivo(NodoArbolBMas nodo, int clave) {
        if (nodo.esHoja()) {
            int i = nodo.indiceDe(clave);
            if (i == -1) {
                return false;
            }
            nodo.getClaves().remove(i);
            nodo.getDatos().remove(i);
            return true;
        }

        int i = indiceHijo(nodo, clave);
        NodoArbolBMas hijo = nodo.getHijos().get(i);

        // Llamada recursiva para eliminar la clave primero en el hijo correspondiente
        boolean eliminado = eliminarRecursivo(hijo, clave);

        // Evaluación de subdesbordamiento al retornar de la recursividad
        if (eliminado && hijo.cantidadClaves() < minimoClaves()) {
            rebalancearHijo(nodo, i);
        }
        return eliminado;
    }

    // Corrige un hijo que quedó con menos claves que el mínimo.
    // Primero intenta pedir prestada una clave a un hermano; si ninguno puede prestar, fusiona.
    private void rebalancearHijo(NodoArbolBMas padre, int indice) {
        List<NodoArbolBMas> hijos = padre.getHijos();
        NodoArbolBMas izquierdo = indice > 0 ? hijos.get(indice - 1) : null;
        NodoArbolBMas derecho = indice < hijos.size() - 1 ? hijos.get(indice + 1) : null;

        if (izquierdo != null && izquierdo.cantidadClaves() > minimoClaves()) {
            prestarDeIzquierdo(padre, indice);
        } else if (derecho != null && derecho.cantidadClaves() > minimoClaves()) {
            prestarDeDerecho(padre, indice);
        } else if (izquierdo != null) {
            fusionar(padre, indice - 1);   // El hijo se une a su hermano izquierdo
        } else {
            fusionar(padre, indice);       // El hermano derecho se une al hijo
        }
    }

    // Pasa la última clave del hermano izquierdo al inicio del hijo
    private void prestarDeIzquierdo(NodoArbolBMas padre, int indice) {
        NodoArbolBMas hijo = padre.getHijos().get(indice);
        NodoArbolBMas izquierdo = padre.getHijos().get(indice - 1);

        if (hijo.esHoja()) {
            // En hojas se mueven la clave y su dato, y el separador pasa a ser la nueva primera clave
            hijo.getClaves().addFirst(izquierdo.getClaves().removeLast());
            hijo.getDatos().addFirst(izquierdo.getDatos().removeLast());
            padre.getClaves().set(indice - 1, hijo.getClaves().getFirst());
        } else {
            // En nodos internos se rota: el separador baja al hijo y la última clave del hermano sube
            hijo.getClaves().addFirst(padre.getClaves().get(indice - 1));
            padre.getClaves().set(indice - 1, izquierdo.getClaves().removeLast());
            hijo.getHijos().addFirst(izquierdo.getHijos().removeLast());
        }
    }

    // Pasa la primera clave del hermano derecho al final del hijo
    private void prestarDeDerecho(NodoArbolBMas padre, int indice) {
        NodoArbolBMas hijo = padre.getHijos().get(indice);
        NodoArbolBMas derecho = padre.getHijos().get(indice + 1);

        if (hijo.esHoja()) {
            hijo.getClaves().addLast(derecho.getClaves().removeFirst());
            hijo.getDatos().addLast(derecho.getDatos().removeFirst());
            padre.getClaves().set(indice, derecho.getClaves().getFirst());
        } else {
            hijo.getClaves().addLast(padre.getClaves().get(indice));
            padre.getClaves().set(indice, derecho.getClaves().removeFirst());
            hijo.getHijos().addLast(derecho.getHijos().removeFirst());
        }
    }

    // Une el hijo en la posición indice + 1 dentro del hijo en la posición indice
    private void fusionar(NodoArbolBMas padre, int indice) {
        NodoArbolBMas izquierdo = padre.getHijos().get(indice);
        NodoArbolBMas derecho = padre.getHijos().get(indice + 1);
        int separador = padre.getClaves().remove(indice);
        padre.getHijos().remove(indice + 1);

        if (izquierdo.esHoja()) {
            // En hojas el separador es solo una copia, por lo que se descarta
            izquierdo.getClaves().addAll(derecho.getClaves());
            izquierdo.getDatos().addAll(derecho.getDatos());
            // La hoja eliminada se saca de la lista enlazada
            izquierdo.setSiguiente(derecho.getSiguiente());
        } else {
            // En nodos internos el separador baja y queda entre las claves de ambos nodos
            izquierdo.getClaves().add(separador);
            izquierdo.getClaves().addAll(derecho.getClaves());
            izquierdo.getHijos().addAll(derecho.getHijos());
        }
    }

    // Reemplaza la clave eliminada en los nodos internos donde todavía aparezca como separador.
    // Solo puede aparecer en el camino de búsqueda de esa clave, así que basta con recorrerlo.
    private void actualizarSeparador(int clave) {
        NodoArbolBMas actual = raiz;
        while (!actual.esHoja()) {
            int j = actual.indiceDe(clave);
            if (j != -1) {
                actual.getClaves().set(j, minimo(actual.getHijos().get(j + 1)));
            }
            actual = actual.getHijos().get(indiceHijo(actual, clave));
        }
    }

    // ---------------------------------------------------------------------------------
    // Recorrido por rango
    // ---------------------------------------------------------------------------------

    // Busca la clave inicial (o la primera mayor que ella, si no existe) e imprime
    // los siguientes n registros recorriendo las hojas enlazadas.
    // Devuelve la cantidad de registros que se imprimieron.
    public int recorrerRango(int claveInicial, int n) {
        if (n <= 0) {
            return 0;
        }
        NodoArbolBMas hoja = buscarHoja(claveInicial);

        // Primera posición de la hoja con una clave mayor o igual a la inicial
        int i = 0;
        while (i < hoja.cantidadClaves() && hoja.getClaves().get(i) < claveInicial) {
            i++;
        }

        int impresos = 0;
        while (hoja != null && impresos < n) {
            if (i < hoja.cantidadClaves()) {
                imprimirRegistro(hoja.getClaves().get(i), hoja.getDatos().get(i));
                impresos++;
                i++;
            } else {
                // Se terminó la hoja actual: se continúa con la siguiente
                hoja = hoja.getSiguiente();
                i = 0;
            }
        }
        return impresos;
    }

    // ---------------------------------------------------------------------------------
    // Verificación
    // ---------------------------------------------------------------------------------

    // Revisa que se cumplan todas las reglas del árbol B+ (útil para pruebas).
    // Devuelve null si el árbol es válido, o una descripción del primer problema encontrado.
    public String validar() {
        List<Integer> profundidades = new ArrayList<>();
        String error = validarNodo(raiz, null, null, 0, profundidades, true);
        if (error != null) {
            return error;
        }
        if (profundidades.stream().distinct().count() > 1) {
            return "Las hojas no están todas al mismo nivel.";
        }

        // Las hojas enlazadas deben recorrer todas las claves en orden
        NodoArbolBMas hoja = raiz;
        while (!hoja.esHoja()) {
            hoja = hoja.getHijos().getFirst();
        }
        Integer anterior = null;
        while (hoja != null) {
            for (int clave : hoja.getClaves()) {
                if (anterior != null && clave <= anterior) {
                    return "Las hojas enlazadas no están en orden.";
                }
                anterior = clave;
            }
            hoja = hoja.getSiguiente();
        }
        return null;
    }

    private String validarNodo(NodoArbolBMas nodo, Integer minimo, Integer maximo, int nivel,
                               List<Integer> profundidades, boolean esRaiz) {
        List<Integer> claves = nodo.getClaves();
        if (claves.size() > orden - 1) {
            return "Nodo con demasiadas claves: " + claves;
        }
        if (!esRaiz && claves.size() < minimoClaves()) {
            return "Nodo con menos claves que el mínimo: " + claves;
        }
        for (int k = 0; k < claves.size(); k++) {
            int c = claves.get(k);
            if (k > 0 && c <= claves.get(k - 1)) return "Claves desordenadas: " + claves;
            if (minimo != null && c < minimo) return "Clave fuera de rango: " + claves;
            if (maximo != null && c >= maximo) return "Clave fuera de rango: " + claves;
        }
        if (nodo.esHoja()) {
            if (nodo.getDatos() == null || nodo.getDatos().size() != claves.size()) {
                return "Hoja con datos inconsistentes: " + claves;
            }
            profundidades.add(nivel);
            return null;
        }
        if (nodo.getDatos() != null || nodo.getSiguiente() != null) {
            return "Nodo interno con datos o enlace siguiente: " + claves;
        }
        if (nodo.getHijos().size() != claves.size() + 1) {
            return "Cantidad de hijos incorrecta en " + claves;
        }
        if (esRaiz && nodo.getHijos().size() < 2) {
            return "Raíz interna con menos de 2 hijos.";
        }
        for (int k = 0; k < nodo.getHijos().size(); k++) {
            Integer min = k == 0 ? minimo : claves.get(k - 1);
            Integer max = k == claves.size() ? maximo : claves.get(k);
            NodoArbolBMas hijo = nodo.getHijos().get(k);
            String error = validarNodo(hijo, min, max, nivel + 1, profundidades, false);
            if (error != null) return error;
            // Cada separador debe ser exactamente la menor clave de su subárbol derecho
            if (k > 0 && minimo(hijo) != claves.get(k - 1)) {
                return "Separador desactualizado: " + claves.get(k - 1);
            }
        }
        return null;
    }
}
