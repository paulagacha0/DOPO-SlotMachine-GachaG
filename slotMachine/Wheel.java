import java.util.ArrayList;

/**
 * [W] Rueda normal; guarda la selección y los estados de sus símbolos.
 * @author Paula Gacha (extensión del ciclo 4)
 */
public class Wheel
{
    private static final int NO_SYMBOL = -1;
    private static final int OUTER_SIZE = 52;
    private final ArrayList<Symbol> symbols;
    private final Circle frameCircle;
    private final Circle backgroundCircle;
    private final Circle symbolCircle;
    private final Object symbolLabel;
    private int currentSymbolIndex;
    private int xPosition;
    private int yPosition;
    private int position;
    private boolean visible;
    private boolean locked;
    private boolean jackpot;
    private Wheel leftWheel;

    /** [W -> CI] Crea el marco y el interior vacío, sin asignar un símbolo. */
    public Wheel(int x, int y)
    {
        symbols = new ArrayList<>();
        frameCircle = new Circle();
        backgroundCircle = new Circle();
        symbolCircle = new Circle();
        symbolLabel = new Object();
        currentSymbolIndex = NO_SYMBOL;
        xPosition = x;
        yPosition = y;
    }

    /** [W] Identifica el tipo normal. */
    public String getType()
    {
        return "normal";
    }

    /** [W] Color del marco cuando no hay jackpot. */
    protected String getFrameColor()
    {
        return "black";
    }

    /** [W] Guarda la vecina y el número, después de insertar o intercambiar. */
    public void setPosition(int position, Wheel leftWheel)
    {
        this.position = position;
        this.leftWheel = leftWheel;
    }

    /** [W] Entrega la vecina a las subclases. */
    protected Wheel getLeftWheel()
    {
        return leftWheel;
    }

    /** [W -> S] Inserta una copia para que cada rueda tenga su propio estado. */
    public void addSymbol(int index, Symbol symbol)
    {
        symbols.add(index, symbol.copy());
        if (hasSymbol() && currentSymbolIndex >= index) {
            currentSymbolIndex++;
        }
    }

    /** [W] Elimina el símbolo y ajusta el índice sin seleccionar otro. */
    public void removeSymbol(int index)
    {
        symbols.remove(index);
        if (currentSymbolIndex == index) {
            currentSymbolIndex = NO_SYMBOL;
        } else if (currentSymbolIndex > index) {
            currentSymbolIndex--;
        }
        refresh();
    }

    /** [W -> S] Selecciona un símbolo y aplica su reacción a la selección. */
    public void setSymbol(int index)
    {
        currentSymbolIndex = index;
        symbols.get(index).onSelected(1);
        refresh();
    }

    /** [W] Deja el interior vacío. */
    public void clearSymbol()
    {
        currentSymbolIndex = NO_SYMBOL;
        refresh();
    }

    /** [W] Indica si existe un símbolo seleccionado. */
    public boolean hasSymbol()
    {
        return currentSymbolIndex != NO_SYMBOL;
    }

    /** [W] Devuelve el índice actual; -1 significa vacío. */
    public int getCurrentSymbolIndex()
    {
        return currentSymbolIndex;
    }

    /** [W -> S] Devuelve el tamaño del símbolo seleccionado, o cero si está vacío. */
    public int getSymbolSize()
    {
        return hasSymbol() ? symbols.get(currentSymbolIndex).getSize() : 0;
    }

    /** [W -> S] Consulta la visibilidad propia del símbolo, no la ventana. */
    public boolean isSymbolVisible()
    {
        return hasSymbol() && symbols.get(currentSymbolIndex).isVisible();
    }

    /** [W] Una rueda normal necesita símbolo y estar desbloqueada para girar. */
    public boolean canRotate()
    {
        return !locked && hasSymbol();
    }

    /** [W] El tipo normal conserva el sentido solicitado. */
    protected long actualSteps(int steps)
    {
        return steps;
    }

    /**
     * [W -> S] Gira y actualiza los símbolos visitados, incluso en modo invisible.
     * Cuenta vueltas completas para no repetir millones de pasos.
     */
    public void rotate(int steps, int symbolCount)
    {
        if (!canRotate() || symbolCount == 0 || steps == 0) {
            return;
        }
        long displacement = actualSteps(steps);
        int direction = displacement > 0 ? 1 : -1;
        long count = Math.abs(displacement);
        long fullTurns = count / symbolCount;
        int extraSteps = (int) (count % symbolCount);
        for (int step = 0; step < symbolCount; step++) {
            long visits = fullTurns;
            if (step < extraSteps) {
                visits++;
            }
            int from = Math.floorMod(currentSymbolIndex + direction * step, symbolCount);
            int to = Math.floorMod(from + direction, symbolCount);
            symbols.get(from).onSpin(visits);
            symbols.get(to).onSelected(visits);
        }
        currentSymbolIndex = (int) Math.floorMod(
            currentSymbolIndex + displacement, (long) symbolCount);
        refresh();
    }

