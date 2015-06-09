package compiler;
import java.util.*;

public class Parser {
    Token token;         
    Lexer lexer;
  
    public Parser(Lexer ts) { 
        lexer = ts;                          
        token = lexer.next();            
    }
  
    private String match (TokenType t) {
        String value = token.value();     
        if (token.type().equals(t))
            token = lexer.next();
        else{
            System.out.println(lexer.getLineNum()+"번째 줄 Syntax error." );
            System.out.println("예상 토큰 : " + t);
            System.out.println("들어온 토큰 : " + token.type());
            System.exit(1);
        }
            return value;
    }
  
    public Program program() {
        TokenType[ ] header = {TokenType.Int, TokenType.Main,TokenType.LeftParen, TokenType.RightParen};
        for (int i=0; i<header.length; i++)
        	match(header[i]);
        match(TokenType.LeftBrace);
        Declarations decs = declarations();
        Block b = progstatements();
        match(TokenType.RightBrace);
	
        return new Program(decs, b); 
    }
  
    private Declarations declarations() {
    	Declarations ds = new Declarations(); 
	
    	while (isType()){
		declaration(ds);
    	}
        return ds;
    }
  
    private void declaration (Declarations ds) {
    	
	Variable v;
	Declaration d;
	String i;
	int arrayN1;
	int arrayN2;
	String Sindex;
	int index;
	String identifier;
	
	Type t = type();
	
	i = match(TokenType.Identifier);
	if(i.indexOf('[') >=0 ){
		arrayN1 = i.indexOf('[');
		arrayN2 = i.indexOf(']');
		Sindex = i.substring(arrayN1 + 1, arrayN2);
	    index = Integer.parseInt(Sindex);
	    for(int z = 0; z < index; z++){
	    	identifier = i.substring(0,arrayN1) + "[" + z +"]";
	    	v = new Variable(identifier);
	    	d = new Declaration(v, t);
	    	ds.add(d);
	    }
	}
	else{
		v = new Variable(i);
		d = new Declaration(v, t);
		ds.add(d);
	}
		while (isComma()) {	
			token = lexer.next();	
			v = new Variable(match(TokenType.Identifier));
			d = new Declaration(v, t);
			ds.add(d);
		}
	match(TokenType.Semicolon);
	}
  
    private Type type () {

	Type t = null;
	if (token.type().equals(TokenType.Int)) {
            t = Type.INT;		
	} else if (token.type().equals(TokenType.Bool)) {
			t = Type.BOOL;
	} else if (token.type().equals(TokenType.Float)) {
			t = Type.FLOAT;
	} else if (token.type().equals(TokenType.Char)) {
			t = Type.CHAR;
	} else {
		 System.out.println(lexer.getLineNum()+"번째 줄 Syntax error." );
         System.out.println("정의되지 않은 타입입니다.");
         System.exit(1);
	}
	
	token = lexer.next();
	return t;          
    }
  
    private Statement statement() {

	Statement s = null;

	if (token.type().equals(TokenType.Semicolon))
		s = new Skip();
	else if (token.type().equals(TokenType.LeftBrace))
		s = statements();
	else if (token.type().equals(TokenType.If))
		s = ifStatement();
	else if (token.type().equals(TokenType.Loop))
		s = LoopStatement();
	else if (token.type().equals(TokenType.Identifier)) 
		s = assignment();
	else { 
		System.out.println(lexer.getLineNum()+"번째 줄 Syntax error." );
		System.out.println("정의되지 않은 statement가 아닙니다.");
		System.exit(1);
	}
		return s;
    }
  
    private Block statements( ) {
        
	Statement s;
	Block b = new Block();
	
	match(TokenType.LeftBrace);
	
	while (isStatement()) {
		s = statement();
		b.members.add(s);
	}
        match(TokenType.RightBrace);
        return b;
    }
    
	private Block progstatements( ) {
 
	Block b = new Block();
	Statement s;
	while (isStatement()) {
		s = statement();
		b.members.add(s);
	} 
        return b;
    }
  
    private Assignment assignment( ) {
	
    Expression source; 
	Variable target; 
	
	target = new Variable(match(TokenType.Identifier));
	match(TokenType.Assign);
	source = expression();
	match(TokenType.Semicolon);
        return new Assignment(target, source); 
    }
  
    private Conditional ifStatement() {
	Conditional con;
	Statement s;
	Expression test;
	
	match(TokenType.If);
	match(TokenType.LeftParen);
	test = expression();
	match(TokenType.RightParen);
	s = statement();
	if (token.type().equals(TokenType.Else)) {
		Statement elsestate = statement();
		con = new Conditional(test, s, elsestate);
	}
	else {
		con = new Conditional(test, s);
	}
	return con;
    }
  
    private Loop LoopStatement() {
       
  	Statement body;
	Expression test;

	match(TokenType.Loop);
	match(TokenType.LeftParen);
	test = expression();
	match(TokenType.RightParen);
	body = statement();
	return new Loop(test, body);
	
    }

    private Expression expression() {
   
	Expression c = conjunction();
	while (token.type().equals(TokenType.Or)) {
		Operator op = new Operator(match(token.type()));
		Expression e = expression();
		c = new Binary(op, c, e);
	}
	return c;
    }
  
