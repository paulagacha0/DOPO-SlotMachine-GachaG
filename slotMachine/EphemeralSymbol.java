/** [ES -> S] Pierde tamaño al girar hasta quedar como un punto.
 * @author Paula Gacha
 */
public class EphemeralSymbol extends Symbol
{
    public static final int MIN_SIZE = 6;
    private static final int SHRINK = 6;
    private int size;

    /** [ES -> S] Empieza con el tamaño normal. */
    public EphemeralSymbol(String color)
    {
        super(color);
        size = NORMAL_SIZE;
    }

    /** [ES] Identifica el tipo efímero. */
    @Override
    public String getType()
    {
        return "ephemeral";
    }

    /** [ES] Devuelve el tamaño que conserva esta rueda. */
    @Override
    public int getSize()
    {
        return size;
    }

    /** [ES] Resta seis píxeles por giro, sin bajar de seis. */
    @Override
    public void onSpin(long times)
    {
        if (times > 0) {
            long reductions = Math.min(times, NORMAL_SIZE / SHRINK);
            size = Math.max(MIN_SIZE, size - (int) reductions * SHRINK);
        }
    }

    /** [ES] Copia el color y el tamaño actual. */
    @Override
    public Symbol copy()
    {
        EphemeralSymbol result = new EphemeralSymbol(getColor());
        result.size = size;
        return result;
    }
}
