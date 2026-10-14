import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/** [P -> SM] Casos pequeños de los nuevos tipos. Autora: Paula Gacha. */
public class SlotMachineC4Test
{
    private SlotMachine machine;

    /** [P] Cada prueba comienza con una máquina vacía e invisible. */
    @Before
    public void setUp()
    {
        machine = new SlotMachine();
        machine.makeInvisible();
    }

    /** [P] Agrega los cuatro tipos; todos empiezan vacíos. */
    @Test
    public void shouldAddAllWheelTypes()
    {
        machine.addWheel("normal", 1);
        machine.addWheel("lefty", 2);
        machine.addWheel("rebel", 3);
        machine.addWheel("reverse", 4);
        assertArrayEquals(new String[] {null, null, null, null}, machine.configuration());
        assertEquals("lefty", machine.wheelType(2));
        assertEquals("rebel", machine.wheelType(3));
        assertEquals("reverse", machine.wheelType(4));
    }

    /** [P] Agrega los tres tipos de símbolos al mismo catálogo. */
    @Test
    public void shouldAddAllSymbolTypes()
    {
        machine.addSymbol("normal", 1, "red");
        machine.addSymbol("ephemeral", 2, "blue");
        machine.addSymbol("shy", 3, "green");
        assertArrayEquals(new String[] {"red", "blue", "green"}, machine.symbols());
        assertTrue(machine.ok());
    }

    /** [P] Lefty copia a su vecina, aunque se pidan varios pasos. */
    @Test
    public void shouldCopyTheLeftWheel()
    {
        addThreeSymbols();
        machine.addWheel(1);
        machine.addWheel("lefty", 2);
        machine.placeSymbol(1, "red");
        machine.placeSymbol(2, "blue");
        machine.spin(2, 5);
        assertArrayEquals(new String[] {"red", "red"}, machine.configuration());
    }

    /** [P] Sin vecina, lefty funciona como una rueda normal. */
    @Test
    public void shouldRotateLeftyWithoutANeighbor()
    {
        addThreeSymbols();
        machine.addWheel("lefty", 1);
        machine.placeSymbol(1, "red");
        machine.spin(1);
        assertEquals("blue", machine.configuration()[0]);
    }

    /** [P] Una lefty vacía puede copiar un símbolo. */
    @Test
    public void shouldFillAnEmptyLefty()
    {
        addThreeSymbols();
        machine.addWheel(1);
        machine.addWheel("lefty", 2);
        machine.placeSymbol(1, "green");
        machine.spin(2);
        assertEquals("green", machine.configuration()[1]);
    }

    /** [P] Si la vecina está vacía, la copia queda vacía. */
    @Test
    public void shouldCopyAnEmptyNeighbor()
    {
        addThreeSymbols();
        machine.addWheel(1);
        machine.addWheel("lefty", 2);
        machine.placeSymbol(2, "blue");
        machine.spin(2);
        assertNull(machine.configuration()[1]);
        assertTrue(machine.ok());
    }

    /** [P] El giro general actualiza primero a la vecina izquierda. */
    @Test
    public void shouldSpinFromLeftToRight()
    {
        addThreeSymbols();
        machine.addWheel(1);
        machine.addWheel("lefty", 2);
        machine.placeSymbol(1, "red");
        machine.placeSymbol(2, "green");
        machine.spin();
        assertArrayEquals(new String[] {"blue", "blue"}, machine.configuration());
    }

    /** [P] El bloqueo también impide que lefty copie. */
    @Test
    public void shouldKeepALockedLeftyStill()
    {
        addThreeSymbols();
        machine.addWheel(1);
        machine.addWheel("lefty", 2);
        machine.placeSymbol(1, "red");
        machine.placeSymbol(2, "blue");
        machine.lock(2);
        machine.spin(2);
        assertFalse(machine.ok());
        assertEquals("blue", machine.configuration()[1]);
    }

