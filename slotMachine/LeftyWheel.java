/** [LW -> W] Al girar copia el símbolo y la apariencia de su vecina izquierda.
 * @author Paula Gacha
 */
public class LeftyWheel extends Wheel
{
    /** [LW -> W] Construye una rueda vacía. */
    public LeftyWheel(int x, int y)
    {
        super(x, y);
    }

    /** [LW] Identifica el tipo. */
    @Override
    public String getType()
    {
        return "lefty";
    }

    /** [LW] Usa un marco azul. */
    @Override
    protected String getFrameColor()
    {
        return "blue";
    }

    /** [LW] Puede copiar incluso si ella o su vecina están vacías. */
    @Override
    public boolean canRotate()
    {
        return !isLocked() && (getLeftWheel() != null || hasSymbol());
    }

    /** [LW -> W] Sin vecina gira normalmente; con vecina copia su estado. */
    @Override
    public void rotate(int steps, int symbolCount)
    {
        if (isLocked() || steps == 0 || symbolCount == 0) {
            return;
        }
        if (getLeftWheel() == null) {
            super.rotate(steps, symbolCount);
        } else {
            copyState(getLeftWheel());
        }
    }

    /** [LW] Comprueba si el destino se logra quedándose quieta o copiando. */
    @Override
    public int stepsTo(int target, int total, int leftTarget)
    {
        if (getLeftWheel() == null) {
            return super.stepsTo(target, total, leftTarget);
        }
        if (getCurrentSymbolIndex() == target) {
            return 0;
        }
        return leftTarget == target ? 1 : -1;
    }
}
