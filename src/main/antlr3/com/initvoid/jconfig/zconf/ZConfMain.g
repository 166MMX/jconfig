grammar ZConfMain;

options {
    output        =  AST;
    ASTLabelType  =  CommonTree;
    k             =  1;
}

tokens {
    V_OPTION_LIST;
    V_STATEMENT_LIST;
    V_EXPRESSION;
    V_WORD;
    V_HELP_TEXT;
    V_IF_FRAG;
}

@lexer::header  {package com.initvoid.jconfig.zconf;}
@parser::header {package com.initvoid.jconfig.zconf;}

@parser::members {
    protected static String parseJavaString(String value)
    {
        if (value == null) return null;

        value = value.substring(1, value.length() - 1);
        value = org.apache.commons.lang3.StringEscapeUtils.unescapeJava(value);

        return value;
    }
}

// ================================================================================================

input
    :
    (                                    options{ greedy = true; }:
        T_EOL
    )*
        input_sub_stmt_list?
    ;

// ================================================================================================

config_stmt
    :   T_CONFIG^      T_WORD               statement_start_eol!
        config_option_list
    ;

menu_config_stmt
    :   T_MENUCONFIG^  T_WORD               statement_start_eol!
        config_option_list
    ;

choice_stmt
    :   T_CHOICE^      T_WORD?              statement_start_eol!
        choice_option_list?
        choice_sub_stmt_list
        T_ENDCHOICE!                        statement_end_eol!
    ;

comment_stmt
    :   T_COMMENT^     prompt_value         statement_start_eol!
        comment_option_list?
    ;

menu_stmt
    :   T_MENU^        prompt_value         statement_start_eol!
        menu_option_list?
        menu_sub_stmt_list
        T_ENDMENU!                          statement_end_eol!
    ;

if_stmt
    :   T_IF^          expr                 statement_start_eol!
        if_sub_stmt_list
        T_ENDIF!                            statement_end_eol!
    ;

source_stmt
    :   T_SOURCE^      prompt_value         statement_end_eol!
    ;

main_menu_stmt
    :   T_MAINMENU^    prompt_value         statement_end_eol!
    ;

common_stmt
    :   if_stmt
    |   comment_stmt
    |   config_stmt
    |   menu_config_stmt
    |   source_stmt
    ;

// ================================================================================================

config_option_list
    :
    (                                   options{ greedy = true; }:
        item=config_option
    |   item=symbol_option
    |   item=depends
    |   item=help
    |   T_EOL
    )+

    ->  ^(V_OPTION_LIST $item+)
    ;

choice_option_list
    :
    (                                   options{ greedy = true; }:
        item=choice_option
    |   item=depends
    |   item=help
    |   T_EOL
    )+

    ->  ^(V_OPTION_LIST $item+)
    ;

comment_option_list
    :
    (                                   options{ greedy = true; }:
        item=depends
    |   T_EOL
    )+

    ->  ^(V_OPTION_LIST $item+)
    ;

menu_option_list
    :
    (                                   options{ greedy = true; }:
        item=visible
    |   item=depends
    |   T_EOL
    )+

    ->  ^(V_OPTION_LIST $item+)
    ;

// ================================================================================================

input_sub_stmt_list
    :   item=main_menu_stmt?
    (                                    options{ greedy = true; }:
        item=common_stmt
    |   item=choice_stmt
    |   item=menu_stmt
    |   T_EOL
    )+

    ->  ^(V_STATEMENT_LIST $item+)
    ;

choice_sub_stmt_list
    :
    (                                    options{ greedy = true; }:
        item=common_stmt
    |   T_EOL
    )+

    ->  ^(V_STATEMENT_LIST $item+)
    ;

menu_sub_stmt_list
    :
    (                                    options{ greedy = true; }:
        item=common_stmt
    |   item=menu_stmt
    |   item=choice_stmt
    |   T_EOL
    )+

    ->  ^(V_STATEMENT_LIST $item+)
    ;

