/** [SS -> S] Alterna su visibilidad cada vez que se selecciona.
 * @author Paula Gacha
 */
public class ShySymbol extends Symbol
{
    private boolean visible;

    /** [SS -> S] La primera selección lo hará visible. */
    public ShySymbol(String color)
    {
        super(color);
        visible = false;
    }

    /** [SS] Identifica el tipo tímido. */
    @Override
    public String getType()
    {
        return "shy";
    }

    /** [SS] Devuelve la visibilidad propia del símbolo. */
    @Override
    public boolean isVisible()
    {
        return visible;
    }

    /** [SS] Un número impar de selecciones invierte el estado. */
    @Override
    public void onSelected(long times)
    {
        if (times > 0 && times % 2 == 1) {
            visible = !visible;
        }
    }

    /** [SS] Copia el color y la visibilidad actual. */
    @Override
    public Symbol copy()
    {
        ShySymbol result = new ShySymbol(getColor());
        result.visible = visible;
        return result;
    }
}
