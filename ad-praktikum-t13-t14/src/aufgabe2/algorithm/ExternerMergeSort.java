package aufgabe2.algorithm;

import aufgabe2.data.DataManagerImpl;
import aufgabe2.interfaces.*;

public class ExternerMergeSort {

	public static void sort(String inputFile, String outputFile) {
		DataManager tapes = new DataManagerImpl(inputFile); // InputFile
															// übergeben,
															// Konstruktor mit
															// angebbarem
															// Dateinamen bitte?

		// Blockweise Sortierung
		DataWrapper data = tapes.readBlock(); // lese von "band" 1;
												// initialisierung
		while (data.getSize() > 0) { // solange das "band" nicht leer ist
			blockSort_quick(data.getData(), 0, data.getSize() - 1);// Sortieren
			tapes.write(data); // zurückschreiben
			data = tapes.readBlock(); // lese wieder von "band" 1
		}

		// Mergen
		while (merge(tapes)) {// Merge für jeden Block aufrufen, bis keine
								// Blöcke mehr kommen
			// der merge tut schon alles, also do nothing
		}
        System.out.println("fertig");
	}

	// Auswählen, welcher sortieralgo für die blöcke verwendet wird:
	// IF blockgröße > 30 then blockSort_quick

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

	private static boolean merge(DataManager ioTapes) {

		InputBuffer linksIn = new InputBuffer(ioTapes,
				InputBuffer.Channels.LEFTCHANNEL);
		InputBuffer rechtsIn = new InputBuffer(ioTapes,
				InputBuffer.Channels.RIGHTCHANNEL);
		OutputBuffer output = new OutputBuffer(ioTapes);

        if((!linksIn.tapes.leftChannelHasNext() && rechtsIn.tapes.rightChannelHasNext())||(linksIn.tapes.leftChannelHasNext() && !rechtsIn.tapes.rightChannelHasNext()))
            return false;

//		if ((!linksIn.hasCurrent()) && (!rechtsIn.hasCurrent()))
//			return false; // Keine weiteren Blöcke, die Sortiert werden könnten

		while (linksIn.hasCurrent() && rechtsIn.hasCurrent()) {

			int linksElem = linksIn.getCurrent();
			int rechtsElem = rechtsIn.getCurrent();

			if (linksElem <= rechtsElem) {
				output.push(linksElem);
				linksIn.moveNext();
			} else {
				output.push(rechtsElem);
				rechtsIn.moveNext();
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
        output.output.setFolgeKomplett(true);
		output.storeInTape();

		return true;

	}

	static void blockSort_quick(int[] data, int links, int rechts) {
		if (rechts - links < 10) {
			blockSort_insertion(data, links, rechts);
		} else {
			int positionPivot = quickSwap(data, links, rechts);
			blockSort_quick(data, links, positionPivot - 1);
			blockSort_quick(data, positionPivot + 1, rechts);
		}
	}

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

	//Von http://www.pohlig.de/Unterricht/Inf2002/Tag49/31.2.2_QuickSort_die_Implementierung.htm geklauter Code
	//ermittlung des pivot modifiziert, läuft gleichmäßiger (ganz kleines bischen schnleller) als unsere eigene implementierung 
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

	private static void swap(int[] data, int pos1, int pos2) {
		int tmp = data[pos1];
		data[pos1] = data[pos2];
		data[pos2] = tmp;
	}

}