    /** [P] Rebel no acepta bloqueo. */
    @Test
    public void shouldNotLockARebel()
    {
        machine.addWheel("rebel", 1);
        machine.lock(1);
        assertFalse(machine.ok());
        assertFalse(machine.isLocked(1));
    }

    /** [P] Rebel no acepta eliminación. */
    @Test
    public void shouldNotDeleteARebel()
    {
        machine.addWheel("rebel", 1);
        machine.delWheel(1);
        assertFalse(machine.ok());
        assertEquals(1, machine.configuration().length);
    }

    /** [P] Tampoco se puede intercambiar poniendo rebel como segunda rueda. */
    @Test
    public void shouldNotSwapWithARebel()
    {
        machine.addWheel(1);
        machine.addWheel("rebel", 2);
        machine.swap(1, 2);
        assertFalse(machine.ok());
        assertEquals("rebel", machine.wheelType(2));
    }

    /** [P] Rebel sí conserva el giro normal. */
    @Test
    public void shouldSpinARebel()
    {
        addThreeSymbols();
        machine.addWheel("rebel", 1);
        machine.placeSymbol(1, "red");
        machine.spin(1);
        assertEquals("blue", machine.configuration()[0]);
    }

    /** [P] Reverse retrocede cuando se solicita avanzar. */
    @Test
    public void shouldRotateInReverse()
    {
        addThreeSymbols();
        machine.addWheel("reverse", 1);
        machine.placeSymbol(1, "red");
        machine.spin(1);
        assertEquals("green", machine.configuration()[0]);
        machine.spin(1, -1);
        assertEquals("red", machine.configuration()[0]);
    }

    /** [P] Con un solo símbolo se observa directamente la reducción. */
    @Test
    public void shouldShrinkAnEphemeral()
    {
        machine.addSymbol("ephemeral", 1, "red");
        machine.addWheel(1);
        machine.placeSymbol(1, "red");
        machine.spin(1);
        assertEquals(36, machine.symbolSize(1));
    }

    /** [P] El símbolo efímero nunca desaparece por tamaño. */
    @Test
    public void shouldStopShrinkingAtAPoint()
    {
        machine.addSymbol("ephemeral", 1, "red");
        machine.addWheel(1);
        machine.placeSymbol(1, "red");
        machine.spin(1, 10);
        assertEquals(6, machine.symbolSize(1));
    }

    /** [P] Girar una rueda no reduce el símbolo de otra. */
    @Test
    public void shouldKeepIndependentSizes()
    {
        machine.addSymbol("ephemeral", 1, "red");
        machine.addWheel(1);
        machine.addWheel(2);
        machine.placeSymbol(1, "red");
        machine.placeSymbol(2, "red");
        machine.spin(1);
        assertEquals(36, machine.symbolSize(1));
        assertEquals(42, machine.symbolSize(2));
    }

    /** [P] El tamaño se conserva al salir del símbolo y regresar a él. */
    @Test
    public void shouldRememberAnEphemeralSize()
    {
        machine.addSymbol("ephemeral", 1, "red");
        machine.addSymbol(2, "blue");
        machine.addWheel(1);
        machine.placeSymbol(1, "red");
        machine.spin(1, 2);
        assertEquals("red", machine.configuration()[0]);
        assertEquals(36, machine.symbolSize(1));
    }

    /** [P] Shy aparece, se oculta y aparece al seleccionarlo. */
    @Test
    public void shouldAlternateAShySymbol()
    {
        machine.addSymbol("shy", 1, "green");
        machine.addWheel(1);
        machine.placeSymbol(1, "green");
        assertTrue(machine.isSymbolVisible(1));
        machine.placeSymbol(1, "green");
        assertFalse(machine.isSymbolVisible(1));
        machine.placeSymbol(1, "green");
        assertTrue(machine.isSymbolVisible(1));
    }

