grammar ZConfHelp;

options {
    language=Java;
    k=3;
}

@lexer::header  { package com.initvoid.jconfig.zconf; }
@parser::header { package com.initvoid.jconfig.zconf; }

input
    returns                                     [ StringBuilder sb ]
                                          @init {
                                                    int firstHelpLineIndentLength = -1;
                                                    int marker = -1;
                                                    sb = new StringBuilder();
                                                }
                                         @after {
                                                    if (marker > 0) ((Lexer) input.getTokenSource()).getCharStream().rewind(marker);
                                                }
    :   (                                       {
                                                    text = null;
                                                }
            (   ws=LWS
                text=TEXT?                      {
                                                    if (text != null)
                                                    {
                                                        int currentHelpLineIndentLength = $ws.text.length();
                                                        if (currentHelpLineIndentLength < firstHelpLineIndentLength)
                                                            break loop3;
                                                        else
                                                        {
                                                            sb.append($ws.text).append($text.text);
                                                            if (firstHelpLineIndentLength == -1)
                                                                firstHelpLineIndentLength = currentHelpLineIndentLength;
                                                        }
                                                    }
                                                }
            )?
            EOL                                 {
                                                    sb.append("\n");
                                                    marker = ((Lexer) input.getTokenSource()).getCharStream().mark();
                                                }
        )*
        (   last_ws=LWS
            last_text=TEXT?                     {
                                                    marker = -1;
                                                    if (last_text != null)
                                                    {
                                                        int currentHelpLineIndentLength = $last_ws.text.length();
                                                        if (currentHelpLineIndentLength < firstHelpLineIndentLength)
                                                            break;
                                                        else
                                                        {
                                                            sb.append($last_ws.text).append($last_text.text);
                                                            if (firstHelpLineIndentLength == -1)
                                                                firstHelpLineIndentLength = currentHelpLineIndentLength;
                                                        }
                                                    }
                                                }
            EOF
        )?
    ;

LWS
                                          @init {
                                                    int spaces = 0;
                                                    StringBuilder sb = new StringBuilder();
                                                }
                                         @after {
                                                    while (spaces > 0)
                                                    {
                                                        if (spaces > 8)
                                                        {   sb.append("        "); spaces -= 8; }
                                                        else
                                                        {   sb.append(" "); spaces--; }
                                                    }
                                                    emit(new ClassicToken(LWS, sb.toString()));
                                                }
    :                                           { getCharPositionInLine() == 0 }?=>
        (                               options { greedy = true; }:
            ' '                                 { spaces++; }
        |   '\t'                                { spaces += 8; spaces -= (spaces \% 8); }
        )+
    ;

TEXT
    :   (                               options { greedy = false; }:
            ~( '\r\n' | '\n' | ' ' | '\t'  )
        )
        (                               options { greedy = true; }:
            ~( '\r\n' | '\n' )
        )*
    ;

EOL
    :   ( '\r\n' | '\n' )
    ;
