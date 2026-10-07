/** [VW -> W] Tipo propuesto: gira en sentido contrario al solicitado.
 * @author Paula Gacha
 */
public class ReverseWheel extends Wheel
{
    /** [VW -> W] Construye una rueda vacía. */
    public ReverseWheel(int x, int y)
    {
        super(x, y);
    }

    /** [VW] Identifica el tipo propuesto. */
    @Override
    public String getType()
    {
        return "reverse";
    }

    /** [VW] Usa un marco magenta. */
    @Override
    protected String getFrameColor()
    {
        return "magenta";
    }

    /** [VW] Invierte el desplazamiento usando long para evitar desbordamiento. */
    @Override
    protected long actualSteps(int steps)
    {
        return -(long) steps;
    }

    /** [VW] Calcula los pasos teniendo en cuenta el sentido contrario. */
    @Override
    public int stepsTo(int target, int total, int leftTarget)
    {
        return Math.floorMod(getCurrentSymbolIndex() - target, total);
    }
}
