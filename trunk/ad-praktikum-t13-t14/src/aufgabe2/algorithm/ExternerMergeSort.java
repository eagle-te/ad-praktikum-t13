package aufgabe2.algorithm;

import aufgabe2.data.DataManagerImpl;
import aufgabe2.interfaces.*;

public class ExternerMergeSort {

	
	
	public static void sort(String inputFile, String outputFile){
		DataManager tapes = new DataManagerImpl(); //InputFile übergeben, Konstruktor mit angebbarem Dateinamen bitte? 
		
		//Blockweise Sortierung
		DataWrapper data = tapes.readBlock(); //lese von "band" 1; initialisierung
		while (data.getSize() > 0){ //solange das "band" nicht leer ist
			tapes.write(blockSort(data)); // kleine blöcke sortieren
			data = tapes.readBlock(); //lese wieder von "band" 1
		}
		
		/*
		 * Code ohne DataWrapper, mit gewünschter Implementation
		
		//Blockweise Sortierung
		int[] data = tapes.readBlock(); //lese von "band" 1; initialisierung
		while (data.length > 0){ //solange das "band" nicht leer ist
			tapes.write(blockSort(data)); // kleine blöcke sortieren
			data = tapes.readBlock(); //lese wieder von "band" 1
		}
		
		//mergen
		MergeInput mergeData;// = tapes.read();
		while (mergeData.GetInput1().length>0 && mergeData.GetInput2().length>0){ //solange das "band" nicht leer ist
			tapes.write(merge(mergeData)); // kleine blöcke sortieren
			data = tapes.read(); //lese wieder von "band" 1
		}
		*/	
	}
	
	private static DataWrapper blockSort(DataWrapper input){
		/* Das Verfahren zum internen sortieren der speicherblöcke
		 *  benutzt momentan einen Insertionsort zum Sortieren.
		 */
		
		int[] unpacked = input.getData();
		
		
		for(int i = 1  ; i <= unpacked.length; i++ ) {
			int j = i; 
			int itemToSort = unpacked[i]; 
			while(j >0 && unpacked[j-1] > itemToSort) {
				//insert
				unpacked[j] = unpacked[j-1]; 
				j = j-1; 
			}
			unpacked[j] = itemToSort; 
		}
		
		return input;
	}
	
	public static int[] blockSort(int[] input){ // zum testen
		/* Das Verfahren zum internen sortieren der speicherblöcke
		 *  benutzt momentan einen Insertionsort zum Sortieren.
		 *  
		 *  F�r die zu sortierenden Int-Arrays muss eines gelten: 
		 *  arr[0] ist ein Stopper-Element, d.h am besten die kleinste zahl �berhaupt.
		 */
		
		int[] unpacked = input;
		
		
		for(int i = 1 ; i < unpacked.length; i++ ) {
			int j = i; 
			int itemToSort = unpacked[i]; 
			while(j>0 && unpacked[j-1] > itemToSort) { /* ohne stopper: while((unpacked[j-1] > itemToSort) && j > 1) */
				
				//insert
				unpacked[j] = unpacked[j-1]; 
				j = j-1; 
			}
			unpacked[j] = itemToSort; 
		}
		
		return input;
	}
	
	
	  private static MergeInputOutput merge(MergeInputOutput input) {
		  DataWrapper linksIn  = input.GetInput1();
		  DataWrapper rechtsIn = input.GetInput2();
          int[] links = linksIn.getData();
          int[] rechts = rechtsIn.getData();

		  int linksLänge = linksIn.getSize();
		  int rechtsLänge = rechtsIn.getSize();
	      int linksPos=0;
	      int rechtsPos=0;
		  int[] merged = new int[linksLänge + rechtsLänge];
	      
		  while ( linksPos < linksLänge && rechtsPos < rechtsLänge) {
			  int linksElem = links[linksPos];
	          int rechtsElem = rechts[rechtsPos];
	            
	          if (linksElem <= rechtsElem) {
	        	  merged[linksPos + rechtsPos] = linksElem;
	        	  linksPos ++;  
	          } else {
	        	  merged[linksPos + rechtsPos] = rechtsElem;
	        	  rechtsPos ++;
	          }
	      }

		  if (input.isBlockComplete()) { //Nur wenn keine weiteren Daten zum gleichem Block kommen, Output komplett füllen
			  while (linksPos < linksLänge) {
				  merged[linksPos + rechtsPos] = links[linksPos];
		          linksPos ++;
		      }

		      while (rechtsPos < rechtsLänge) {
		    	  merged[linksPos + rechtsPos] =  rechts[rechtsPos];
		          rechtsPos ++;
		      }
		  }

		  int NotMergedCount = (linksLänge + rechtsLänge) - (linksPos + rechtsPos);
		  MergeInputOutput.InputStream NotCompleteMergedStream = ( rechtsPos < rechtsLänge ? MergeInputOutput.InputStream.INPUT2 : MergeInputOutput.InputStream.INPUT1);
	      
		  return input; //wünschenswerte für Erzeugung: MergeOutput.Create(merged, NotMergedCount, NotCompleteMergedStream);
	    }

}