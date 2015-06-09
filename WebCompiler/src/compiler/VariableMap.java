package compiler;

import java.util.*;

public class VariableMap extends HashMap<String, Location> {
	public VariableMap() { }
	
	public VariableMap(String key, Location a){
		put(key, a);
	}
	
	public void mput(String key, Location loc){
		put(key, loc);
	}
	
	public VariableMap onion(String key, Location loc){
		put(key, loc);
		return this;
	}
	
	public VariableMap onion(VariableMap t){
		for (String key : t.keySet())
			put(key, t.get(key));
			return this;
	}
	
	public void display( ) {
	        System.out.print("[ ");
	        String sep = "";
	        for (String key : keySet()) {
	            System.out.print(sep + "<" + key + ", " + get(key).getOffSet() + ">");
	            sep = ", ";
	        }
	        System.out.println(" ]");
	    }
}
