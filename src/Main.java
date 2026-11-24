import java.util.Scanner;

/**
 * Programa de consola para interactuar con un árbol B+.
 *
 * Cada registro tiene una clave numérica (por ejemplo, el código de un producto)
 * y un dato de texto (por ejemplo, su descripción), de forma parecida a como
 * una base de datos usa un índice para ubicar sus registros.
 */
public class Main {

    private static final int ORDEN_POR_DEFECTO = 4;
    private static final Scanner ENTRADA = new Scanner(System.in);

    public static void main(String[] args) {
        mostrarBienvenida();

        // El usuario escoge el orden del árbol al iniciar el programa
        int orden = leerOrden();
        ArbolBMas arbol = new ArbolBMas(orden);
        System.out.println("Se creó un árbol B+ de orden " + orden
                + " (cada nodo tiene como máximo " + orden + " hijos y " + (orden - 1) + " claves).");

        // Ciclo principal: se repite hasta que el usuario elija salir
        boolean continuar = true;
        while (continuar) {
            menu();
            int opcion = leerEntero("Seleccione una opción: ");
            System.out.println();
            switch (opcion) {
                case 1 -> insertarRegistro(arbol);
                case 2 -> buscarRegistro(arbol);
                case 3 -> eliminarRegistro(arbol);
                case 4 -> recorrerRango(arbol);
                case 5 -> mostrarArbol(arbol);
                case 6 -> mostrarRegistros(arbol);
                case 7 -> cargarEjemplo(arbol);
                case 0 -> continuar = false;
                default -> System.out.println("Opción no válida. Escriba un número del 0 al 7.");
            }
        }
        System.out.println("¡Hasta luego!");
        ENTRADA.close();
    }

    // ---------------------------------------------------------------------------------
    // Menú
    // ---------------------------------------------------------------------------------

    private static void mostrarBienvenida() {
        System.out.println("==============================================");
        System.out.println("        SIMULADOR DE ÁRBOL B+ EN JAVA");
        System.out.println("==============================================");
        System.out.println("Cada registro tiene una clave (número entero) y un dato (texto).");
        System.out.println("Los datos se guardan en las hojas, que están enlazadas entre sí.");
        System.out.println();
    }

    private static void menu() {
        System.out.println();
        System.out.println("----------------- MENÚ -----------------");
        System.out.println(" 1. Insertar un registro");
        System.out.println(" 2. Buscar un registro");
        System.out.println(" 3. Eliminar un registro");
        System.out.println(" 4. Recorrer un rango de registros");
        System.out.println(" 5. Mostrar la estructura del árbol");
        System.out.println(" 6. Mostrar todos los registros (hojas enlazadas)");
        System.out.println(" 7. Cargar registros de ejemplo");
        System.out.println(" 0. Salir");
        System.out.println("----------------------------------------");
    }

    // ---------------------------------------------------------------------------------
    // Opciones del menú
    // ---------------------------------------------------------------------------------

    private static void insertarRegistro(ArbolBMas arbol) {
        int clave = leerEntero("Clave del registro: ");
        String dato = leerTexto("Dato asociado: ");
        if (arbol.insertar(clave, dato)) {
            System.out.println("Registro [" + clave + "] insertado correctamente.");
            arbol.imprimirArbol();
        } else {
            System.out.println("Ya existe un registro con la clave " + clave + ": " + arbol.obtenerDato(clave));
        }
    }

    private static void buscarRegistro(ArbolBMas arbol) {
        int clave = leerEntero("Clave que desea buscar: ");
        String dato = arbol.obtenerDato(clave);
        if (dato != null) {
            System.out.println("Registro encontrado -> [" + clave + "] " + dato);
        } else {
            System.out.println("No existe ningún registro con la clave " + clave + ".");
        }
    }

    private static void eliminarRegistro(ArbolBMas arbol) {
        if (avisarSiVacio(arbol)) return;
        int clave = leerEntero("Clave del registro que desea eliminar: ");
        String dato = arbol.obtenerDato(clave);
        if (arbol.eliminar(clave)) {
            System.out.println("Registro [" + clave + "] " + dato + " eliminado correctamente.");
            arbol.imprimirArbol();
        } else {
            System.out.println("No existe ningún registro con la clave " + clave + ".");
        }
    }

