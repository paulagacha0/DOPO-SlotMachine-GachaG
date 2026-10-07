import static org.junit.Assert.*;
import org.junit.Test;

/** [P -> SC, SM] Pruebas cortas del solucionador. Adaptación del ciclo 4: Paula Gacha. */
public class SlotMachineContestTest
{
    /** [P] El constructor crea la misma cantidad de ruedas y símbolos. */
    @Test
    public void shouldCreateThreeWheelsAndSymbols()
    {
        SlotMachine machine = new SlotMachine(3);
        assertEquals(3, machine.symbols().length);
        assertEquals(3, machine.configuration().length);
        assertFalse(machine.isJackpot());
    }

    /** [P] Resuelve tres símbolos diferentes. */
    @Test
    public void shouldSolveDifferentSymbols()
    {
        checkSolution(new int[] {0, 1, 2});
    }

    /** [P] Resuelve una configuración con repeticiones. */
    @Test
    public void shouldSolveRepeatedSymbols()
    {
        checkSolution(new int[] {0, 0, 1, 1, 2});
    }

    /** [P] Si ya ganó, no necesita movimientos. */
    @Test
    public void shouldKeepAnExistingJackpot()
    {
        int[][] actions = checkSolution(new int[] {1, 1, 1});
        assertEquals(0, actions.length);
    }

    /** [P] Esta configuración necesita un solo giro en la primera rueda. */
    @Test
    public void shouldStopAfterOneMove()
    {
        int[][] actions = checkSolution(new int[] {0, 1, 1});
        assertEquals(1, actions.length);
        assertArrayEquals(new int[] {1, 1}, actions[0]);
    }

    /** [P] Comprueba el tamaño máximo con todos los símbolos diferentes. */
    @Test
    public void shouldSolveFiftyWheels()
    {
        int[] initial = new int[50];
        for (int i = 0; i < 50; i++) {
            initial[i] = i;
        }
        int[][] actions = checkSolution(initial);
        assertTrue(actions.length <= 5001);
    }

    /** [P] La versión real también crea y resuelve una máquina aleatoria. */
    @Test
    public void shouldSolveARandomMachine()
    {
        SlotMachineContest contest = new SlotMachineContest();
        int[][] actions = contest.solve(3);
        assertTrue(actions.length > 0);
        assertTrue(actions.length <= 19);
    }

    /** [P] JUnit espera el error porque dos ruedas no están permitidas. */
    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectTwoWheels()
    {
        SlotMachineContest contest = new SlotMachineContest();
        contest.solve(2);
    }

    /** [P -> SM, SC] Prepara un caso, lo resuelve y reproduce sus movimientos. */
    public static int[][] checkSolution(int[] initial)
    {
        SlotMachine machine = new SlotMachine(initial.length);
        String[] colors = machine.symbols();
        for (int i = 0; i < initial.length; i++) {
            machine.placeSymbol(i + 1, colors[initial[i]]);
        }
        SlotMachineContest contest = new FixedContest(machine);
        int[][] actions = contest.solve(initial.length);
        assertTrue(machine.isJackpot());
        assertTrue(actions.length <= 10000);
        for (int i = 0; i < initial.length; i++) {
            machine.placeSymbol(i + 1, colors[initial[i]]);
        }
        for (int[] action : actions) {
            assertEquals(2, action.length);
            machine.spin(action[0], action[1]);
            assertTrue(machine.ok());
        }
        assertTrue(machine.isJackpot());
        return actions;
    }

    /** [P -> SC] Cambia solo la máquina inicial; hereda la estrategia real. */
    private static class FixedContest extends SlotMachineContest
    {
        private final SlotMachine preparedMachine;

        /** [P] Guarda la máquina preparada para la prueba. */
        FixedContest(SlotMachine machine)
        {
            preparedMachine = machine;
        }

        /** [P] Entrega la máquina conocida en lugar de crear otra al azar. */
        @Override
        protected SlotMachine createMachine(int n)
        {
            return preparedMachine;
        }
    }
}