if_sub_stmt_list
    :
    (                                    options{ greedy = true; }:
        item=common_stmt
    |   item=menu_stmt
    |   item=choice_stmt
    |   T_EOL
    )+

    ->  ^(V_STATEMENT_LIST $item+)
    ;

// ================================================================================================

config_option
    :   type_option
    |   prompt_option
    |   default_config_option
    |   select_option
    |   range_option
    ;

choice_option
    :   type_option
    |   prompt_option
    |   default_choice_option
    |   optional_option
    ;

symbol_option
    :   option_option
    ;

type_option
    :
    (   T_TYPE_BOOL
    |   T_TYPE_BOOLEAN
    |   T_TYPE_TRISTATE
    |   T_TYPE_STRING
    |   T_TYPE_HEX
    |   T_TYPE_INT
    )^  prompt_value?
        option_if_frag?
        option_eol!
    ;

prompt_option
    :   T_PROMPT^
        prompt_value
        option_if_frag?
        option_eol!
    ;

default_config_option
    :
    (   T_DEFAULT
    |   T_DEFAULT_BOOL
    |   T_DEFAULT_TRISTATE
    )^ 	expr
        option_if_frag?
        option_eol!
    ;

default_choice_option
    :
    (   T_DEFAULT
    |   T_DEFAULT_BOOL
    |   T_DEFAULT_TRISTATE
    )^	T_WORD
        option_if_frag?
        option_eol!
    ;

select_option
    :   T_SELECT^
        T_WORD
        option_if_frag?
        option_eol!
    ;

range_option
    :   T_RANGE^
        symbol
        symbol
        option_if_frag?
        option_eol!
    ;

option_option
    :   T_OPTION^
        option_param_list
        option_eol!
    ;

optional_option
    :   T_OPTIONAL^
        option_eol!
    ;

depends
    :   T_DEPENDS_ON^
        expr
        option_eol!
    ;

visible
    :   T_VISIBLE^
        option_if_frag?
        option_eol!
    ;

help
    :   T_HELP^
        help_content
        option_eol!
    ;

help_content
	@init
	    {
	        StringBuilder      helpTextBuilder  =  null;
        }
    :   {
            TokenStream        thisTokenStream  =  getTokenStream();
            Lexer              thisLexer        =  (Lexer) thisTokenStream.getTokenSource();
            CharStream         charStream       =  thisLexer.getCharStream();
            ZConfHelpLexer     tokenSource      =  new ZConfHelpLexer(charStream, state);
            CommonTokenStream  tokenStream      =  new CommonTokenStream(tokenSource);
            ZConfHelpParser    parser           =  new ZConfHelpParser(tokenStream);
                               helpTextBuilder  =  parser.input();
        }

    ->  V_HELP_TEXT[helpTextBuilder.toString()]
    ;

option_if_frag
    :   T_IF
        expr

    -> ^(V_IF_FRAG expr)
    ;

option_param_list
    :
    (                                    options{ greedy = true; }:
        modules_option_param
    |   def_config_list_option_param
    |   env_option_param
    |   all_no_config_y_option_param
    )*
    ;

modules_option_param
    :   T_OPT_MODULES
    ;

def_config_list_option_param
    :   T_OPT_DEFCONFIG_LIST
    ;

env_option_param
    :   T_OPT_ENV T_EQUAL prompt_value
    ;

all_no_config_y_option_param
    :   T_OPT_ALLNOCONFIG_Y
    ;

prompt_value
    :   T_WORD                                  ->  V_WORD[$T_WORD, $T_WORD.text]
    |   T_WORD_QUOTE                            ->  V_WORD[$T_WORD_QUOTE, parseJavaString($T_WORD_QUOTE.text)]
    ;

// ================================================================
// ================================================================

// T_LPAREN > T_NOT > T_EQUAL, T_UNEQUAL > T_AND > T_OR
// T_OR < T_AND < T_EQUAL, T_UNEQUAL < T_NOT < T_LPAREN

expr
    :   or_expr

    ->  ^(V_EXPRESSION or_expr)
    ;

or_expr
    :            and_expr
    (   T_OR     or_expr
    )?
    ;

