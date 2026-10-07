import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/** [P -> SM] Dos casos de Paula Gacha preparados para compartir con el grupo. */
public class SlotMachineCC4Test
{
    private SlotMachine machine;

    /** [P] Prepara una máquina nueva para cada caso. */
    @Before
    public void setUp()
    {
        machine = new SlotMachine();
        machine.makeInvisible();
    }

    /** [P] Al intercambiar ruedas, lefty debe reconocer su nueva vecina. */
    @Test
    public void accordingGachaGShouldCopyTheNewNeighbor()
    {
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addWheel(1);
        machine.addWheel("lefty", 2);
        machine.addWheel(3);
        machine.placeSymbol(1, "red");
        machine.placeSymbol(2, "red");
        machine.placeSymbol(3, "blue");
        machine.swap(1, 3);
        machine.spin(2);
        assertEquals("blue", machine.configuration()[1]);
        assertTrue(machine.ok());
    }

    /** [P] Rechazar la eliminación de rebel conserva también el símbolo shy. */
    @Test
    public void accordingGachaGShouldKeepARebelAndItsSymbol()
    {
        machine.addSymbol("shy", 1, "green");
        machine.addWheel("rebel", 1);
        machine.placeSymbol(1, "green");
        machine.placeSymbol(1, "green");
        machine.delWheel(1);
        assertFalse(machine.ok());
        assertEquals("green", machine.configuration()[0]);
        assertFalse(machine.isSymbolVisible(1));
    }
}
