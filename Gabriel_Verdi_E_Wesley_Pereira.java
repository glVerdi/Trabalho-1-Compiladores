import java.io.*;

public class Gabriel_Verdi_E_Wesley_Pereira {

  private static final int BASE_TOKEN_NUM = 301;
  
  public static final int IDENT  = 301;
  public static final int NUM 	 = 302;
  public static final int WHILE  = 303;
  public static final int IF	 = 304;
  public static final int FI	 = 305;
  public static final int ELSE = 306;
  public static final int INT = 307;
  public static final int DOUBLE = 308;
  public static final int BOOLEAN = 309;
  public static final int FUNC = 310;
  public static final int VOID = 311;

    public static final String tokenList[] = 
      {"IDENT",
		 "NUM", 
		 "WHILE", 
		 "IF", 
		 "FI",
		 "ELSE",
       "INT",
       "DOUBLE",
       "BOOLEAN",
       "FUNC",
       "VOID" };
                                      
  /* referencia ao objeto Scanner gerado pelo JFLEX */
  private Yylex lexer;

  public ParserVal yylval;

  private static int laToken;
  private boolean debug;

  
  /* construtor da classe */
  public Gabriel_Verdi_E_Wesley_Pereira (Reader r) {
      lexer = new Yylex (r, this);
  }

   /* Gramática do Trabalho
    * Prog --> ListaDec1
    * 
    * ListaDec1 --> DeclVar ListaDec1
    *             | DeclFunc ListaDec1
    *             |
    * 
    * DeclVar --> Tipo ListaIdent ';' DeclVar
    *           |
    * 
    * Tipo --> int | double | boolean
    * 
    * ListaIdent --> IDENT , ListaIdent        Tirando a ambiguidade
    *              | IDENT                     ListaIdent --> IDENT ListaIdentResto
    *                                          ListaIdentResto --> , ListaIdent 
    *                                                            | vazio
    *
    * DeclFun --> FUNC tipoOuVoid IDENT '(' FormalPar ')' '{' DeclVar ListaCmd '}' DeclFun
    *           |
    * 
    * TipoOuVoid --> Tipo | VOID
    * 
    * FormalPar -> paramList |
    * 
    * paramList --> Tipo IDENT , ParamList     Tirandoo a ambiguidade
    *             | Tipo IDENT                 paramlist --> Tipo IDENT paramListResto
    *                                          paramListResto --> , paramList 
                                                              | vazio
    * Bloco --> { ListaCmd }
    * 
    * ListaCmd --> Cmd ListaCmd
    *            |
    * 
    * Cmd --> Bloco
    *       | while ( E ) Cmd
    *       | IDENT = E ;
    *       | if ( E ) Cmd RestoIf
    * 
    * RestoIf -> else Cmd
    *          |
    *
    * E --> E + T             Fatoração a esquerda
    *     | E - T             E --> T E'
    *     | T                 E' --> + T E' | - T E' |  (*vazio*)
    * 
    * T --> T * F             T --> F T'
    *     | T / F             T' --> * F T' | / F T' |  (*vazio*)
    *     | F
    * 
    * F --> IDENT
    *     | NUM
    *     | ( E )
    * 
    */

    private void Prog() {
      if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN || IDENT == '{') {
          if (debug) System.out.println("Prog --> ListaDec1");
          ListaDec1(); 
          Bloco(); 
      }
      else {
          yyerror("Esperado tipo (int, double, boolean) ou {");
      }
  }

   private void ListaDec1() {
      if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN || laToken == '{') {
          DeclVar();
          ListaDec1();
      } else if (laToken == IDENT) {
          DeclFunc();
          ListaDec1();
      }
  }

     private void DeclVar() {
      Tipo();
      ListaIdent();
      verifica(';');
   }

     private void Tipo() {
      if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN) {
         verifica(laToken);
      } else {
         yyerror("Esperado int, double ou boolean");
      }
   }

     private void ListaIdent() {
      verifica(IDENT);  
      ListaIdentResto();  
   }
  
    private void ListaIdentResto() {
      if (laToken == ',') {
          verifica(',');  
          ListaIdent();   
      }
   }

     private void DeclFunc() {
      if (laToken == FUNC) {
         verifica(FUNC);
         tipoOuVoid();
         verifica(IDENT);
         verifica('(');
         FormalPar();
         verifica(')');
         verifica('{');
         ListaDec1();
         ListaCmd();
         verifica('}');
         DeclFunc();
     }
   }

   private void tipoOuVoid() {
      if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN) {
         Tipo();
      } else if (laToken == VOID) {
         verifica(VOID);
      } else {
         yyerror("Esperado int, double, boolean ou void");
      }
   }

   private void FormalPar() {
      if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN) {
         Tipo();
         verifica(IDENT);
         if (laToken == ',') {
            verifica(',');
            FormalPar();
         }
      } else if (laToken == ')') {
      } else {
         yyerror("Esperado int, double, boolean ou )");
      }
   }

   private void paramList() {
      if (laToken == INT || laToken == DOUBLE || laToken == BOOLEAN) {
          Tipo(); 
          verifica(IDENT);  
          paramListResto();  
      }
  }
  
  private void paramListResto() {
      if (laToken == ',') {
          verifica(',');  
          paramList();   
      }
  }
  
  private void Bloco() {
      if (debug) System.out.println("Bloco --> { ListaCmd }");
      //if (laToken == '{') {
         verifica('{');
         ListaCmd();
         verifica('}');
      //}
  }

  private void ListaCmd() {
   if (laToken == IDENT || laToken == WHILE || laToken == IF || laToken == '{') {
       Cmd(); 
       ListaCmd(); 
   }
}

  private void Cmd() {
      if (laToken == '{') {
         if (debug) System.out.println("Cmd --> Bloco");
         Bloco();
	   }    
      else if (laToken == WHILE) {
         if (debug) System.out.println("Cmd --> WHILE ( E ) Cmd");
         verifica(WHILE);    // laToken = this.yylex(); 
  		   verifica('(');
  		   E();
         verifica(')');
         Cmd();
	   }
      else if (laToken == IDENT ) {
         if (debug) System.out.println("Cmd --> IDENT = E ;");
            verifica(IDENT);  
            verifica('='); 
            E();
		      verifica(';');
	   }
    else if (laToken == IF) {
         if (debug) System.out.println("Cmd --> if (E) Cmd RestoIF");
         verifica(IF);
         verifica('(');
  		   E();
         verifica(')');
         Cmd();
         RestoIF();
	   }
 	else yyerror("Esperado {, if, while ou identificador");
   }


   private void RestoIF() {
       if (laToken == ELSE) {
         if (debug) System.out.println("RestoIF --> else Cmd FI ");
         verifica(ELSE);
         Cmd();
         
    
	   } else {
         if (debug) System.out.println("RestoIF -->  (*vazio*)  ");
         // aceitar como vazio  <-- my way
         // ou testar o follow de RestoIF
         }
     }     

   private void E() {
         if (laToken == IDENT || laToken == NUM || laToken == '(') {
          if (debug) System.out.println("E --> T E'");
         T();
         Elinha();
         }
         else yyerror("Esperado operando (, identificador ou numero");
      }

   private void Elinha() {
      if (laToken == '+') {
         if (debug) System.out.println("E' --> + T E'");
         verifica('+');
         T();
         Elinha();
      }
      else if (laToken == '-') {
         if (debug) System.out.println("E' --> - T E'");
         verifica('-');
         T();
         Elinha();
      }
      else {
         if (debug) System.out.println("E' -->  (*vazio*)  ");
         }
   }

   private void T() {
      if (laToken == IDENT || laToken == NUM || laToken == '(') {
       if (debug) System.out.println("T --> F T'");
      F();
      Tlinha();
      }
      else yyerror("Esperado operando (, identificador ou numero");
   }

