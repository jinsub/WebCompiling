package compiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class CodeGenerator {
	private int ifLabelN = 1;
	private int loopLabelN = 1;
	private String ucodeStr = "";
	
	CodeGenerator(VariableMap vMap, Program out){
		CodeStartMain(out.decpart);
		CodeDeclaration(vMap, out.decpart);
		CodeBody(vMap, out.body);
		CodeEndMain();
	}
	public void CodeStartMain(Declarations ds){
		String fileName = "Ucode";
		int size = ds.size();
		String Ssize = String.valueOf(size);
		String BlockN = "2";
		String LexicalLevel = "2";
		/*try{
			BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, true));
			fw.write("main       proc" + "        " + Ssize + "     " + BlockN + "     " + LexicalLevel);
			fw.newLine();
			fw.flush();
			fw.close();
		}catch(Exception e){
			e.printStackTrace();
		}*/
		ucodeStr+="main		proc" + "		" + Ssize + "	" + BlockN + "	" + LexicalLevel+"\n";
	}

	public void CodeEndMain(){
		String fileName = "Ucode";
		/*try{
			BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, true));
			fw.write("           call        main");
			fw.newLine();
			fw.write("           end");
			fw.flush();
			fw.close();
		}catch(Exception e){
			e.printStackTrace();
		}*/
		ucodeStr+="		call		main\n"+"		end";
	}
	
	public void CodeDeclaration(VariableMap m, Declarations ds){
		String fileName = "Ucode";
		/*try{
			String key;
			BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, true));
			for (Declaration d : ds){
			key = d.v.toString();
			fw.write("           sym" + "         " + m.get(key).getSegment() + "     " + m.get(key).getOffSet() + "     " + m.get(key).getWordLength());
			fw.flush();
			fw.newLine();
			}
			fw.close();
		}catch(Exception e){
			e.printStackTrace();
		}*/
		String key;
		for (Declaration d : ds){
			key = d.v.toString();
			ucodeStr+="		sym" + "		" + m.get(key).getSegment() + "	" + m.get(key).getOffSet() + "	" + m.get(key).getWordLength()+"\n";
		}
	}

	public void CodeBody(VariableMap m, Block b){
		for (Statement s : b.members){
			if (s instanceof Skip); 
	        if (s instanceof Assignment) CodeAssignment(m, (Assignment)s);
	        if (s instanceof Conditional) CodeConditional(m, (Conditional)s); 
	        if (s instanceof Loop) CodeLoop(m, (Loop)s);  
	        if (s instanceof Block) CodeBlock(m, (Block)s);
		}
	}
	
	public void CodeStatement(VariableMap m, Statement s){
		if (s instanceof Skip); 
        if (s instanceof Assignment) CodeAssignment(m, (Assignment)s);
        if (s instanceof Conditional) CodeConditional(m, (Conditional)s); 
        if (s instanceof Loop) CodeLoop(m, (Loop)s);  
        if (s instanceof Block) CodeBlock(m, (Block)s); 
	}
	
	public void CodeAssignment(VariableMap m, Assignment a){
		String fileName = "Ucode";
		/*try{
			String key = a.target.toString();
			BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, true));
			CodeExpression(m,a.source);
			fw.write("           str" + "         " +m.get(key).getSegment() + "     " + m.get(key).getOffSet());
			fw.flush();
			fw.newLine();
			fw.close();
		}catch(Exception e){
			e.printStackTrace();
		}*/
		String key = a.target.toString();
		CodeExpression(m,a.source);
		ucodeStr+="		str" + "		" +m.get(key).getSegment() + "	" + m.get(key).getOffSet()+"\n";		
	}
	
	public void CodeConditional(VariableMap m, Conditional c){
		Expression test = c.test;
		Statement thenBranch = c.thenbranch;
		Statement elseBranch = c.elsebranch;
		String labelName = "$" + Integer.toString(ifLabelN) + "ICon";
		ifLabelN++;
		String fileName = "Ucode";
		
		/*try{
			CodeExpression(m, test);
			BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, true));
			fw.write("           fjp" + "         " + labelName);
			fw.flush();
			fw.newLine();
			fw.close();
			CodeStatement(m,thenBranch);
			fw = new BufferedWriter(new FileWriter(fileName, true));
			fw.write(labelName + "     nop");
			fw.flush();
			fw.newLine();
			fw.close();
		}catch(Exception e){
			e.printStackTrace();
		}*/
		CodeExpression(m, test);
		ucodeStr+="		fjp" + "		" + labelName+"\n";
		CodeStatement(m,thenBranch);
		ucodeStr+=labelName + "	nop"+"\n";
		CodeStatement(m,elseBranch);
	}
	
	public void CodeLoop(VariableMap m, Loop l){
		Expression test = l.test;
		Statement body = l.body;
		String labelName = "$"+ Integer.toString(loopLabelN)+ "LCon" ;
		String exitLabelName = "$" + Integer.toString(loopLabelN) + "LEx" ;
		loopLabelN++;
		String fileName = "Ucode";
		
		/*try{
			CodeExpression(m, test);
			BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, true));
			fw.write("           fjp" + "         " + exitLabelName);
			fw.flush();
			fw.newLine();
			fw.close();
			fw = new BufferedWriter(new FileWriter(fileName, true));
			fw.write(labelName + "     nop");
			fw.flush();
			fw.newLine();
			fw.close();
			CodeStatement(m, body);
			CodeExpression(m, test);
			fw = new BufferedWriter(new FileWriter(fileName, true));
			fw.write("           tjp" + "         " + labelName);
			fw.flush();
			fw.newLine();
			fw.write(exitLabelName + "      nop");
			fw.flush();
			fw.newLine();
			fw.close();
		}catch(Exception e){
			e.printStackTrace();
		}*/
		CodeExpression(m, test);
		ucodeStr+="		fjp" + "		" + exitLabelName+"\n";
		ucodeStr+=labelName + "	nop"+"\n";
		CodeStatement(m, body);
		CodeExpression(m, test);
		ucodeStr+="		tjp" + "		" + labelName+"\n";
		ucodeStr+=exitLabelName + "	nop"+"\n";
	}
	
	public void CodeBlock(VariableMap m, Block b){
		for (Statement s : b.members){
			if (s instanceof Skip); 
	        if (s instanceof Assignment) CodeAssignment(m, (Assignment)s);
	        if (s instanceof Conditional) CodeConditional(m, (Conditional)s); 
	        if (s instanceof Loop) CodeLoop(m, (Loop)s);  
	        if (s instanceof Block) CodeBlock(m, (Block)s);
		}
	}
	
	public void CodeExpression(VariableMap m, Expression e){
		if (e instanceof Value) CodeValue(m, (Value)e);
        if (e instanceof Variable) CodeVariable(m, (Variable)e);
        if (e instanceof Binary) CodeBinary(m, (Binary)e);
        if (e instanceof Unary) CodeUnary(m, (Unary)e);
            
	}
	
	public void CodeValue(VariableMap m, Value v){
		//ldc 상수
		if (v instanceof BoolValue) CodeBoolValue(m, (BoolValue)v);
		if (v instanceof IntValue) CodeIntValue(m, (IntValue)v);
		if (v instanceof FloatValue)CodeFloatValue(m, (FloatValue)v);
	//	if (v instanceof CharValue);
	}

	public void CodeVariable(VariableMap m, Variable v){
		//lod blockN offSet
		String fileName = "Ucode";
		/*try{
			String key = v.toString();
			BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, true));
			fw.write("           lod" + "         " +m.get(key).getSegment() + "     " + m.get(key).getOffSet());
			fw.flush();
			fw.newLine();
			fw.close();
		}catch(Exception e){
			e.printStackTrace();
		}*/
		String key = v.toString();
		ucodeStr+="		lod" + "		" +m.get(key).getSegment() + "	" + m.get(key).getOffSet()+"\n";
		
	}

	public void CodeBinary(VariableMap m, Binary b){
		 	
		String strOp = b.op.toString();
		CodeExpression(m, b.term1);
		CodeExpression(m, b.term2);
		
		String fileName = "Ucode";
		/*try{
			BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, true));
			switch(strOp){
				case "&&" :
						fw.write("           and");
						fw.flush();
						fw.newLine();
						fw.close();
					break;
						
				case "||" :
						fw.write("           or");
						fw.flush();
						fw.newLine();
						fw.close();
					break;
					
				case "INT<" :
				case "FLOAT<" :
				case "CHAR<" :
				case "BOOL<" :
						fw.write("           gt");
						fw.flush();
						fw.newLine();
						fw.close();
					break;
					
				case "INT<=" :
				case "FLOAT<=" :
				case "CHAR<=" :
				case "BOOL<=" :
						fw.write("           ge");
						fw.flush();
						fw.newLine();
						fw.close();
					break;
					
				case "INT==" :
				case "FLOAT==" :
				case "CHAR==" :
				case "BOOL==" :
						fw.write("           eq");
						fw.flush();
						fw.newLine();
						fw.close();
					break;
					
				case "INT!=" :
				case "FLOAT!=" :
				case "CHAR!=" :
				case "BOOL!=" :
						fw.write("           ne");
						fw.flush();
						fw.newLine();
						fw.close();
					break;
					
				case "INT>" :
				case "FLOAT>" :
				case "CHAR>" :
				case "BOOL>" :
						fw.write("           lt");
						fw.flush();
						fw.newLine();
						fw.close();
					break;
					
				case "INT>=" :
				case "FLOAT>=" :
				case "CHAR>=" :
				case "BOOL>=" :
						fw.write("           le");
						fw.flush();
						fw.newLine();
						fw.close();
					break;		
					
				case "INT+" :
				case "FLOAT+" :
						fw.write("           add");
						fw.flush();
						fw.newLine();
						fw.close();
					break;
					
				case "INT-" :
				case "FLOAT-" :
						fw.write("           sub");
						fw.flush();
						fw.newLine();
						fw.close();
					break;
					
				case "INT*" :
				case "FLOAT*" :	
						fw.write("           mul");
						fw.flush();
						fw.newLine();
						fw.close();
					break;
					
				case "INT/" :
				case "FLOAT/" :
						fw.write("           div");
						fw.flush();
						fw.newLine();
						fw.close();
					break;
					
				default :	//없는 연산자 에러
					break; 	 
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}*/
		switch(strOp){
		case "&&" :
				ucodeStr+="		and"+"\n";
			break;
				
		case "||" :
				ucodeStr+="		or"+"\n";
			break;
			
		case "INT<" :
		case "FLOAT<" :
		case "CHAR<" :
		case "BOOL<" :
				ucodeStr+="		gt"+"\n";
			break;
			
		case "INT<=" :
		case "FLOAT<=" :
		case "CHAR<=" :
		case "BOOL<=" :
				ucodeStr+="		ge"+"\n";
			break;
	
		case "INT==" :
		case "FLOAT==" :
		case "CHAR==" :
		case "BOOL==" :
				ucodeStr+="		eq"+"\n";
			break;
			
		case "INT!=" :
		case "FLOAT!=" :
		case "CHAR!=" :
		case "BOOL!=" :
			ucodeStr+="		ne"+"\n";
			break;
			
		case "INT>" :
		case "FLOAT>" :
		case "CHAR>" :
		case "BOOL>" :
			ucodeStr+="		lt"+"\n";
			break;
			
		case "INT>=" :
		case "FLOAT>=" :
		case "CHAR>=" :
		case "BOOL>=" :
			ucodeStr+="		le"+"\n";
			break;		
			
		case "INT+" :
		case "FLOAT+" :
			ucodeStr+="		add"+"\n";
			break;
			
		case "INT-" :
		case "FLOAT-" :
			ucodeStr+="		sub"+"\n";
			break;
			
		case "INT*" :
		case "FLOAT*" :	
			ucodeStr+="		mul"+"\n";
			break;
			
		case "INT/" :
		case "FLOAT/" :
			ucodeStr+="		div"+"\n";
			break;
			
		default :	//없는 연산자 에러
			break; 	 
	}
	
}
	
	public void CodeUnary(VariableMap m, Unary u){
		
		String opString = u.op.toString();
		CodeExpression(m, u.term);
		if(opString == "!"){
			String fileName = "Ucode";
			/*try{
				BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, true));
				fw.write("           notop");
				fw.flush();
				fw.newLine();
				fw.close();
			}catch(Exception e){
				e.printStackTrace();
			}*/
			ucodeStr+="		notop"+"\n";
		}
		else if(opString == "-"){
			String fileName = "Ucode";
			/*try{
				BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, true));
				fw.write("           neg");
				fw.flush();
				fw.newLine();
				fw.close();
			}catch(Exception e){
				e.printStackTrace();
			}*/
			ucodeStr+="		neg"+"\n";
		}
		else{//에러
			}
	}
		
	public void CodeBoolValue(VariableMap m, BoolValue b){
		if(b.boolValue()){
			String fileName = "Ucode";
			/*try{
				BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, true));
				fw.write("           ldc" + "         1");
				fw.flush();
				fw.newLine();
				fw.close();
			}catch(Exception e){
				e.printStackTrace();
			}*/
			ucodeStr+="		ldc" + "		1"+"\n";
		}
		else{
			String fileName = "Ucode";
			/*try{
				BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, true));
				fw.write("           ldc" + "         0");
				fw.flush();
				fw.newLine();
				fw.close();
			}catch(Exception e){
				e.printStackTrace();
			}*/
			ucodeStr+="		ldc" + "		0"+"\n";
		}
	}

	public void CodeIntValue(VariableMap m, IntValue i){
		String fileName = "Ucode";
		/*try{
			BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, true));
			fw.write("           ldc" + "         " + i.toString());
			fw.flush();
			fw.newLine();
			fw.close();
		}catch(Exception e){
			e.printStackTrace();
		}*/
		ucodeStr+="		ldc" + "		" + i.toString()+"\n";
	}
	
	public void CodeFloatValue(VariableMap m, FloatValue f){
		String fileName = "Ucode.txt";
		/*try{
			BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, true));
			fw.write("           ldc" + "         " + f.toString());
			fw.flush();
			fw.newLine();
			fw.close();
		}catch(Exception e){
			e.printStackTrace();
		}*/
		ucodeStr+="		ldc" + "		" + f.toString()+"\n";
	}
	public String getCode(){
		return ucodeStr;
	}
	public void flush(){
		ucodeStr="";
	}
	public static void main(String args[]){
		String a = "int main(){"+"\n"+"int a;"+"\n"+"a = 3;"+"\n"+"}";
		Parser parser = new Parser(new Lexer(a));
		Program prog = parser.program();
		TypeMap map = StaticTypeCheck.typing(prog.decpart);
		StaticTypeCheck.V(prog);;
		Program out = TypeTransformer.T(prog, map);
	//	Semantics semantics = new Semantics();
	//	semantics.M(out);
	//	State state = semantics.M(out);
	//	state.display( );
		VtoLMapping m = new VtoLMapping();
		VariableMap vMap = m.typing(out.decpart); 
	//	vMap.display();
		CodeGenerator G = new CodeGenerator(vMap, out);
		
	}
	
}