    /** [W] Calcula los pasos para llegar a un destino; -1 indicaría imposible. */
    public int stepsTo(int target, int total, int leftTarget)
    {
        return Math.floorMod(target - currentSymbolIndex, total);
    }

    /** [W -> S] Copia la selección y apariencia, conservando tipo y bloqueo propios. */
    protected void copyState(Wheel source)
    {
        currentSymbolIndex = source.currentSymbolIndex;
        if (source.hasSymbol()) {
            symbols.set(currentSymbolIndex, source.symbols.get(currentSymbolIndex).copy());
        }
        refresh();
    }

    /** [W] El tipo normal permite bloquear. */
    public boolean canBeLocked()
    {
        return true;
    }

    /** [W] El tipo normal permite intercambiar. */
    public boolean canBeSwapped()
    {
        return true;
    }

    /** [W] El tipo normal permite eliminar. */
    public boolean canBeRemoved()
    {
        return true;
    }

    /** [W] Bloquea solo si el tipo lo permite. */
    public void lock()
    {
        if (canBeLocked()) {
            locked = true;
            refresh();
        }
    }

    /** [W] Quita el bloqueo. */
    public void unlock()
    {
        locked = false;
        refresh();
    }

    /** [W] Consulta el bloqueo. */
    public boolean isLocked()
    {
        return locked;
    }

    /** [W] Cambia la ubicación en pantalla. */
    public void moveTo(int x, int y)
    {
        xPosition = x;
        yPosition = y;
        refresh();
    }

    /** [W -> CI, CV] Muestra esta rueda y sus etiquetas. */
    public void makeVisible()
    {
        visible = true;
        refresh();
    }

    /** [W -> CI, CV] Oculta el dibujo, sin cambiar el estado de un símbolo shy. */
    public void makeInvisible()
    {
        if (visible) {
            hideDrawing();
        }
        visible = false;
    }

    /** [W] Cambia el marco ganador, manteniendo la etiqueta del tipo. */
    public void setJackpotAppearance(boolean jackpot)
    {
        if (this.jackpot != jackpot) {
            this.jackpot = jackpot;
            refresh();
        }
    }

    /** [W -> CI, CV] Dibuja el marco, fondo blanco, símbolo centrado y etiquetas. */
    private void refresh()
    {
        if (!visible) {
            return;
        }
        hideDrawing();
        frameCircle.changeSize(OUTER_SIZE);
        frameCircle.changeColor(jackpot ? "yellow" : getFrameColor());
        frameCircle.moveTo(xPosition, yPosition);
        backgroundCircle.changeSize(Symbol.NORMAL_SIZE);
        backgroundCircle.changeColor("white");
        backgroundCircle.moveTo(xPosition + 5, yPosition + 5);
        frameCircle.makeVisible();
        backgroundCircle.makeVisible();
        String detail = "vacia";
        if (hasSymbol()) {
            Symbol symbol = symbols.get(currentSymbolIndex);
            int size = symbol.getSize();
            symbolCircle.changeColor(symbol.getColor());
            symbolCircle.changeSize(size);
            int margin = (OUTER_SIZE - size) / 2;
            symbolCircle.moveTo(xPosition + margin, yPosition + margin);
            if (symbol.isVisible()) {
                symbolCircle.makeVisible();
            }
            detail = symbol.getType() + ":" + size;
            if (!symbol.isVisible()) {
                detail = symbol.getType() + " oculto";
            }
        }
        String title = position + " " + getType() + (locked ? " [B]" : "");
        Canvas.getCanvas().drawText(this, title, xPosition - 4, yPosition + 66);
        Canvas.getCanvas().drawText(symbolLabel, detail, xPosition - 4, yPosition + 80);
    }

    /** [W -> CI, CV] Retira las figuras y los dos textos de esta rueda. */
    private void hideDrawing()
    {
        symbolCircle.makeInvisible();
        backgroundCircle.makeInvisible();
        frameCircle.makeInvisible();
        Canvas.getCanvas().erase(this);
        Canvas.getCanvas().erase(symbolLabel);
    }
}
