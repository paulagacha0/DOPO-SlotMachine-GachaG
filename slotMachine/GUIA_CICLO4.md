# SlotMachine — guía del ciclo 4

Trabajo actual individual: **Paula Gacha**. Base: `DOPO-SlotMachine-GachaG`, commit `01eb9cacbfe60aa635cd058e64298dce250b5abb`.

La idea para explicar es: **SlotMachine coordina; cada Wheel decide cómo girar; cada Symbol decide cómo reaccionar.** El ciclo pide extender el simulador mediante diferentes tipos.

## 1. Qué pedían y qué se añadió

| Requisito | Solución y archivo |
|---|---|
| 16. Tipos de ruedas y símbolos | Clases base y subclases; creación por tipo en `SlotMachine` |
| 17. Rueda normal | `Wheel`: conserva el comportamiento anterior |
| 17. Rueda lefty | `LeftyWheel`: al girar copia la selección y apariencia de su vecina izquierda |
| 17. Rueda rebel | `RebelWheel`: rechaza bloqueo, intercambio y eliminación |
| 18. Símbolo normal | `Symbol`: tamaño y visibilidad constantes |
| 18. Símbolo ephemeral | `EphemeralSymbol`: disminuye hasta quedar como un punto |
| 18. Símbolo shy | `ShySymbol`: alterna visibilidad cada vez que se selecciona |
| 19. Un tipo propuesto | `ReverseWheel`: gira en sentido contrario |
| Diseño: agregar por tipo | `addWheel(String type, int pos)` y `addSymbol(String type, int pos, String color)` |
| Distinguir visualmente los tipos | Bordes y etiquetas en `Wheel`, texto en `Canvas` |
| Pruebas del ciclo | `SlotMachineC4Test`: 27 casos pequeños |
| Dos casos compartidos | `SlotMachineCC4Test`: dos casos preparados; falta compartirlos realmente |
| Dos pruebas de aceptación | Recorridos en `PRUEBAS_ACEPTACION_C4.md` |

El PDF también exige Astah completo, retrospectiva de todos los ciclos con estado por mini-ciclo y publicación del enlace en Moodle. Esta carpeta resuelve la parte BlueJ solicitada; esos productos se completan después.

## 2. Etiquetas y clases

| Etiqueta | Archivo | Responsabilidad |
|---|---|---|
| SM | `SlotMachine.java` | Catálogo, ruedas, validaciones y coordinación |
| W | `Wheel.java` | Selección, estados de símbolos, giro normal y dibujo |
| LW | `LeftyWheel.java` | Copiar a la vecina izquierda |
| RW | `RebelWheel.java` | Rechazar tres operaciones |
| VW | `ReverseWheel.java` | Invertir el sentido del giro |
| S | `Symbol.java` | Símbolo normal y comportamiento común |
| ES | `EphemeralSymbol.java` | Guardar y reducir el tamaño |
| SS | `ShySymbol.java` | Alternar la visibilidad |
| SC | `SlotMachineContest.java` | Conservar la estrategia del concurso |
| COL | `SymbolColors.java` | Validar colores y construir paletas |
| CI | `Circle.java` | Dibujar círculos; ahora permite `moveTo(x, y)` |
| CV | `Canvas.java` | Ventana, figuras y etiquetas |
| P | Clases de pruebas | Preparar, ejecutar y comprobar escenarios |

`Rectangle.java` y `Triangle.java` son figuras reutilizadas de shapes. No implementan las reglas nuevas.

Una etiqueta como `[SM -> W]` indica una conexión para estudiar. No ejecuta nada. La herencia se escribe con `extends`; los objetos se crean con `new`; los métodos se ejecutan mediante llamadas.

## 3. La relación que cambió

`SlotMachine` mantiene un catálogo ordenado y una lista de ruedas. Cada rueda guarda un índice actual y **copias independientes de los símbolos**, en el mismo orden del catálogo.

Antes bastaba con el índice. Ahora un símbolo tiene tamaño o visibilidad propios. Si dos ruedas compartieran el mismo objeto efímero mutable, reducirlo en una también afectaría a la otra. `copy()` evita esa mezcla.

Ejemplo: ambas ruedas muestran azul efímero de 42 píxeles. Al girar la primera, su azul puede quedar en 36; el azul de la segunda conserva 42. El catálogo conserva los colores y tipos; las copias guardan el estado de cada rueda.

Cada rueda contiene tres `Circle`: marco, fondo blanco y símbolo interior. Si está vacía o su símbolo shy está oculto, no se dibuja el círculo del símbolo. No se asigna un color por defecto a una rueda nueva.

## 4. Sigue una llamada completa