and_expr
    :            comp_expr
    (   T_AND    and_expr
    )?
    ;

comp_expr
    :              not_expr
    (   T_EQUAL    not_expr
    |   T_UNEQUAL  not_expr
    )?
    ;

not_expr
    :              list_expr
    |   T_NOT      not_expr
    ;

list_expr
    :                 symbol
    |   T_OPEN_PAREN  expr    T_CLOSE_PAREN
    ;

symbol
    :   T_WORD                                  ->  V_WORD[$T_WORD, $T_WORD.text]
    |   T_WORD_QUOTE                            ->  V_WORD[$T_WORD_QUOTE, parseJavaString($T_WORD_QUOTE.text)]
    ;

eol_or_eof
    :   T_EOL
    |   EOF
    ;

statement_start_eol
    :   T_EOL
    ;

statement_end_eol
    :   T_EOL
    |   EOF
    ;

option_eol
    :   T_EOL
    |   EOF
    ;

DASHES
    :   '---'                                   {$channel=HIDDEN;}
    ;

COMMENT
    :   '#' ~('\r'|'\n')*                       {$channel=HIDDEN;}
    ;

WS  :   (' '|'\t')+                             {$channel=HIDDEN;}
    ;

T_FOLD
    :   '\\' T_EOL                              {$channel=HIDDEN;}
    ;

T_EOL
    :   '\r'? '\n'
    ;

T_MAINMENU              : 'mainmenu'        ;
T_MENU                  : 'menu'            ;
T_ENDMENU               : 'endmenu'         ;
T_SOURCE                : 'source'          ;
T_CHOICE                : 'choice'          ;
T_ENDCHOICE             : 'endchoice'       ;
T_COMMENT               : 'comment'         ;
T_CONFIG                : 'config'          ;
T_MENUCONFIG            : 'menuconfig'      ;
T_HELP                  : 'help'
                        | '---help---'      ;
T_IF                    : 'if'              ;
T_ENDIF                 : 'endif'           ;
T_DEPENDS_ON            : 'depends on'      ;
T_OPTIONAL              : 'optional'        ;
T_DEFAULT               : 'default'         ;
T_PROMPT                : 'prompt'          ;
T_TYPE_TRISTATE         : 'tristate'        ;
T_DEFAULT_TRISTATE      : 'def_tristate'    ;
T_TYPE_BOOL             : 'bool'            ;
T_TYPE_BOOLEAN          : 'boolean'         ;
T_DEFAULT_BOOL          : 'def_bool'        ;
T_TYPE_INT              : 'int'             ;
T_TYPE_HEX              : 'hex'             ;
T_TYPE_STRING           : 'string'          ;
T_SELECT                : 'select'          ;
T_RANGE                 : 'range'           ;
T_VISIBLE               : 'visible'         ;
T_OPTION                : 'option'          ;
T_ON                    : 'on'              ;
T_OPT_MODULES           : 'modules'         ;
T_OPT_DEFCONFIG_LIST    : 'defconfig_list'  ;
T_OPT_ENV               : 'env'             ;
T_OPT_ALLNOCONFIG_Y     : 'allnoconfig_y'   ;

T_AND                   : '&&'              ;
T_OR                    : '||'              ;
T_OPEN_PAREN            : '('               ;
T_CLOSE_PAREN           : ')'               ;
T_NOT                   : '!'               ;
T_EQUAL                 : '='               ;
T_UNEQUAL               : '!='              ;

T_DQUOT                 : '"'               ;
T_SQUOT                 : '\''              ;

T_WORD_QUOTE
    :    '"' ( ESC_SEQ | ~('\\'| '"') )*  '"'
    |   '\'' ( ESC_SEQ | ~('\\'|'\'') )* '\''
    ;

T_WORD
    :   ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'-'|'/'|'.')+
    ;

fragment
HEX_DIGIT
    :   ('0'..'9'|'a'..'f'|'A'..'F')
    ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE_ESC
    |   HEX_ESC
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
HEX_ESC
    :   '\\' 'x' HEX_DIGIT HEX_DIGIT
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;
