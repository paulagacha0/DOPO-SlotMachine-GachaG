import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/** [P -> SM] Pruebas sencillas del catálogo, la asignación y el giro. */
public class SlotMachineC1Test
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

    /** [P] Comprueba el estado inicial de la máquina. */
    @Test
    public void shouldCreateAnEmptyMachine()
    {
        assertEquals(0, machine.symbols().length);
        assertEquals(0, machine.configuration().length);
        assertFalse(machine.isJackpot());
    }

    /** [P] Comprueba que una rueda nueva comience vacía. */
    @Test
    public void shouldAddAnEmptyWheelWithoutDefaultSymbol()
    {
        machine.addSymbol(1, "red");
        machine.addWheel(1);

        assertEquals(1, machine.configuration().length);
        assertNull(machine.configuration()[0]);
        assertFalse(machine.isJackpot());
        assertTrue(machine.ok());
    }

    /** [P] Comprueba que se respete la posición de inserción. */
    @Test
    public void shouldInsertSymbolsAtTheRequestedPositions()
    {
        machine.addSymbol(1, "red");
        machine.addSymbol(1, "blue");

        assertArrayEquals(new String[] {"blue", "red"}, machine.symbols());
    }

    /** [P] Comprueba la asignación de un símbolo conocido. */
    @Test
    public void shouldPlaceAnExistingSymbolOnAWheel()
    {
        machine.addSymbol(1, "red");
        machine.addWheel(1);
        machine.placeSymbol(1, "red");

        assertArrayEquals(new String[] {"red"}, machine.configuration());
        assertTrue(machine.ok());
    }

    /** [P] Comprueba que insertar un color no cambie el símbolo ya mostrado. */
    @Test
    public void shouldPreserveDisplayedSymbolAfterCatalogInsertion()
    {
        machine.addSymbol(1, "red");
        machine.addWheel(1);
        machine.placeSymbol(1, "red");

        machine.addSymbol(1, "blue");

        assertArrayEquals(new String[] {"red"}, machine.configuration());
    }

    /** [P] Comprueba que se vacíe la rueda al desaparecer su símbolo. */
    @Test
    public void shouldClearWheelWhenItsSymbolIsDeleted()
    {
        machine.addSymbol(1, "red");
        machine.addWheel(1);
        machine.placeSymbol(1, "red");

        machine.delSymbol("red");

        assertNull(machine.configuration()[0]);
        assertFalse(machine.isJackpot());
    }

    /** [P] Comprueba un giro de un paso. */
    @Test
    public void shouldRotateAnAssignedWheel()
    {
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addWheel(1);
        machine.placeSymbol(1, "red");

        machine.spin(1);

        assertArrayEquals(new String[] {"blue"}, machine.configuration());
        assertTrue(machine.ok());
    }

    /** [P] Comprueba el rechazo de un giro sin símbolo inicial. */
    @Test
    public void shouldNotRotateAnEmptyWheel()
    {
        machine.addSymbol(1, "red");
        machine.addWheel(1);

        machine.spin(1);

        assertNull(machine.configuration()[0]);
        assertFalse(machine.ok());
    }

    /** [P] Comprueba que todas las ruedas deben estar configuradas para ganar. */
    @Test
    public void shouldDetectJackpotOnlyWhenEveryWheelHasTheSameSymbol()
    {
        machine.addSymbol(1, "red");
        machine.addWheel(1);
        machine.addWheel(2);
        machine.placeSymbol(1, "red");

        assertFalse(machine.isJackpot());

        machine.placeSymbol(2, "red");

        assertTrue(machine.isJackpot());
    }

    /** [P] Comprueba dos entradas inválidas. */
    @Test
    public void shouldRejectInvalidPositionsAndDuplicateSymbols()
    {
        machine.addWheel(0);
        assertFalse(machine.ok());

        machine.addSymbol(1, "red");
        machine.addSymbol(2, "RED");

        assertFalse(machine.ok());
        assertArrayEquals(new String[] {"red"}, machine.symbols());
    }
}
