/** [S] Símbolo normal: conserva su color, tamaño y visibilidad.
 * @author Paula Gacha
 */
public class Symbol
{
    public static final int NORMAL_SIZE = 42;
    private final String color;

    /** [S] Guarda el color del símbolo. */
    public Symbol(String color)
    {
        this.color = color;
    }

    /** [S] Devuelve el color. */
    public String getColor()
    {
        return color;
    }

    /** [S] Identifica el tipo para mostrarlo en pantalla. */
    public String getType()
    {
        return "normal";
    }

    /** [S] Devuelve el diámetro en píxeles. */
    public int getSize()
    {
        return NORMAL_SIZE;
    }

    /** [S] El símbolo normal siempre permite dibujarse. */
    public boolean isVisible()
    {
        return true;
    }

    /** [S] Recibe cuántas veces se seleccionó; el normal no cambia. */
    public void onSelected(long times)
    {
    }

    /** [S] Recibe los giros que parten de él; el normal no cambia. */
    public void onSpin(long times)
    {
    }

    /** [S] Crea una copia independiente para una rueda. */
    public Symbol copy()
    {
        return new Symbol(color);
    }
}
