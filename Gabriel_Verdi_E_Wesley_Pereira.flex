%%

%{
  private Gabriel_Verdi_E_Wesley_Pereira yyparser;

  public Yylex(java.io.Reader r, Gabriel_Verdi_E_Wesley_Pereira yyparser) {
    this(r);
    this.yyparser = yyparser;
  }


%} 

%integer
%line
%char

WHITE_SPACE_CHAR=[\n\r\ \t\b\012]

%%

"$TRACE_ON"   { yyparser.setDebug(true); }
"$TRACE_OFF"  { yyparser.setDebug(false); }

"while"	 	{ return Gabriel_Verdi_E_Wesley_Pereira.WHILE; }
"if"		{ return Gabriel_Verdi_E_Wesley_Pereira.IF; }
"else"		{ return Gabriel_Verdi_E_Wesley_Pereira.ELSE; }
"fi"		{ return Gabriel_Verdi_E_Wesley_Pereira.FI; }

[:jletter:][:jletterdigit:]* { return Gabriel_Verdi_E_Wesley_Pereira.IDENT; }  

[0-9]+ 	{ return Gabriel_Verdi_E_Wesley_Pereira.NUM; }

"{" |
"}" |
";" |
"(" |
")" |
"+" |
"-" |
"*" |
"/" |
"="    	{ return yytext().charAt(0); } 


{WHITE_SPACE_CHAR}+ { }

. { System.out.println("Erro lexico: caracter invalido: <" + yytext() + ">"); }
