package aufgabe2.algorithm;

import aufgabe2.algorithm.parallel.QuickSortMultiThreaded;
import aufgabe2.algorithm.parallel.stolen.Quicksort;
import aufgabe2.data.DataManagerImpl;
import aufgabe2.interfaces.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class ExternerMergeSort {

	/**
	 * Sortiert die angegebene externe Datei, welche ausschließlich aus 4-Byte-Integer-Zahlen besteht
	 * @param inputFile Der Pfad der Datei, welche sortiert werden soll. An jenem Verzeichnis muss
	 * 		  Platz sein für eine weitere Datei mit der gleichen Größe. 
	 * @return Der Pfad der sortierten Datei
	 */
	public static String sort(String inputFile) {
		DataManager tapes = new DataManagerImpl(inputFile);
        ExecutorService threadPool = Executors.newCachedThreadPool();
        long start;
//        ExecutorService threadPool = new ForkJoinPool();

		// Blockweise Sortierung
        start = System.currentTimeMillis();
        ByteBuffer data = tapes.readBlock(); // lese  von "band" 1
        System.out.println("readcall: "+(System.currentTimeMillis() - start)+" ms");
												// initialisierung
		while (data.hasRemaining()) { // solange das "band" nicht leer ist
//			blockSort_quick(data.getData(), 0, data.getSize() - 1);// Sortieren
			IntBuffer intBuffer = data.asIntBuffer();
            start = System.currentTimeMillis();
//             Quicksort.forkJoinQuicksort((ForkJoinPool)threadPool,intBuffer); <-- noch schneller aber funzt mit intbuffer noch nicht ;/
            System.out.println("Start Quicksort");
			QuickSortMultiThreaded.sort(intBuffer, 0, intBuffer.limit() - 1, threadPool);
            System.out.println("Stop Quicksort");
			//QuickSortMultiThreaded.blockSort_quick(intBuffer,0,intBuffer.limit() - 1); <-- single thread version
            System.out.println("quicksort: "+(System.currentTimeMillis()-start)+" ms");
            start = System.currentTimeMillis();
            tapes.writeBlock(data); // zurückschreiben
            System.out.println("writecall: "+(System.currentTimeMillis() - start )+" ms");
			start = System.currentTimeMillis();
            data = tapes.readBlock(); // lese wieder von "band" 1
            System.out.println("readcall: "+(System.currentTimeMillis() - start)+" ms");
		}

		// Mergen
		while (merge(tapes)) {// Merge für jeden Block aufrufen, bis keine
								// Blöcke mehr kommen
			// der merge tut schon alles, also do nothing
		}
        //System.out.println("fertig");
        //tapes.closeAllChannelsIfOpen();
       return tapes.completeSort();
        //return "guck im projekt verzeichnis";
	}

	/**
	 * Führt einen ganzen Datenblock zusammen
	 * @param ioTapes die Datenquelle
	 * @return ob es Daten zum Zusammenführen gab.
	 */
	private static boolean merge(DataManager ioTapes) {

		InputBuffer linksIn = ioTapes.readLeftChannel(); //new InputBuffer(ioTapes,InputBuffer.Channels.LEFTCHANNEL);
		InputBuffer rechtsIn = ioTapes.readRightChannel(); //new InputBuffer(ioTapes, InputBuffer.Channels.RIGHTCHANNEL);
			
		//Terminierung: wenn Beiden Input-Channels schon zu beginn an leer ist, dann gibt es nix zu mergen...
		if ((!linksIn.hasCurrent()) && (!rechtsIn.hasCurrent()))
			return false; // Keine weiteren Blöcke, die Sortiert werden könnten

        OutputBuffer output = ioTapes.createOuputBuffer(); //new OutputBuffer(ioTapes);
    	
        boolean linksHasCurrent = linksIn.hasCurrent();
        boolean rechtsHasCurrent = rechtsIn.hasCurrent();
        
        if (linksHasCurrent && rechtsHasCurrent){
        	int linksElem = linksIn.getCurrent();
			int rechtsElem = rechtsIn.getCurrent();
			
			while (linksHasCurrent && rechtsHasCurrent) {
				if (linksElem <= rechtsElem) {
					output.push(linksElem);
					linksHasCurrent = linksIn.moveNext();
					linksElem = linksIn.getCurrent();
				} else {
					output.push(rechtsElem);
					rechtsHasCurrent = rechtsIn.moveNext();
					rechtsElem = rechtsIn.getCurrent();
				}
			}
        }
        
		

		while (linksIn.hasCurrent()) {
			output.push(linksIn.getCurrent());
			linksIn.moveNext();
		}

		while (rechtsIn.hasCurrent()) {
			output.push(rechtsIn.getCurrent());
			rechtsIn.moveNext();
		}
        
				
		output.finishBlock();

		return true;

	}

	//Sortieralgorithmen-@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	
	/**
	 * Sortiert die Daten von der linken bis zur rechten Grenze mit InsertionSort
	 * @param data das Array, auf welchem sortiert werden soll
	 * @param links die linke Grenze (einschließlich), ab welcher mit der Sortierung begonnen werden soll
	 * @param rechts die rechte Grenze (einschließlich), bis zu welcher sortiert werden soll
	 */
	static void blockSort_insertion(int[] data, int links, int rechts) {
		for (int i = links + 1; i <= rechts; i++) {
			int j = i;
			int itemToSort = data[i];
			while (j > 0 && data[j - 1] > itemToSort) {
				// insert
				data[j] = data[j - 1];
				j = j - 1;
			}
			data[j] = itemToSort;
		}

	}
	
	/**
	 *Sortiert die Daten von der linken bis zur rechten Grenze mit QuickSort
	 * @param data das Array, auf welchem sortiert werden soll
	 * @param links die linke Grenze (einschließlich), ab welcher mit der Sortierung begonnen werden soll
	 * @param rechts die rechte Grenze (einschließlich), bis zu welcher sortiert werden soll
	 * @return
	 */
	static void blockSort_quick(int[] data, int links, int rechts) {
		if (rechts - links < 10) {
			blockSort_insertion(data, links, rechts);
		} else {
			int positionPivot = quickSwap(data, links, rechts);
			blockSort_quick(data, links, positionPivot - 1);
			blockSort_quick(data, positionPivot + 1, rechts);
		}
	}

	/**
	 * Hilfsmethode für blockSort_quick: Ermittelt ein Pivot-Element und sortiert die liste so, dass alle Elemente kleiner als das Pivot-Element links davon stehen, die größeren Element rechts
	 * @param data das Array, auf welchem sortiert werden soll
	 * @param links die linke Grenze (einschließlich), ab welcher mit der Sortierung begonnen werden soll
	 * @param rechts die rechte Grenze (einschließlich), bis zu welcher sortiert werden soll
	 * @return der Index des Pivot-ELements
	 */
	private static int quickSwap(int[] data, int links, int rechts) {

		int i = links;
		int j = rechts - 1; // Starte mit j links vom Pivotelement
		int pivot = data[rechts];
		while (i <= j) {
			while ((data[i] <= pivot) && (i < rechts))
				i++;
			while ((links <= j) && (data[j] > pivot))
				j--;
			// a[j].key ≤ pivot
			if (i < j) {
				swap(data, i, j);
			}
		}
		swap(data, i, rechts); // Pivotelement in die Mitte tauschen
		return i;
	}

	/**
	 * Hilfsmethode für quickSwap: vertauscht zwei Elemente miteinander 
	 * @param data das Array, auf welchem vertauscht werden soll
	 * @param pos1 der Index des 1. Elements
	 * @param pos2 der Index des 2. Elements
	 */
	private static void swap(int[] data, int pos1, int pos2) {
		int tmp = data[pos1];
		data[pos1] = data[pos2];
		data[pos2] = tmp;
	}
	
	//Von http://www.pohlig.de/Unterricht/Inf2002/Tag49/31.2.2_QuickSort_die_Implementierung.htm geklauter Code
	//ermittlung des pivot modifiziert, läuft gleichmäßiger (ganz kleines bischen schnleller) als unsere eigene implementierung
	//wird zur Zeit nicht verwendet.
	   static void quickSort2(int[] liste, int untereGrenze, int obereGrenze) {
		    int links = untereGrenze;
		    int rechts = obereGrenze;
		    int pivot = Math.min(liste[rechts], Math.max(liste[0], liste[((untereGrenze + obereGrenze) / 2)]));
		    do {
		      while (liste[links] < pivot) {
		        links++;
		      }
		      while (pivot < liste[rechts]) {
		        rechts--;
		      }
		      if (links <= rechts) {
		        int tmp = liste[links];
		        liste[links] = liste[rechts];
		        liste[rechts] = tmp;
		        links++;
		        rechts--;
		      }
		    } while (links <= rechts);
		    if (untereGrenze < rechts) {
		       quickSort2(liste, untereGrenze, rechts);
		    }
		    if (links < obereGrenze) {
		        quickSort2(liste, links, obereGrenze);
		    }
		  }


}
