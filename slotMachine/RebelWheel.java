/** [RW -> W] Gira normalmente, pero rechaza bloqueo, intercambio y eliminación.
 * @author Paula Gacha
 */
public class RebelWheel extends Wheel
{
    /** [RW -> W] Construye una rueda vacía. */
    public RebelWheel(int x, int y)
    {
        super(x, y);
    }

    /** [RW] Identifica el tipo. */
    @Override
    public String getType()
    {
        return "rebel";
    }

    /** [RW] Usa un marco rojo. */
    @Override
    protected String getFrameColor()
    {
        return "red";
    }

    /** [RW] Impide bloquear esta rueda. */
    @Override
    public boolean canBeLocked()
    {
        return false;
    }

    /** [RW] Impide intercambiar esta rueda. */
    @Override
    public boolean canBeSwapped()
    {
        return false;
    }

    /** [RW] Impide eliminar esta rueda. */
    @Override
    public boolean canBeRemoved()
    {
        return false;
    }
}
