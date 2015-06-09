package compiler;

import java.util.*;

public class VtoLMapping {
	
	private int offSet = 0;
	private int wordLength = 1;
	private int index = 0;
	private int size;
	public  VariableMap typing (Declarations d) {
			
			size = d.size();
			Location[] l = new Location[size];
			VariableMap map = new VariableMap();
			for (Declaration di : d){ 
				offSet = offSet + wordLength;
				l[index] = new Location(offSet, wordLength);
				map.put(di.v.toString(), l[index]);
				index++;
			}
	        return map;
	    }
}