1. `addWheel("lefty", 2)` llama a la creación por tipo y construye `new LeftyWheel(...)`.
2. Se guarda en `ArrayList<Wheel>`: una `LeftyWheel` también es una `Wheel`.
3. `spin(2)` llama a `spin(2, 1)`.
4. `SlotMachine` valida y obtiene la rueda mediante `wheels.get(2 - 1)`.
5. `rotateWheelBySteps` llama a `wheel.rotate(...)`.
6. Java ejecuta `LeftyWheel.rotate` porque ese es el objeto real.
7. Con vecina, `copyState` copia la selección y apariencia; sin vecina, `super.rotate(...)` utiliza el giro normal.
8. Se actualiza el aspecto de jackpot y `ok()` queda en verdadero.

Eso es **polimorfismo**: una misma llamada produce el comportamiento del tipo real. Los símbolos también lo usan: `onSpin` reduce un efímero y `onSelected` alterna un shy.

- `extends`: heredar de una clase.
- `super(x, y)`: ejecutar el constructor del padre.
- `super.rotate(...)`: utilizar el método del padre.
- `@Override`: indicar que se reemplaza un método heredado.

Para crear otro tipo, se añade una subclase y su opción en `createWheel` o `createSymbol`. La máquina no necesita preguntar el tipo en cada giro.

## 5. Reglas de las ruedas

Con catálogo `[red, blue, green]`, una normal que está en red pasa a blue con un paso. Una reverse pasa de red a green porque retrocede. Los pasos negativos también se invierten.

`RebelWheel` responde falso a `canBeLocked`, `canBeSwapped` y `canBeRemoved`. La máquina revisa el permiso antes de cambiar algo. El giro sí está permitido. Ante una orden rechazada, se conserva el estado y `ok()` devuelve falso; en modo visible aparece un mensaje.

Decisiones adoptadas donde el enunciado no precisa los detalles:

- Lefty copia selección, tamaño y visibilidad. Conserva su tipo y bloqueo. Copiar no vuelve a seleccionar un shy.
- Si la vecina está vacía, lefty queda vacía. Una lefty inicialmente vacía puede copiar.
- Sin vecina, gira normalmente. Bloqueada, no copia.
- La vecina es la anterior en la lista, incluso si el dibujo continúa en otra fila. Insertar, eliminar o intercambiar actualiza esa referencia.
- `spin()` recorre de izquierda a derecha; lefty copia a la vecina ya actualizada.
- Cero pasos no selecciona ni copia. Intercambiar mueve los objetos completos, con su estado.

`spin(String[])` planea antes de mover: lefty puede conservar su selección o copiar el destino de su vecina. Si algún destino es imposible, se rechaza toda la operación sin efectuar cambios parciales.

## 6. Reglas de los símbolos

**Normal:** mide 42 píxeles y siempre permite dibujarse. Sus métodos `onSpin` y `onSelected` no necesitan hacer cambios. `Symbol` es concreta porque ya representa este tipo normal.

**Ephemeral:** cada paso que sale de ese símbolo le resta 6 píxeles, hasta un mínimo de 6: `42, 36, 30, 24, 18, 12, 6, 6...`. Con un catálogo de un solo símbolo, cada paso lo reduce y vuelve a seleccionarlo. Con varios, conserva su reducción al salir y regresar. Asignarlo con `placeSymbol` no lo reduce.

Ejemplo: catálogo `[blue efímero, red normal]`. Desde blue se gira a red y luego se vuelve a blue. El azul aparece de 36. La salida desde red no lo reduce otra vez.

**Shy:** primera selección visible, segunda oculta, tercera visible. Seleccionarlo con `placeSymbol` o llegar a él durante un giro cuenta como selección. Mostrar u ocultar la ventana no cuenta.

Un shy oculto sigue asignado: `configuration()` devuelve su color y puede participar en un jackpot. Una rueda vacía devuelve `null`. `isSymbolVisible(wheel)` distingue la visibilidad del símbolo de la visibilidad de la ventana.

**Varios pasos:** `Wheel.rotate` cuenta vueltas completas y pasos extra, para saber cuántas veces se sale de cada símbolo y se selecciona el siguiente. Así funciona incluso con cantidades grandes. Con 3 símbolos y 8 pasos hay 2 vueltas completas y 2 pasos extra. Para shy, un número par de selecciones conserva su visibilidad; uno impar la invierte.

Hasta 100 pasos se animan en modo visible. Los giros mayores se calculan directamente para evitar una espera enorme. El resultado lógico es el mismo.

## 7. Cómo reconocerlos

