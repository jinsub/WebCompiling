package compiler;

import compiler.*;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class compile
 */
@WebServlet("/compile")
public class compile extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public compile() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String exampleString = request.getParameter("id");
		try{
			if (exampleString!=null){
					exampleString=convertToC(exampleString);
					System.out.println(exampleString);
					exampleString=ucode(exampleString);
					out.println(exampleString);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	private String convertToC(String tokStr){
		int boxDepth = 0;
		int[] boxOpened = {0,0,0,0,0,0};
		String[] split_toks= tokStr.split(",");
		int depth = 0;
		String retStr="int main(){\n";
		for(int i=1;i<split_toks.length-1;i++){
			depth=(Integer.parseInt(split_toks[i])/20)+1;
			if(depth<=boxDepth){
				for(int k=boxDepth;k>=depth;k--){
					if(boxOpened[k-1]==1){
						for(int l=0;l<k;l++) retStr+="    ";
						retStr+="}\n";
						boxOpened[k-1]=0;
					}
				}
			}
			for(int j=0;j<depth;j++) retStr+="    ";
			switch(split_toks[++i]){
			case "dec":
				retStr+=split_toks[++i]+" "+split_toks[++i];
				if(!split_toks[++i].isEmpty()){
					retStr+="["+split_toks[i]+"];";
				}
				else retStr+=";";
				retStr+="\n";
				break;
			case "opr":
				retStr+=split_toks[++i]+" = "+split_toks[++i];
				switch(split_toks[++i]){
				case "add":
					retStr += " + ";
					break;
				case "sub":
					retStr += " - ";
					break;
				case "mul":
					retStr += " * ";
					break;
				case "div":
					retStr += " / ";
				default:
					System.out.println("operation selection error");		
				}
				retStr+=split_toks[++i]+";\n";
				break;
			case "ass":
				retStr+=split_toks[++i]+" = "+split_toks[++i]+";\n";
				break;
			case "loop":
				retStr+="loop( "+split_toks[++i]+" ){\n";
				boxOpened[depth-1]=1;
				if(boxDepth<depth) boxDepth=depth; 
				break;
			case "if":
				retStr+="if( "+split_toks[++i]+" ){\n";
				boxOpened[depth-1]=1;
				if(boxDepth<depth) boxDepth=depth;
				break;
			case "input":
				retStr+="input(\"%";
				switch(split_toks[++i]){
				case "int":
					retStr+="d";
					break;
				case "float":
					retStr+="f";
					break;
				case "char":
					retStr+="c";
					break;
				}
				retStr+= "\","+ split_toks[++i] +");\n";
				break;
			case "output":
				retStr+="output( "+ split_toks[++i] +" );\n";
				break;
			default:
				System.out.println("default token: "+split_toks[i]);
			}
		}
		for(int k=boxDepth;k>=1;k--){
			if(boxOpened[k-1]==1){
				for(int l=0;l<k;l++) retStr+="    ";
				retStr+="}\n";
				boxOpened[k-1]=0;
			}
		}
		retStr+="}";
		return retStr;
	}
	private String ucode(String str){
		Parser parser = new Parser(new Lexer(str));
		Program prog = parser.program();
		TypeMap map = StaticTypeCheck.typing(prog.decpart);
		StaticTypeCheck.V(prog);
		Program out = TypeTransformer.T(prog, map);
	//	Semantics semantics = new Semantics();
	//	semantics.M(out);
	//	State state = semantics.M(out);
	//	state.display( );
		VtoLMapping m = new VtoLMapping();
		VariableMap vMap = m.typing(out.decpart); 
	//	vMap.display();
		CodeGenerator G = new CodeGenerator(vMap, out);
		String retStr = G.getCode();
		G.flush();
		return retStr;
	}
}
