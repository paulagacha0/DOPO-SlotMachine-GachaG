# Copiar el ciclo 4 y subirlo con GitHub Desktop

La carpeta corresponde al repositorio **DOPO-SlotMachine-GachaG**, tu trabajo actual individual. El ZIP es para descargar y extraer; al repositorio se suben los archivos extraídos.

## 1. Ubicar tu proyecto

1. Cierra BlueJ para que no reescriba `package.bluej` mientras copias.
2. Abre GitHub Desktop y selecciona el repositorio `DOPO-SlotMachine-GachaG`.
3. Selecciona la rama en la que entregarás el trabajo. Si estás trabajando directamente en `main`, puedes continuar en ella.
4. Pulsa **Fetch origin**. Si aparece **Pull origin**, úsalo para traer lo publicado antes de copiar.
5. Si tienes cambios propios todavía sin guardar, haz un commit de ellos antes de reemplazar archivos. Esta versión parte del commit `01eb9cacbfe60aa635cd058e64298dce250b5abb`.
6. Ve a **Repository > Show in Explorer**. Esa ventana muestra la carpeta local correcta.

## 2. Copiar los archivos

1. Descarga `SlotMachine_Ciclo4_Paula_BlueJ.zip` y usa **Extraer todo**.
2. Dentro aparece una carpeta llamada `slotMachine`.
3. Abre esa carpeta extraída y copia **todo su contenido**.
4. En la ventana que abriste desde GitHub Desktop, entra a la carpeta existente `slotMachine`.
5. Pega allí y acepta reemplazar los archivos con el mismo nombre. También deben copiarse las clases nuevas.
6. Comprueba la ruta: debe quedar `DOPO-SlotMachine-GachaG/slotMachine/package.bluej`. Evita crear `slotMachine/slotMachine`.

No necesitas recrear el repositorio. Este cambio conserva el historial. La carpeta entregada contiene Java, configuración BlueJ y cuatro documentos de apoyo.

## 3. Comprobar en tu computador

1. Abre `slotMachine/package.bluej` con BlueJ, o usa **Proyecto > Abrir** y selecciona esa carpeta.
2. Pulsa **Compilar**.
3. Deben aparecer 21 archivos Java, incluidas las siete clases de pruebas. Las nuevas son `Symbol`, `EphemeralSymbol`, `ShySymbol`, `LeftyWheel`, `RebelWheel`, `ReverseWheel`, `SlotMachineC4Test` y `SlotMachineCC4Test`.
4. Ejecuta **Test All** en cada clase de pruebas: se esperan 71 casos aprobados en total.
5. Sigue `PRUEBAS_ACEPTACION_C4.md` para comprobar la ventana y los comportamientos.
6. Cierra BlueJ después de comprobarlo.

## 4. Hacer el commit y el push

1. Vuelve a GitHub Desktop. En **Changes** aparecerán archivos nuevos y modificados.
2. Selecciona los archivos de `slotMachine` que pertenecen a esta actualización. Si aparecen `.class` o `.ctxt`, desmárcalos: BlueJ los vuelve a generar.
3. Puedes hacer **un solo commit** con toda esta actualización ya integrada.
4. En **Summary** escribe: `Add cycle 4 wheel and symbol types`.
5. En **Description**, si quieres: `Añade lefty, rebel, reverse, ephemeral y shy; simplifica las pruebas y prepara dos demostraciones.`
6. Pulsa **Commit to main**, o el botón con el nombre de tu rama actual.
7. Pulsa **Push origin**. Si es una rama que nunca has subido, aparecerá **Publish branch**.
8. Usa **Repository > View on GitHub** y abre `slotMachine` en esa misma rama. Comprueba que están las clases nuevas.
9. Si publicaste una rama distinta de `main`, recuerda que sigue siendo otra rama: intégrala mediante el pull request antes de entregar el contenido de `main`.

## 5. Lo que sigue después de BlueJ

- Actualizar Astah para que refleje estas clases y relaciones.
- Completar la retrospectiva de todos los ciclos con datos reales.
- Compartir realmente los dos casos de `SlotMachineCC4Test`.
- Publicar el TXT del enlace para Moodle. Para el trabajo individual, siguiendo tu apellido y la inicial ya usados, el nombre sería `GachaG.txt`; confirma la regla que el profesor aplique a la entrega individual.

Documentación de GitHub Desktop: https://docs.github.com/en/desktop/making-changes-in-a-branch/pushing-changes-to-github-from-github-desktop