| Elemento | Señal visual |
|---|---|
| Normal | Marco negro y texto `normal` |
| Lefty | Marco azul y texto `lefty` |
| Rebel | Marco rojo y texto `rebel` |
| Reverse | Marco magenta y texto `reverse` |
| Bloqueada | Etiqueta `[B]` |
| Jackpot | Marcos amarillos; el texto conserva el tipo |
| Efímero | Texto como `ephemeral:36` y tamaño reducido |
| Shy oculto | Interior blanco y texto `shy oculto` |
| Vacía | Interior blanco y texto `vacia` |

Consultas añadidas: `wheelType(2)`, `symbolSize(2)`, `isSymbolVisible(2)` e `isLocked(2)`. Usan posiciones desde 1. Una posición inválida en estas consultas lanza `IllegalArgumentException`; las consultas no modifican `ok()`.

Los colores admitidos siguen siendo los seis nombres básicos y `#RRGGBB`; blanco está reservado para el vacío. No se añadió reconocimiento de todos los nombres CSS.

## 8. Pruebas básicas

| Clase | Casos | Tema |
|---|---:|---|
| SlotMachineC1Test | 10 | Creación, catálogo y asignación |
| SlotMachineC2Test | 20 | Bloqueo, intercambio y giros |
| SlotMachineCC2Test | 2 | Casos previos conservados |
| SlotMachineContestTest | 8 | Constructor y solución del concurso |
| SlotMachineContestCTest | 2 | Casos previos del concurso |
| SlotMachineC4Test | 27 | Tipos nuevos y combinaciones |
| SlotMachineCC4Test | 2 | Vecina tras intercambiar; rebel con shy |
| **Total** | **71** | **Aprobados automáticamente** |

Cada caso prepara datos, ejecuta una acción y compara el resultado. `@Before` prepara una máquina nueva antes de cada prueba. `@Test` marca un caso. `assertEquals` compara un valor; `assertArrayEquals`, un arreglo; `assertTrue` espera verdadero y `assertFalse`, falso.

Ejemplo: `shouldCopyTheLeftWheel` crea una normal y una lefty, asigna red y blue, gira la segunda y compara el resultado con `[red, red]`.

Se simplificaron las pruebas del concurso: ya no necesitan `ObservedMachine`. Quedó `FixedContest`, una clase interna pequeña que entrega una máquina conocida y hereda la estrategia real. `checkSolution` comprueba el jackpot y reproduce los movimientos devueltos para verificar que efectivamente sirven.

En BlueJ: abrir `package.bluej`, compilar y ejecutar **Test All** en cada clase de pruebas. Se usa modo invisible para evitar ventanas y pausas; no ver el dibujo durante JUnit es esperado.

## 9. Mini-ciclos y estado real

| Grupo de trabajo | Estado |
|---|---|
| Separar símbolos y agregar creación por tipo | Implementado y comprobado |
| Incorporar lefty y rebel | Implementado y comprobado |
| Incorporar ephemeral y shy | Implementado y comprobado |
| Incorporar reverse y diferenciar visualmente | Implementado; dibujo comprobado en memoria |
| Pruebas y sustentación | 71 casos pasan; dos recorridos manuales preparados |
| Cierre de entrega | Pendientes: ejecución local en BlueJ, compartir casos, Astah y retrospectiva |

La comprobación gráfica usó el dibujo del proyecto sobre una imagen en memoria. No se abrió BlueJ en este entorno. Los recorridos de aceptación se deben ejecutar en tu computador; no se presentan como ya aprobados por el profesor. Esta tabla describe el trabajo, no inventa commits ni horas.

## 10. Respuestas para practicar

**¿Por qué Symbol?** Un símbolo ahora guarda estado y comportamiento; un String con el color ya no basta.

**¿Por qué copias?** Para que reducir u ocultar un símbolo en una rueda no cambie el de otra.

**¿Dónde está el polimorfismo?** En las llamadas a `rotate`, `onSpin` y `onSelected`, que responden según la subclase real.

**¿Por qué no abstract?** `Wheel` y `Symbol` ya representan los tipos normales y pueden crearse directamente.

**¿Qué tipo nuevo propusiste?** Reverse, que invierte el desplazamiento y conserva el resto de la rueda normal.

**¿Cambió el concurso?** Se conserva su algoritmo. `SlotMachine(n)` sigue creando ruedas y símbolos normales para resolverlo.

**¿Qué significa un círculo blanco?** La etiqueta permite saber si está vacío o si contiene un shy oculto.

**¿Por dónde estudio?** Rebel, Reverse, Shy, Ephemeral y finalmente el recorrido completo de `SlotMachine.spin` a `Wheel.rotate` y a los métodos de `Symbol`.
