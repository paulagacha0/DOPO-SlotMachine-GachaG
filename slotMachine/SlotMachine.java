import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 * [SM] Coordina ruedas, catálogo, validaciones y visibilidad.
 * @author Paula Gacha (extensión del ciclo 4)
 */
public class SlotMachine
{
    private static final int FIRST_WHEEL_X = 30;
    private static final int WHEEL_Y = 40;
    private static final int WHEEL_SPACING = 90;
    private static final int WHEELS_PER_ROW = 10;
    private static final int ROW_SPACING = 110;
    private static final int STEP_DELAY = 200;
    private final ArrayList<Wheel> wheels;
    private final ArrayList<Symbol> symbols;
    private boolean visible;
    private boolean successful;
    private boolean running;

    /** [SM] Crea una máquina vacía, lista para agregar elementos. */
    public SlotMachine()
    {
        wheels = new ArrayList<>();
        symbols = new ArrayList<>();
        visible = true;
        successful = true;
        running = true;
    }

    /**
     * [SM -> COL, W] Crea n ruedas normales con símbolos normales al azar.
     * @param n cantidad de ruedas y símbolos, entre 3 y 50
     * @throws IllegalArgumentException si el tamaño no está permitido
     */
    public SlotMachine(int n)
    {
        this();
        if (n < 3 || n > 50) {
            throw new IllegalArgumentException("The size must be between 3 and 50.");
        }
        visible = false;
        for (String color : SymbolColors.createPalette(n)) {
            addSymbol(symbols.size() + 1, color);
        }
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            addWheel(i + 1);
            wheels.get(i).setSymbol(random.nextInt(n));
        }
        if (hasJackpot()) {
            int next = (wheels.get(0).getCurrentSymbolIndex() + 1) % n;
            wheels.get(n - 1).setSymbol(next);
        }
        updateJackpotAppearance();
    }

    /** [SM] Conserva la operación anterior: agrega una rueda normal. */
    public void addWheel(int pos)
    {
        addWheel("normal", pos);
    }

    /**
     * [SM -> W] Agrega una rueda del tipo indicado, inicialmente vacía.
     * @param type normal, lefty, rebel o reverse
     * @param pos posición desde 1 hasta la cantidad de ruedas más 1
     */
    public void addWheel(String type, int pos)
    {
        if (!ensureRunning()) {
            return;
        }
        if (pos < 1 || pos > wheels.size() + 1) {
            fail("La posición de la rueda no es válida.");
            return;
        }
        Wheel wheel = createWheel(type);
        if (wheel == null) {
            fail("El tipo de rueda no existe.");
            return;
        }
        for (int i = 0; i < symbols.size(); i++) {
            wheel.addSymbol(i, symbols.get(i));
        }
        wheels.add(pos - 1, wheel);
        arrangeWheels();
        updateJackpotAppearance();
        if (visible) {
            wheel.makeVisible();
        }
        successful = true;
    }

    /** [SM -> W] Elimina la rueda si su tipo lo permite. */
    public void delWheel(int pos)
    {
        if (!ensureRunning() || !checkWheel(pos)) {
            return;
        }
        Wheel wheel = wheels.get(pos - 1);
        if (!wheel.canBeRemoved()) {
            fail("Una rueda rebel no se puede eliminar.");
            return;
        }
        wheel.makeInvisible();
        wheels.remove(pos - 1);
        arrangeWheels();
        updateJackpotAppearance();
        successful = true;
    }

    /** [SM -> W] Intercambia los objetos completos, si ambos tipos lo permiten. */
    public void swap(int wheel1, int wheel2)
    {
        if (!ensureRunning() || !checkWheel(wheel1) || !checkWheel(wheel2)) {
            return;
        }
        Wheel first = wheels.get(wheel1 - 1);
        Wheel second = wheels.get(wheel2 - 1);
        if (!first.canBeSwapped() || !second.canBeSwapped()) {
            fail("Una rueda rebel no se puede intercambiar.");
            return;
        }
        wheels.set(wheel1 - 1, second);
        wheels.set(wheel2 - 1, first);
        arrangeWheels();
        updateJackpotAppearance();
        successful = true;
    }

    /** [SM -> W] Bloquea una rueda que acepte bloqueo. */
    public void lock(int wheel)
    {
        if (!ensureRunning() || !checkWheel(wheel)) {
            return;
        }
        Wheel selected = wheels.get(wheel - 1);
        if (!selected.canBeLocked() || selected.isLocked()) {
            fail("La rueda no admite bloqueo o ya está bloqueada.");
            return;
        }
        selected.lock();
        successful = true;
    }

    /** [SM -> W] Desbloquea una rueda previamente bloqueada. */
    public void unlock(int wheel)
    {
        if (!ensureRunning() || !checkWheel(wheel)) {
            return;
        }
        Wheel selected = wheels.get(wheel - 1);
        if (!selected.isLocked()) {
            fail("La rueda no está bloqueada.");
            return;
        }
        selected.unlock();
        successful = true;
    }

    /** [SM] Conserva la operación anterior: agrega un símbolo normal. */
    public void addSymbol(int pos, String color)
    {
        addSymbol("normal", pos, color);
    }

    /**
     * [SM -> S, W] Agrega el símbolo al catálogo y una copia a cada rueda.
     * @param type normal, ephemeral o shy
     * @param pos posición desde 1 hasta la cantidad de símbolos más 1
     * @param color color único del símbolo; el blanco se reserva para el vacío
     */
    public void addSymbol(String type, int pos, String color)
    {
        if (!ensureRunning()) {
            return;
        }
        String normalized = SymbolColors.normalize(color);
        if (pos < 1 || pos > symbols.size() + 1) {
            fail("La posición del símbolo no es válida.");
            return;
        }
        if (!SymbolColors.isSymbol(normalized) || findSymbol(normalized) >= 0) {
            fail("El color no es válido o ya existe.");
            return;
        }
        Symbol symbol = createSymbol(type, normalized);
        if (symbol == null) {
            fail("El tipo de símbolo no existe.");
            return;
        }
        symbols.add(pos - 1, symbol);
        for (Wheel wheel : wheels) {
            wheel.addSymbol(pos - 1, symbol);
        }
        successful = true;
    }

    /** [SM -> W] Elimina el símbolo y vacía las ruedas que lo tenían seleccionado. */
    public void delSymbol(String symbol)
    {
        if (!ensureRunning()) {
            return;
        }
        int index = findSymbol(symbol);
        if (index < 0) {
            fail("El símbolo no existe.");
            return;
        }
        symbols.remove(index);
        for (Wheel wheel : wheels) {
            wheel.removeSymbol(index);
        }
        updateJackpotAppearance();
        successful = true;
    }

    /** [SM -> W] Selecciona directamente un símbolo; shy alterna su visibilidad. */
    public void placeSymbol(int wheel, String symbol)
    {
        if (!ensureRunning() || !checkWheel(wheel)) {
            return;
        }
        int index = findSymbol(symbol);
        if (index < 0) {
            fail("El símbolo no existe.");
            return;
        }
        wheels.get(wheel - 1).setSymbol(index);
        updateJackpotAppearance();
        successful = true;
    }

    /** [SM] Gira una rueda un paso. */
    public void spin(int wheel)
    {
        spin(wheel, 1);
    }

    /** [SM -> W] Valida y solicita un giro; cada tipo decide cómo realizarlo. */
    public void spin(int wheel, int steps)
    {
        if (!ensureRunning() || !checkWheel(wheel)) {
            return;
        }
        Wheel selected = wheels.get(wheel - 1);
        if (symbols.isEmpty() || !selected.canRotate()) {
            fail("La rueda está bloqueada o no tiene un símbolo para girar.");
            return;
        }
        rotateWheelBySteps(selected, steps);
        updateJackpotAppearance();
        successful = true;
    }

    /** [SM -> W] Gira de izquierda a derecha; lefty copia la vecina ya actualizada. */
    public void spin()
    {
        if (!ensureRunning()) {
            return;
        }
        if (wheels.isEmpty() || symbols.isEmpty()) {
            fail("Faltan ruedas o símbolos para girar.");
            return;
        }
        boolean canSpin = false;
        for (Wheel wheel : wheels) {
            if (!wheel.isLocked()) {
                canSpin = true;
                if (!wheel.canRotate()) {
                    fail("Hay una rueda vacía que no puede girar.");
                    return;
                }
            }
        }
        if (!canSpin) {
            fail("Todas las ruedas están bloqueadas.");
            return;
        }
        for (Wheel wheel : wheels) {
            if (!wheel.isLocked()) {
                rotateWheelBySteps(wheel, 1);
            }
        }
        updateJackpotAppearance();
        successful = true;
    }

    /** [SM -> W] Primero planea todos los giros; rechaza destinos imposibles sin cambiar nada. */
    public void spin(String[] setSymbols)
    {
        if (!ensureRunning()) {
            return;
        }
        if (setSymbols == null || wheels.isEmpty() || setSymbols.length != wheels.size()) {
            fail("La configuración solicitada no tiene el tamaño correcto.");
            return;
        }
        int[] targets = new int[wheels.size()];
        int[] steps = new int[wheels.size()];
        for (int i = 0; i < wheels.size(); i++) {
            targets[i] = findSymbol(setSymbols[i]);
            Wheel wheel = wheels.get(i);
            if (targets[i] < 0 || !wheel.hasSymbol()) {
                fail("Hay un símbolo desconocido o una rueda sin configurar.");
                return;
            }
            if (wheel.isLocked()) {
                if (wheel.getCurrentSymbolIndex() != targets[i]) {
                    fail("Una rueda bloqueada no puede alcanzar el destino.");
                    return;
                }
            } else {
                int leftTarget = i == 0 ? -1 : targets[i - 1];
                steps[i] = wheel.stepsTo(targets[i], symbols.size(), leftTarget);
                if (steps[i] < 0) {
                    fail("Una rueda lefty no puede alcanzar ese destino copiando a su vecina.");
                    return;
                }
            }
        }
        for (int i = 0; i < wheels.size(); i++) {
            if (!wheels.get(i).isLocked()) {
                rotateWheelBySteps(wheels.get(i), steps[i]);
            }
        }
        updateJackpotAppearance();
        successful = true;
    }

    /** [SM -> S] Devuelve los colores del catálogo en orden. */
    public String[] symbols()
    {
        String[] result = new String[symbols.size()];
        for (int i = 0; i < symbols.size(); i++) {
            result[i] = symbols.get(i).getColor();
        }
        return result;
    }

    /** [SM -> W] Devuelve los colores asignados, aunque shy esté oculto; null significa vacío. */
    public String[] configuration()
    {
        String[] result = new String[wheels.size()];
        for (int i = 0; i < wheels.size(); i++) {
            Wheel wheel = wheels.get(i);
            if (wheel.hasSymbol()) {
                result[i] = symbols.get(wheel.getCurrentSymbolIndex()).getColor();
            }
        }
        return result;
    }

    /** [SM] Cuenta símbolos asignados diferentes, sin contar las ruedas vacías. */
    public int distinctSymbols()
    {
        ArrayList<String> distinct = new ArrayList<>();
        for (String color : configuration()) {
            if (color != null && !distinct.contains(color)) {
                distinct.add(color);
            }
        }
        return distinct.size();
    }

    /** [SM] Comprueba que todas las ruedas tengan el mismo símbolo asignado. */
    public boolean isJackpot()
    {
        return hasJackpot();
    }

    /** [SM -> W] Consulta el diámetro actual del símbolo; cero si está vacío. */
    public int symbolSize(int wheel)
    {
        return getWheel(wheel).getSymbolSize();
    }

    /** [SM -> W] Consulta si el símbolo permite dibujarse, independientemente de la ventana. */
    public boolean isSymbolVisible(int wheel)
    {
        return getWheel(wheel).isSymbolVisible();
    }

    /** [SM -> W] Devuelve el tipo de una rueda. */
    public String wheelType(int wheel)
    {
        return getWheel(wheel).getType();
    }

    /** [SM -> W] Consulta el bloqueo de una rueda. */
    public boolean isLocked(int wheel)
    {
        return getWheel(wheel).isLocked();
    }

    /** [SM -> W] Muestra la máquina sin volver a seleccionar sus símbolos. */
    public void makeVisible()
    {
        if (!ensureRunning()) {
            return;
        }
        visible = true;
        for (Wheel wheel : wheels) {
            wheel.makeVisible();
        }
        updateJackpotAppearance();
        successful = true;
    }

    /** [SM -> W, CV] Oculta la máquina conservando su estado. */
    public void makeInvisible()
    {
        if (!ensureRunning()) {
            return;
        }
        for (Wheel wheel : wheels) {
            wheel.makeInvisible();
        }
        visible = false;
        Canvas.closeCanvas();
        successful = true;
    }

    /** [SM] Oculta la máquina y termina la recepción de órdenes. */
    public void exit()
    {
        if (!ensureRunning()) {
            return;
        }
        makeInvisible();
        running = false;
        successful = true;
    }

    /** [SM] Devuelve si el último comando fue válido; las consultas no lo cambian. */
    public boolean ok()
    {
        return successful;
    }

    /** [SM] Valida que el simulador siga activo. */
    private boolean ensureRunning()
    {
        if (!running) {
            fail("El simulador ya terminó.");
        }
        return running;
    }

    /** [SM] Valida una posición externa de rueda. */
    private boolean checkWheel(int wheel)
    {
        if (wheel < 1 || wheel > wheels.size()) {
            fail("La posición de la rueda no es válida.");
            return false;
        }
        return true;
    }

    /** [SM] Obtiene una rueda para las consultas, sin modificar ok. */
    private Wheel getWheel(int wheel)
    {
        if (wheel < 1 || wheel > wheels.size()) {
            throw new IllegalArgumentException("The wheel position is invalid.");
        }
        return wheels.get(wheel - 1);
    }

    /** [SM -> COL, S] Busca por color en el catálogo. */
    private int findSymbol(String color)
    {
        String normalized = SymbolColors.normalize(color);
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i).getColor().equals(normalized)) {
                return i;
            }
        }
        return -1;
    }

    /** [SM] Normaliza el nombre del tipo. */
    private String normalizeType(String type)
    {
        return type == null ? "" : type.trim().toLowerCase(Locale.ROOT);
    }

    /** [SM -> W, LW, RW, VW] Elige la clase al crear; los giros usan polimorfismo. */
    private Wheel createWheel(String type)
    {
        String name = normalizeType(type);
        if (name.equals("normal")) {
            return new Wheel(FIRST_WHEEL_X, WHEEL_Y);
        }
        if (name.equals("lefty")) {
            return new LeftyWheel(FIRST_WHEEL_X, WHEEL_Y);
        }
        if (name.equals("rebel")) {
            return new RebelWheel(FIRST_WHEEL_X, WHEEL_Y);
        }
        if (name.equals("reverse")) {
            return new ReverseWheel(FIRST_WHEEL_X, WHEEL_Y);
        }
        return null;
    }

    /** [SM -> S, ES, SS] Elige el tipo de símbolo al crearlo. */
    private Symbol createSymbol(String type, String color)
    {
        String name = normalizeType(type);
        if (name.equals("normal")) {
            return new Symbol(color);
        }
        if (name.equals("ephemeral")) {
            return new EphemeralSymbol(color);
        }
        if (name.equals("shy")) {
            return new ShySymbol(color);
        }
        return null;
    }

    /** [SM -> W] Actualiza posición y vecina; izquierda significa anterior en la lista. */
    private void arrangeWheels()
    {
        for (int i = 0; i < wheels.size(); i++) {
            Wheel left = i == 0 ? null : wheels.get(i - 1);
            wheels.get(i).setPosition(i + 1, left);
            int x = FIRST_WHEEL_X + (i % WHEELS_PER_ROW) * WHEEL_SPACING;
            int y = WHEEL_Y + (i / WHEELS_PER_ROW) * ROW_SPACING;
            wheels.get(i).moveTo(x, y);
        }
    }

    /** [SM -> W, CV] Anima giros pequeños; los grandes se calculan directamente. */
    private void rotateWheelBySteps(Wheel wheel, int steps)
    {
        long count = Math.abs((long) steps);
        if (!visible || count > 100 || steps == 0) {
            wheel.rotate(steps, symbols.size());
            return;
        }
        int direction = steps > 0 ? 1 : -1;
        for (long i = 0; i < count; i++) {
            wheel.rotate(direction, symbols.size());
            updateJackpotAppearance();
            Canvas.getCanvas().wait(STEP_DELAY);
        }
    }

    /** [SM -> W] Ganar exige al menos una rueda y ninguna vacía. */
    private boolean hasJackpot()
    {
        if (wheels.isEmpty() || !wheels.get(0).hasSymbol()) {
            return false;
        }
        int first = wheels.get(0).getCurrentSymbolIndex();
        for (Wheel wheel : wheels) {
            if (!wheel.hasSymbol() || wheel.getCurrentSymbolIndex() != first) {
                return false;
            }
        }
        return true;
    }

    /** [SM -> W] Aplica el marco amarillo si hay jackpot. */
    private void updateJackpotAppearance()
    {
        boolean jackpot = hasJackpot();
        for (Wheel wheel : wheels) {
            wheel.setJackpotAppearance(jackpot);
        }
    }

    /** [SM] Registra el fallo; solo muestra mensajes si la máquina está visible. */
    private void fail(String message)
    {
        successful = false;
        if (visible) {
            JOptionPane.showMessageDialog(null, message, "Slot Machine", JOptionPane.WARNING_MESSAGE);
        }
    }
}