    /** [P] Shy oculto sigue siendo un símbolo asignado. */
    @Test
    public void shouldKeepAHiddenSymbolAssigned()
    {
        machine.addSymbol("shy", 1, "green");
        machine.addWheel(1);
        machine.placeSymbol(1, "green");
        machine.spin(1);
        assertFalse(machine.isSymbolVisible(1));
        assertEquals("green", machine.configuration()[0]);
        assertTrue(machine.isJackpot());
    }

    /** [P] Cero pasos no reduce ni cambia la visibilidad. */
    @Test
    public void shouldKeepSymbolsUnchangedWithZeroSteps()
    {
        machine.addSymbol("shy", 1, "green");
        machine.addWheel(1);
        machine.placeSymbol(1, "green");
        machine.spin(1, 0);
        assertTrue(machine.isSymbolVisible(1));
        assertTrue(machine.ok());
    }

    /** [P] Lefty copia también el tamaño que tiene la vecina. */
    @Test
    public void shouldCopyTheEphemeralSize()
    {
        machine.addSymbol("ephemeral", 1, "red");
        machine.addWheel(1);
        machine.addWheel("lefty", 2);
        machine.placeSymbol(1, "red");
        machine.spin(1, 2);
        machine.spin(2);
        assertEquals(30, machine.symbolSize(2));
    }

    /** [P] Lefty copia la visibilidad sin volver a seleccionar el símbolo. */
    @Test
    public void shouldCopyAShyState()
    {
        machine.addSymbol("shy", 1, "green");
        machine.addWheel(1);
        machine.addWheel("lefty", 2);
        machine.placeSymbol(1, "green");
        machine.placeSymbol(1, "green");
        machine.spin(2);
        assertFalse(machine.isSymbolVisible(2));
        assertEquals("green", machine.configuration()[1]);
    }

    /** [P] Elimina un símbolo especial sin asignar otro por defecto. */
    @Test
    public void shouldDeleteASpecialSymbol()
    {
        machine.addSymbol("shy", 1, "green");
        machine.addWheel(1);
        machine.placeSymbol(1, "green");
        machine.delSymbol("green");
        assertNull(machine.configuration()[0]);
        assertEquals(0, machine.symbolSize(1));
    }

    /** [P] Rechaza nombres de tipos que no existen. */
    @Test
    public void shouldRejectUnknownTypes()
    {
        machine.addWheel("unknown", 1);
        assertFalse(machine.ok());
        assertEquals(0, machine.configuration().length);
        machine.addSymbol("unknown", 1, "red");
        assertFalse(machine.ok());
        assertEquals(0, machine.symbols().length);
    }

    /** [P] El color sigue siendo único, aunque el tipo sea diferente. */
    @Test
    public void shouldRejectRepeatedColorsAcrossTypes()
    {
        machine.addSymbol(1, "red");
        machine.addSymbol("shy", 2, "red");
        assertFalse(machine.ok());
        assertEquals(1, machine.symbols().length);
    }

    /** [P] La configuración objetivo también funciona con reverse y lefty. */
    @Test
    public void shouldReachATargetWithSpecialWheels()
    {
        addThreeSymbols();
        machine.addWheel("reverse", 1);
        machine.addWheel("lefty", 2);
        machine.placeSymbol(1, "red");
        machine.placeSymbol(2, "blue");
        machine.spin(new String[] {"green", "green"});
        assertArrayEquals(new String[] {"green", "green"}, machine.configuration());
        assertTrue(machine.ok());
    }

    /** [P] Un destino imposible para lefty no modifica ninguna rueda. */
    @Test
    public void shouldRejectAnImpossibleLeftyTarget()
    {
        addThreeSymbols();
        machine.addWheel(1);
        machine.addWheel("lefty", 2);
        machine.placeSymbol(1, "red");
        machine.placeSymbol(2, "blue");
        machine.spin(new String[] {"blue", "green"});
        assertFalse(machine.ok());
        assertArrayEquals(new String[] {"red", "blue"}, machine.configuration());
    }

    /** [P] Prepara un catálogo pequeño para las pruebas de giro. */
    private void addThreeSymbols()
    {
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addSymbol(3, "green");
    }
}
