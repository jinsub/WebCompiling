package compiler;

// StaticTypeCheck.java

import java.util.*;

// Static type checking for Clite is defined by the functions 
// V and the auxiliary functions typing and typeOf.  These
// functions use the classes in the Abstract Syntax of Clite.


public class StaticTypeCheck {

    public static TypeMap typing (Declarations d) {
        TypeMap map = new TypeMap();
        for (Declaration di : d) 
            map.put (di.v, di.t);
        return map;
    }

    public static void V (Program p) {
        V (p.decpart);
        V (p.body, typing (p.decpart));
    } 
    
    public static void V (Declarations d) {
        for (int i=0; i<d.size() - 1; i++)
            for (int j=i+1; j<d.size(); j++) {
                Declaration di = d.get(i);
                Declaration dj = d.get(j);
                if(di.v.equals(dj.v)){
                	System.out.println(dj.v + "변수가 중복 선언 되었습니다.");
                	System.exit(1);
                }
            }
    } 

    public static void V (Statement s, TypeMap tm) {
        if ( s == null )
            throw new IllegalArgumentException( "AST error: null statement");
        else if (s instanceof Skip) return;
        else if (s instanceof Assignment) {
            Assignment a = (Assignment)s;
            if(!(tm.containsKey(a.target))){
            	System.out.println(a.target + "변수가 선언되어 있지 않습니다.");
            	System.exit(1);
            }
            
            V(a.source, tm);
            
            Type ttype = (Type)tm.get(a.target); 
            Type srctype = typeOf(a.source, tm);
            
            if (ttype != srctype){
            	System.out.println("타겟 변수와 소스 변수의 타입이 맞지 않습니다.");
            }
            return;
        } 
        else if (s instanceof Conditional) {
            
        	Conditional c = (Conditional)s;
            
        	V(c.test, tm); 
            
        	Type testtype = typeOf(c.test, tm);
            
        	if (testtype == Type.BOOL) {
                V(c.thenbranch, tm); 
                V(c.elsebranch, tm); 
                return;
            }else {
                System.out.println("조건이 bool형이 아닙니다.");
            }
        }
        else if (s instanceof Loop) {
            Loop l = (Loop)s; 
            V(l.test, tm);
            Type testtype = typeOf(l.test, tm);
            if (testtype == Type.BOOL) {
                V(l.body, tm);
            }else {
            	System.out.println("조건이 bool형이 아닙니다.");
            }
        }
        else if (s instanceof Block) {
            Block b = (Block)s;
            for(Statement i : b.members) {
                V(i, tm);
            } 

        } else {
        throw new IllegalArgumentException("should never reach here");
    }
    }

    public static void V (Expression e, TypeMap tm) {
        if (e instanceof Value) 
            return;
        if (e instanceof Variable) { 
            Variable v = (Variable)e;
            if(!(tm.containsKey(v))){
            	System.out.println("변수 선언이 안되어있습니다.");
            }
            return;
        }
        if (e instanceof Binary) {
            Binary b = (Binary) e;
            
            Type typ1 = typeOf(b.term1, tm);
            Type typ2 = typeOf(b.term2, tm);
            
            V (b.term1, tm);
            V (b.term2, tm);
            
            if (b.op.ArithmeticOp( ))  
                if(typ1 != typ2){
                	System.out.println("타입이 맞지 않습니다.");
                }
            else if (b.op.RelationalOp( )) 
            	if(typ1 != typ2){
            		System.out.println("타입이 맞지 않습니다.");
                }
            else if (b.op.BooleanOp( )) 
                if(!(typ1 == Type.BOOL && typ2 == Type.BOOL)){
                	System.out.println("타입이 맞지 않습니다.");
                }
            else
                throw new IllegalArgumentException("should never reach here BinaryOp error");
            return;
        }
        if (e instanceof Unary) {
            Unary u = (Unary) e;
            Type type = typeOf(u.term, tm); //start here
            V(u.term, tm);
            if (u.op.NotOp()) {
                if(!(type == Type.BOOL)){
                	System.out.println("타입이 맞지 않습니다.");
                }
             } 
            else if (u.op.NegateOp()) {
                if(!( type == (Type.INT) || type == (Type.FLOAT) )){
                	System.out.println("타입이 맞지 않습니다.");
                }
            }
            else {
                throw new IllegalArgumentException("should never reach here UnaryOp error");
            }
            return;
        }

        throw new IllegalArgumentException("should never reach here");
    }

    public static Type typeOf (Expression e, TypeMap tm) {
        if (e instanceof Value) return ((Value)e).type;
        if (e instanceof Variable) {
            Variable v = (Variable)e;
            return (Type)tm.get(v);
        }
        if (e instanceof Binary) {
            Binary b = (Binary)e;
            if (b.op.ArithmeticOp( ))
                if (typeOf(b.term1,tm) == Type.FLOAT)
                    return (Type.FLOAT);
                else return (Type.INT);
            if (b.op.RelationalOp( ) || b.op.BooleanOp( )) 
                return (Type.BOOL);
        }
        if (e instanceof Unary) {
            Unary u = (Unary)e;
            
            if (u.op.NotOp( ))        return (Type.BOOL);
            else if (u.op.NegateOp( )) return typeOf(u.term,tm);
            else if (u.op.intOp( ))    return (Type.INT);
            else if (u.op.floatOp( )) return (Type.FLOAT);
            else if (u.op.charOp( ))  return (Type.CHAR);
        }
        throw new IllegalArgumentException("should never reach here");
    } 

    public static void main(String args[]) {
        Parser parser  = new Parser(new Lexer("test.txt"));
        Program prog = parser.program();
        TypeMap map = typing(prog.decpart);
        V(prog);
        System.out.print("끝났다.");
    }
}

