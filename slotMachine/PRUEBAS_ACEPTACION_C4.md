# Dos pruebas de aceptación — BlueJ

Son recorridos preparados para ejecutarlos tú. Anota el resultado real y la fecha después de realizarlos.

## Antes de comenzar

1. Abre BlueJ y selecciona **Proyecto > Abrir**. Abre la carpeta `slotMachine`, donde está `package.bluej`.
2. Pulsa **Compilar**.
3. Haz clic derecho en la clase `SlotMachine` y elige **new SlotMachine()**, sin parámetros. Pon al objeto el nombre `machine1`.
4. Las llamadas siguientes se hacen con clic derecho sobre el objeto rojo `machine1`, no sobre el rectángulo de la clase.
5. Cuando existan métodos con el mismo nombre, elige la versión cuyos parámetros coincidan con la tabla. Por ejemplo, para agregar un tipo elige `addWheel(String type, int pos)`.
6. En cada cuadro de parámetros escribe solo el valor correspondiente: `"lefty"` en type y `2` en pos. Los textos llevan comillas; los números, no.
7. Las llamadas de las tablas muestran la operación completa para orientarte. No pegues toda la llamada dentro de un único parámetro.

Para ver un arreglo devuelto por `configuration()`, pulsa **Inspeccionar** en el resultado y lee cada posición. La posición `[0]` del arreglo representa la rueda 1.

## Prueba 1: los cuatro tipos de ruedas

**Objetivo:** demostrar ruedas vacías, copia izquierda, restricciones rebel y giro reverse.

En `machine1`, ejecuta en este orden:

| Paso | Llamada | Qué debe ocurrir |
|---:|---|---|
| 1 | `addSymbol(1, "red")` | Agrega rojo al catálogo |
| 2 | `addSymbol(2, "blue")` | Agrega azul |
| 3 | `addSymbol(3, "green")` | Agrega verde |
| 4 | `addWheel("normal", 1)` | Rueda vacía, marco negro |
| 5 | `addWheel("lefty", 2)` | Rueda vacía, marco azul |
| 6 | `addWheel("rebel", 3)` | Rueda vacía, marco rojo |
| 7 | `addWheel("reverse", 4)` | Rueda vacía, marco magenta |
| 8 | `configuration()` | `[null, null, null, null]` |
| 9 | `placeSymbol(1, "red")` | Asigna rojo a la primera |
| 10 | `placeSymbol(2, "blue")` | Asigna azul a lefty |
| 11 | `placeSymbol(3, "red")` | Asigna rojo a rebel |
| 12 | `placeSymbol(4, "red")` | Asigna rojo a reverse |
| 13 | `spin(2)` | Lefty copia rojo de la primera |
| 14 | `configuration()` | `[red, red, red, red]` |
| 15 | `isJackpot()` | `true`; marcos amarillos, etiquetas conservadas |
| 16 | `spin(4)` | Reverse retrocede de rojo a verde |
| 17 | `configuration()` | `[red, red, red, green]` |
| 18 | `lock(3)` | Mensaje: rebel no permite bloqueo; ciérralo |
| 19 | `ok()` | `false` |
| 20 | `isLocked(3)` | `false` |
| 21 | `delWheel(3)` | Mensaje de rechazo; ciérralo |
| 22 | `ok()` | `false`; permanecen cuatro ruedas |
| 23 | `swap(1, 3)` | Mensaje de rechazo; ciérralo |
| 24 | `ok()` | `false`; no se intercambiaron |
| 25 | `spin(3)` | Rebel sí gira: pasa de rojo a azul |
| 26 | `configuration()` | `[red, red, blue, green]` |
| 27 | `spin()` | Giran en orden de izquierda a derecha |
| 28 | `configuration()` | `[blue, blue, green, blue]` |
| 29 | `makeInvisible()` | Oculta esta máquina antes de crear otra |

**Qué decir:** “La máquina realiza la misma llamada de giro. La rueda concreta decide si avanza, copia a su vecina o retrocede. Rebel conserva el giro, pero cambia los permisos de otras operaciones”.

**Criterio de aceptación:** se cumplen los resultados de las tablas y las operaciones rechazadas conservan el estado.

Fecha de ejecución: __________. Resultado real: __________. Observaciones: __________.

## Prueba 2: símbolos efímeros y tímidos

Crea un objeto nuevo con **new SlotMachine()**, llamado `machine2`. Usa un solo color al inicio para observar cada reducción sin que cambie el color.

| Paso | Llamada en machine2 | Resultado esperado |
|---:|---|---|
| 1 | `addSymbol("ephemeral", 1, "blue")` | Catálogo con un azul efímero |
| 2 | `addWheel(1)` | Primera rueda vacía |
| 3 | `addWheel(2)` | Segunda rueda vacía |
| 4 | `placeSymbol(1, "blue")` | Azul de 42 píxeles |
| 5 | `placeSymbol(2, "blue")` | Otro azul de 42; jackpot lógico |
| 6 | `spin(1)` | Solo el primer azul disminuye |
| 7 | `symbolSize(1)` | `36` |
| 8 | `symbolSize(2)` | `42`: su estado es independiente |
| 9 | `spin(1, 10)` | El primero llega al tamaño mínimo |
| 10 | `symbolSize(1)` | `6`; permanece como un punto |
| 11 | `symbolSize(2)` | `42` |
| 12 | `addSymbol("shy", 2, "green")` | Añade verde tímido al catálogo |
| 13 | `placeSymbol(2, "green")` | Primera selección: verde visible |
| 14 | `isSymbolVisible(2)` | `true` |
| 15 | `placeSymbol(2, "green")` | Segunda selección: verde oculto |
| 16 | `isSymbolVisible(2)` | `false`; etiqueta `shy oculto` |
| 17 | `configuration()` | `[blue, green]`: oculto sigue asignado |
| 18 | `makeInvisible()` | Oculta la ventana |
| 19 | `makeVisible()` | Muestra la ventana, sin reseleccionar |
| 20 | `isSymbolVisible(2)` | Sigue en `false` |
| 21 | `placeSymbol(2, "green")` | Tercera selección: vuelve a aparecer |
| 22 | `spin(2)` | Pasa al azul efímero de esa rueda, de tamaño 42 |
| 23 | `spin(2)` | Vuelve al verde: cuarta selección, oculto |
| 24 | `isSymbolVisible(2)` | `false` |
| 25 | `exit()` | Termina esta máquina y oculta la ventana |

**Qué decir:** “Cada rueda conserva el estado de sus símbolos. El efímero reduce su tamaño con los giros; el tímido alterna cuando se selecciona. Ocultar la ventana no modifica esa selección”.

**Criterio de aceptación:** el tamaño nunca baja de 6, la otra rueda conserva su tamaño y shy alterna sin desaparecer del estado lógico.

Fecha de ejecución: __________. Resultado real: __________. Observaciones: __________.

## Ejecutar JUnit ante el profesor

1. Compila todo.
2. Clic derecho en `SlotMachineC4Test` y selecciona **Test All**: deben pasar 27 casos.
3. Clic derecho en `SlotMachineCC4Test` y **Test All**: deben pasar 2 casos.
4. Repite con las otras cinco clases de pruebas para verificar lo anterior. En total son 71 casos.
5. Abre un caso corto, por ejemplo `shouldNotLockARebel`: muestra la creación, la llamada `lock` y las dos comprobaciones.

Las pruebas JUnit se ejecutan en modo invisible: así no abren mensajes que bloqueen la ejecución. Los dos casos de CC4 están preparados para compartir; debes realizar ese intercambio para cumplir la condición del enunciado.
