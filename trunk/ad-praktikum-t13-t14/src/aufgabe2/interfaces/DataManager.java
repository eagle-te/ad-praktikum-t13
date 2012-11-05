package aufgabe2.interfaces;

import java.util.Queue;

/**
 * Created with IntelliJ IDEA.
 * User: abg667
 * Date: 30.10.12
 * Time: 11:32
 */
public interface DataManager {

    /**
     * Zum lesen unsortierter Blöcke an Datensätzen, um diese per
     * InsertSort für das MergeSort vorzubereiten.
     * Beispiel; Datensätze d = {2,6,8,34,74,23,63,234,45,267}, Blockgröße b = 4
     *           DataManager.readBlock() -> {2,6,8,34}
     *           DataManager.readBlock() -> {74,23,63,234}
     *           DataManager.readBlock() -> {45,267}
     * @return einen DataWrapper inkl. unsortiertem Block
     */
    public DataWrapper readBlock();

    /**
     * Liest zwei, bereits mit InsertSort sortierte, DataWrapper.
     *
     * @return DataWrapper Array mit größe 2, [0] linker, [1] rechter Datawrapper
     */
    public DataWrapper[] read();

    /**
     * Schreibt Blöcke von Datensätzen abwechselnd in zwei Dateien.
     *
     *
     * @param dataWrapper beinhaltet einen Array mit sortierten Datensätzen
     */
    public void write(DataWrapper dataWrapper);

    /**
     *
     * Erstellt einen neuen DataWrapper, anstelle der Klasse.
     *
     * @param data Array von Datensätzen
     * @param size of Array von Datensätzen
     * @return DataWrapper mit angegebenen Daten
     */
    public DataWrapper createDataWrapper(int[] data, int size, boolean folgeKomplett);

}