package compiler;

import java.util.*;

public class TypeMap extends HashMap<Variable, Type> { 
	public TypeMap( ) { }
    
    public TypeMap(Variable key, Type typ) {
        put(key, typ);
    }
    
    public TypeMap onion(Variable key, Type typ) {
        put(key, typ);
        return this;
    }
    
    public TypeMap onion (TypeMap t) {
        for (Variable key : t.keySet())
            put(key, t.get(key));
        return this;
    }

}
