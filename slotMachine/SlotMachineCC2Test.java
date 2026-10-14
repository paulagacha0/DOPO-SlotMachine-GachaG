import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/** [P -> SM] Dos casos del ciclo 2 conservados como antecedentes. */
public class SlotMachineCC2Test
{
    /** [P] Máquina que se prepara antes de cada prueba. */
    private SlotMachine machine;

    /** [P] Prepara una máquina nueva e invisible antes de cada caso de prueba. */
    @Before
    public void setUp()
    {
        machine = new SlotMachine();
        machine.makeInvisible();
    }

    /** [P] Comprueba el giro general con una rueda bloqueada en un caso identificado por el equipo. */
    @Test
    public void accordingGgMdShouldKeepLockedWheelStillWhileSpinningAll()
    {
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addWheel(1);
        machine.addWheel(2);
        machine.placeSymbol(1, "red");
        machine.placeSymbol(2, "red");
        machine.lock(1);

        machine.spin();

        assertArrayEquals(new String[] {"red", "blue"}, machine.configuration());
        assertTrue(machine.ok());
    }

    /** [P] Comprueba que se rechace todo el destino cuando uno de sus colores es desconocido. */
    @Test
    public void accordingGgMdShouldRejectInvalidTargetWithoutPartialChanges()
    {
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addWheel(1);
        machine.addWheel(2);
        machine.placeSymbol(1, "red");
        machine.placeSymbol(2, "blue");

        machine.spin(new String[] {"blue", "cyan"});

        assertArrayEquals(new String[] {"red", "blue"}, machine.configuration());
        assertFalse(machine.ok());
    }
}