    private static void recorrerRango(ArbolBMas arbol) {
        if (avisarSiVacio(arbol)) return;
        int claveInicial = leerEntero("Clave desde la que desea empezar: ");
        int n = leerEnteroPositivo("Cantidad de registros que desea ver: ");
        if (!arbol.buscar(claveInicial)) {
            System.out.println("La clave " + claveInicial + " no existe; se mostrarán los registros que le siguen.");
        }
        System.out.println("Registros a partir de la clave " + claveInicial + ":");
        int impresos = arbol.recorrerRango(claveInicial, n);
        if (impresos == 0) {
            System.out.println("  (No hay registros a partir de esa clave)");
        } else if (impresos < n) {
            System.out.println("Solo se encontraron " + impresos + " de los " + n + " registros solicitados.");
        }
    }

    private static void mostrarArbol(ArbolBMas arbol) {
        System.out.println("Estructura del árbol B+ (orden " + arbol.getOrden() + "):");
        arbol.imprimirArbol();
    }

    private static void mostrarRegistros(ArbolBMas arbol) {
        System.out.println("Registros en orden (recorriendo las hojas enlazadas):");
        arbol.imprimirHojas();
    }

    // Inserta un conjunto de productos de ejemplo para probar el programa rápidamente
    private static void cargarEjemplo(ArbolBMas arbol) {
        int[] claves = {10, 20, 5, 6, 15, 30, 25, 40, 45, 50};
        String[] datos = {"Teclado", "Monitor", "Mouse", "Audífonos", "Cámara web",
                "Impresora", "Parlantes", "Disco duro", "Memoria USB", "Router"};
        int insertados = 0;
        for (int i = 0; i < claves.length; i++) {
            if (arbol.insertar(claves[i], datos[i])) {
                insertados++;
            }
        }
        System.out.println("Se cargaron " + insertados + " registros de ejemplo"
                + (insertados < claves.length ? " (los demás ya existían)." : "."));
        arbol.imprimirArbol();
    }

    // ---------------------------------------------------------------------------------
    // Lectura de datos con validación
    // ---------------------------------------------------------------------------------

    private static boolean avisarSiVacio(ArbolBMas arbol) {
        if (arbol.estaVacio()) {
            System.out.println("El árbol está vacío. Inserte registros o cargue los de ejemplo (opción 7).");
            return true;
        }
        return false;
    }

    private static int leerOrden() {
        while (true) {
            String texto = leerLinea("Orden del árbol (mínimo 3, Enter para usar " + ORDEN_POR_DEFECTO + "): ");
            if (texto.isEmpty()) {
                return ORDEN_POR_DEFECTO;
            }
            try {
                int orden = Integer.parseInt(texto);
                if (orden >= 3) {
                    return orden;
                }
                System.out.println("El orden debe ser 3 o mayor.");
            } catch (NumberFormatException e) {
                System.out.println("Debe escribir un número entero.");
            }
        }
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leerLinea(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Debe escribir un número entero.");
            }
        }
    }

    private static int leerEnteroPositivo(String mensaje) {
        while (true) {
            int valor = leerEntero(mensaje);
            if (valor > 0) {
                return valor;
            }
            System.out.println("El número debe ser mayor que 0.");
        }
    }

    private static String leerTexto(String mensaje) {
        while (true) {
            String texto = leerLinea(mensaje);
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("El dato no puede quedar vacío.");
        }
    }

    // Lee una línea completa; si la entrada se termina, el programa cierra de forma ordenada
    private static String leerLinea(String mensaje) {
        System.out.print(mensaje);
        if (!ENTRADA.hasNextLine()) {
            System.out.println();
            System.out.println("Fin de la entrada. ¡Hasta luego!");
            System.exit(0);
        }
        return ENTRADA.nextLine().trim();
    }
}