    private Expression conjunction() {

	Expression eq = equality();
	while (token.type().equals(TokenType.And)) {
		Operator op = new Operator(match(token.type()));
		Expression c = conjunction();
		eq = new Binary(op, eq, c);
	}
        return eq;  
    }
  
    private Expression equality() {
	Expression rel = relation();
	while (isEqualityOp()) {
		Operator op = new Operator(match(token.type()));
		Expression rel2 = relation();
		rel = new Binary(op, rel, rel2);
	}
        return rel; 
    }

    private Expression relation() {
	Expression a = addition();
	while (isRelationalOp()) {
		Operator op = new Operator(match(token.type()));
		Expression a2 = addition();
		a = new Binary(op, a, a2);
	}
        return a;
    }
  
    private Expression addition() {
        Expression e = term();
        while (isAddOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = term();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression term() {
        Expression e = factor();
        while (isMultiplyOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = factor();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression factor() {
        if (isUnaryOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term = primary();
             return new Unary(op, term);
        }
        else return primary();
    }
  
    private Expression primary () {
        Expression e = null;
        if (token.type().equals(TokenType.Identifier)) {
            e = new Variable(match(TokenType.Identifier));
        } else if (isLiteral()) {
            e = literal();
        } else if (token.type().equals(TokenType.LeftParen)) {
            token = lexer.next();
            e = expression();       
            match(TokenType.RightParen);
        } else if (isType( )) {
            Operator op = new Operator(match(token.type()));
            match(TokenType.LeftParen);
            Expression term = expression();
            match(TokenType.RightParen);
            e = new Unary(op, term);
        } else {
        	System.out.println(lexer.getLineNum()+"번째 줄 Syntax error." );
        	System.out.println("정의된 토큰이 아닙니다.");
        	System.exit(1);
        }
        
        return e;
    }

    private Value literal( ) { 
	Value value = null;
	String stval = token.value();
	
	if (token.type().equals(TokenType.IntLiteral)) {
		value = new IntValue (Integer.parseInt(stval));
		token = lexer.next();
	}
	else if (token.type().equals(TokenType.FloatLiteral))  {
		value = new FloatValue(Float.parseFloat(stval));
		token = lexer.next();
	}
	else if (token.type().equals(TokenType.CharLiteral))  {
		value = new CharValue(stval.charAt(0));
		token = lexer.next();
	}
    else if (token.type().equals(TokenType.True))  {
        value = new BoolValue(true);
        token = lexer.next();
    }
    else if (token.type().equals(TokenType.False))  {
        value = new BoolValue(false);
        token = lexer.next();
    }
    else {//에러 메시지 출력
    }	 
     
	return value;
    }
  
    private boolean isBooleanOp() {
	return token.type().equals(TokenType.And) || 
	    token.type().equals(TokenType.Or);
    } 

    private boolean isAddOp( ) {
        return token.type().equals(TokenType.Plus) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isMultiplyOp( ) {
        return token.type().equals(TokenType.Multiply) ||
               token.type().equals(TokenType.Divide);
    }
    
    private boolean isUnaryOp( ) {
        return token.type().equals(TokenType.Not) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isEqualityOp( ) {
        return token.type().equals(TokenType.Equals) ||
            token.type().equals(TokenType.NotEqual);
    }
    
    private boolean isRelationalOp( ) {
        return token.type().equals(TokenType.Less) ||
               token.type().equals(TokenType.LessEqual) || 
               token.type().equals(TokenType.Greater) ||
               token.type().equals(TokenType.GreaterEqual);
    }
    
    private boolean isType( ) {
        return token.type().equals(TokenType.Int)
            || token.type().equals(TokenType.Bool) 
            || token.type().equals(TokenType.Float)
            || token.type().equals(TokenType.Char);
    }
    
    private boolean isLiteral( ) {
        return token.type().equals(TokenType.IntLiteral) ||
            isBooleanLiteral() ||
            token.type().equals(TokenType.FloatLiteral) ||
            token.type().equals(TokenType.CharLiteral);
    }
    
    private boolean isBooleanLiteral( ) {
        return token.type().equals(TokenType.True) ||
            token.type().equals(TokenType.False);
    }

    private boolean isComma( ) {
	return token.type().equals(TokenType.Comma);
    }
   
    private boolean isSemicolon( ) {
	return token.type().equals(TokenType.Semicolon);
    }

    private boolean isLeftBrace() {
	return token.type().equals(TokenType.LeftBrace);
    } 
 
    private boolean isRightBrace() {
	return token.type().equals(TokenType.RightBrace);
		
    } 

    private boolean isStatement() {
	return 	isSemicolon() ||
		isLeftBrace() ||
		token.type().equals(TokenType.If) ||
		token.type().equals(TokenType.Loop) ||
		token.type().equals(TokenType.Identifier); 
    }
    public static void main(String args[]) {
    	String a = "int main(){"+"\n"+"int a;"+"\n"+"a = 3;"+"\n"+"}";
      	Parser parser  = new Parser(new Lexer(a));
      	Program prog = parser.program();
      	System.out.print("끝났다");
    } 
}