private void Tlinha() {
   if (laToken == '*') {
      if (debug) System.out.println("T' --> * F T'");
      verifica('*');
      F();
      Tlinha();
   }
   else if (laToken == '/') {
      if (debug) System.out.println("T' --> / F T'");
      verifica('/');
      F();
      Tlinha();
   }
   else {
      if (debug) System.out.println("E' -->  (*vazio*)  ");
      }
}

  private void F() {
      if (laToken == IDENT) {
         if (debug) System.out.println("F --> IDENT");
         verifica(IDENT);
	   }
      else if (laToken == NUM) {
         if (debug) System.out.println("F --> NUM");
         verifica(NUM);
	   }
      else if (laToken == '(') {
         if (debug) System.out.println("F --> ( E )");
         verifica('(');
         E();        
		 verifica(')');
	   }
 	else yyerror("Esperado operando (, identificador ou numero");
   }


  private void verifica(int expected) {
      if (laToken == expected)
         laToken = this.yylex();
      else {
         String expStr, laStr;       

		expStr = ((expected < BASE_TOKEN_NUM )
                ? ""+(char)expected
			     : tokenList[expected-BASE_TOKEN_NUM]);
         
		laStr = ((laToken < BASE_TOKEN_NUM )
                ? Character.toString(laToken)
                : tokenList[laToken-BASE_TOKEN_NUM]);

          yyerror( "esperado token: " + expStr +
                   " na entrada: " + laStr);
     }
   }

   /* metodo de acesso ao Scanner gerado pelo JFLEX */
   private int yylex() {
       int retVal = -1;
       try {
           yylval = new ParserVal(0); //zera o valor do token
           retVal = lexer.yylex(); //le a entrada do arquivo e retorna um token
       } catch (IOException e) {
           System.err.println("IO Error:" + e);
          }
       return retVal; //retorna o token para o Parser 
   }

  /* metodo de manipulacao de erros de sintaxe */
  public void yyerror (String error) {
     System.err.println("Erro: " + error);
     System.err.println("Entrada rejeitada");
     System.out.println("\n\nFalhou!!!");
     System.exit(1);
     
  }

  public void setDebug(boolean trace) {
      debug = true;
  }


  /**
   * Runs the scanner on input files.
   *
   * This main method is the debugging routine for the scanner.
   * It prints debugging information about each returned token to
   * System.out until the end of file is reached, or an error occured.
   *
   * @param args   the command line, contains the filenames to run
   *               the scanner on.
   */
  public static void main(String[] args) {
     Gabriel_Verdi_E_Wesley_Pereira parser = null;
     try {
         if (args.length == 0)
            parser = new Gabriel_Verdi_E_Wesley_Pereira(new InputStreamReader(System.in));
         else 
            parser = new  Gabriel_Verdi_E_Wesley_Pereira( new java.io.FileReader(args[0]));

          parser.setDebug(false);
          laToken = parser.yylex();          

          parser.Prog();
     
          if (laToken== Yylex.YYEOF)
             System.out.println("\n\nSucesso!");
          else     
             System.out.println("\n\nFalhou - esperado EOF.");               

        }
        catch (java.io.FileNotFoundException e) {
          System.out.println("File not found : \""+args[0]+"\"");
        }
//        catch (java.io.IOException e) {
//          System.out.println("IO error scanning file \""+args[0]+"\"");
//          System.out.println(e);
//        }
//        catch (Exception e) {
//          System.out.println("Unexpected exception:");
//          e.printStackTrace();
//      }
    
  }
  
}


