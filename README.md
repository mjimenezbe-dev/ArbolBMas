# Árbol B+ en Java

Segunda práctica de implementación de estructuras de datos — Estructuras de Datos (SOFT-10), Universidad CENFOTEC, C3-2026.

Autor: Manuel Jiménez Benavides

## Descripción

El programa implementa un árbol B+ que simula, de forma simplificada, la manera en que las bases de datos y los sistemas de archivos organizan sus registros. Cada registro tiene una **clave** numérica y un **dato** de texto. El usuario trabaja con el árbol desde un menú de consola.

El árbol se mantiene balanceado en todo momento: si una inserción llena un nodo, el nodo se divide; si una eliminación deja un nodo con menos claves de las permitidas, el nodo pide prestada una clave a un hermano o se fusiona con él.

## Estructura del proyecto

```
src/
├── Main.java            Menú de consola y lectura validada de datos
├── ArbolBMas.java       Árbol B+ (atributos: raiz y orden)
└── NodoArbolBMas.java   Nodo del árbol (claves, esHoja, hijos, datos, siguiente)
```

### Clase `NodoArbolBMas`

| Atributo    | Descripción |
|-------------|-------------|
| `esHoja`    | Indica si el nodo es una hoja. |
| `claves`    | Lista ordenada de claves. |
| `hijos`     | Lista de hijos. Siempre vacía en las hojas. |
| `datos`     | Datos asociados a cada clave (misma posición). `null` en los nodos internos. |
| `siguiente` | Referencia a la hoja de la derecha. `null` en los nodos internos. |

### Clase `ArbolBMas`

Sus únicos atributos son `raiz` y `orden`. Con orden *m*, cada nodo tiene entre ⌈m/2⌉ y *m* hijos, excepto la raíz, que puede tener entre 2 y *m*.

| Método | Descripción |
|--------|-------------|
| `insertar(clave, dato)` | Inserta un registro. Divide los nodos que se llenan y enlaza la hoja nueva con sus vecinas. No permite claves repetidas. |
| `buscar(clave)` / `obtenerDato(clave)` | Baja hasta la hoja correspondiente y busca la clave. |
| `eliminar(clave)` | Elimina un registro y rebalancea el árbol. |
| `recorrerRango(claveInicial, n)` | Busca la clave inicial (o la siguiente mayor, si no existe) e imprime los siguientes *n* registros usando el enlace entre hojas. |
| `imprimirArbol()` | Muestra la estructura del árbol por niveles. |
| `imprimirHojas()` | Muestra todos los registros en orden recorriendo las hojas enlazadas. |
| `validar()` | Revisa que se cumplan todas las reglas del árbol B+ (útil para pruebas). |

## Pasos realizados

1. **Atributos del nodo.** Se agregaron `datos` y `siguiente` a `NodoArbolBMas`. El constructor crea la lista de datos solo cuando el nodo es una hoja, y los *setters* impiden asignar datos o un enlace siguiente a un nodo interno.
2. **Inserción con datos.** `insertar` recibe ahora la clave y su dato, y los guarda en la misma posición dentro de la hoja. Al dividir una hoja, los datos también se reparten y la hoja nueva se intercala en la lista enlazada (`nuevo.siguiente = lleno.siguiente; lleno.siguiente = nuevo`).
3. **Búsqueda.** Se ajustó para que siempre llegue hasta una hoja, ya que en un árbol B+ los nodos internos solo contienen separadores y no indican por sí mismos que un registro exista.
4. **Eliminación.** Se implementó de forma recursiva, siguiendo la misma idea de la inserción: primero se elimina en la hoja y, al regresar de la recursión, cada padre revisa si su hijo quedó por debajo del mínimo de claves. En ese caso:
   - si el hermano izquierdo o derecho tiene claves de sobra, se le **pide prestada** una (en hojas se mueve la clave con su dato; en nodos internos se hace una rotación a través del padre);
   - si ninguno puede prestar, el nodo se **fusiona** con un hermano y el separador se quita del padre (en hojas también se actualiza el enlace `siguiente`).
   
   Si la raíz interna se queda sin claves, su único hijo pasa a ser la nueva raíz. Por último, si la clave eliminada seguía apareciendo como separador en algún nodo interno, se reemplaza por la menor clave real de su subárbol derecho.
5. **Recorrido por rango.** Se baja hasta la hoja donde debería estar la clave inicial y, desde ahí, se avanza por las hojas enlazadas hasta imprimir *n* registros o llegar al final.
6. **Menú de consola.** `Main` pide el orden del árbol (4 por defecto) y muestra un menú con todas las operaciones. Todas las entradas se validan, de modo que un valor incorrecto no detiene el programa. Después de insertar o eliminar se muestra el árbol para observar cómo se rebalancea.
7. **Pruebas.** Se ejecutaron miles de inserciones y eliminaciones aleatorias con órdenes del 3 al 8, comparando el resultado con un `TreeMap` y verificando con `validar()` que el árbol siguiera cumpliendo todas sus reglas después de cada operación.

## Ejecución

Requiere **Java 21** o superior.

En IntelliJ IDEA: abrir la carpeta del proyecto y ejecutar `Main`.

Desde la terminal:

```bash
javac -d out src/*.java
java -cp out Main
```

### Ejemplo

Con orden 4 y los registros de ejemplo (opción 7):

```
+- Interno > [30]
   +- Interno > [10, 20]
   |  +- Hoja > [5, 6]
   |  +- Hoja > [10, 15]
   |  +- Hoja > [20, 25]
   +- Interno > [45]
      +- Hoja > [30, 40]
      +- Hoja > [45, 50]
```

Al recorrer 3 registros desde la clave 12 (que no existe), se muestran los que le siguen:

```
  [15] Cámara web
  [20] Monitor
  [25] Parlantes
```
