grammar ZConfLang;

options {
    language=Java;
}

tokens {
    T_MAINMENU            = 'mainmenu'        ;
    T_MENU                = 'menu'            ;
    T_ENDMENU             = 'endmenu'         ;
    T_SOURCE              = 'source'          ;
    T_CHOICE              = 'choice'          ;
    T_ENDCHOICE           = 'endchoice'       ;
    T_COMMENT             = 'comment'         ;
    T_CONFIG              = 'config'          ;
    T_MENUCONFIG          = 'menuconfig'      ;
    T_HELP                = 'help'            ;
    T_IF                  = 'if'              ;
    T_ENDIF               = 'endif'           ;
    T_DEPENDS             = 'depends'         ;
    T_OPTIONAL            = 'optional'        ;
    T_DEFAULT             = 'default'         ;
    T_PROMPT              = 'prompt'          ;
    T_TYPE_TRISTATE       = 'tristate'        ;
    T_DEFAULT_TRISTATE    = 'def_tristate'    ;
    T_TYPE_BOOL           = 'bool'            ;
    T_TYPE_BOOLEAN        = 'boolean'         ;
    T_DEFAULT_BOOL        = 'def_bool'        ;
    T_TYPE_INT            = 'int'             ;
    T_TYPE_HEX            = 'hex'             ;
    T_TYPE_STRING         = 'string'          ;
    T_SELECT              = 'select'          ;
    T_RANGE               = 'range'           ;
    T_VISIBLE             = 'visible'         ;
    T_OPTION              = 'option'          ;
    T_ON                  = 'on'              ;
    T_OPT_MODULES         = 'modules'         ;
    T_OPT_DEFCONFIG_LIST  = 'defconfig_list'  ;
    T_OPT_ENV             = 'env'             ;
    T_OPT_ALLNOCONFIG_Y   = 'allnoconfig_y'   ;

    T_AND          = '&&'  ;
    T_OR           = '||'  ;
    T_OPEN_PAREN   = '('   ;
    T_CLOSE_PAREN  = ')'   ;
    T_NOT          = '!'   ;
    T_EQUAL        = '='   ;
    T_UNEQUAL      = '!='  ;

    T_DQUOT  = '"'   ;
    T_SQUOT  = '\''  ;
}


input
    :
    (                                    options{ greedy = true; }:
        T_EOL
    )*
        sub_stmt_list=input_sub_stmt_list
    ;

input_sub_stmt_list
    :
        main_menu_stmt?
    (   common_stmt
    |   choice_stmt
    |   menu_stmt
    |   T_EOL
    )*
    ;

common_stmt
    :   if_stmt
    |   comment_stmt
    |   config_stmt
    |   menu_config_stmt
    |   source_stmt
    ;

config_stmt
    :   start=config_start
        option_list=config_option_list
    ;

config_start
    :   'config'
        T_WORD
        T_EOL
    ;

config_option_list
    :
    (   config_option
    |   symbol_option
    |   depends
    |   help
    )*
    ;

menu_config_stmt
    :   start=menu_config_start
        option_list=config_option_list
    ;
    
menu_config_start
    :   'menuconfig'
        T_WORD
        T_EOL
    ;

choice_stmt
    :   start=choice_start
        option_list=choice_option_list
        sub_stmt_list=choice_sub_stmt_list
        choice_end
    ;

choice_start
    :   'choice'
        T_WORD?
        T_EOL
    ;

choice_option_list
    :
    (   choice_option
    |   depends
    |   help
    )*
    ;

choice_sub_stmt_list
    :
    (   common_stmt
    |   T_EOL
    )*
    ;
    
choice_end
    :   'endchoice'
        T_EOL
    ;

comment_stmt
    :   start=comment_start
        option_list=comment_option_list
    ;
    
comment_start
    :   'comment'
        prompt_value
        T_EOL
    ;
    
comment_option_list
    :
    (   depends
    )*
    ;

menu_stmt
    :   start=menu_start
        option_list=menu_option_list
        sub_stmt_list=menu_sub_stmt_list
        menu_end
    ;

menu_start
    :   'menu'
        prompt_value
        T_EOL
    ;

menu_option_list
    :
    (   visible
    |   depends
    )*
    ;

menu_sub_stmt_list
    :
    (   common_stmt
    |   menu_stmt
    |   choice_stmt
    |   T_EOL
    )*
    ;

menu_end
    :   'endmenu'
        T_EOL
    ;

if_stmt
    :   start=if_start
        sub_stmt_list=if_sub_stmt_list
        if_end
    ;
    
if_start
    :   'if'
        expr
        T_EOL
    ;

if_sub_stmt_list
    :
    (   common_stmt
    |   menu_stmt
    |   choice_stmt
    |   T_EOL
    )*
    ;
    
if_end
    :   'endif'
        T_EOL
    ;

source_stmt
    :   start=source_start
    ;
    
source_start
    :   'source'
        prompt_value
        T_EOL
    ;

main_menu_stmt
    :   start=main_menu_start
    ;

main_menu_start
    :   'mainmenu'
        prompt_value
        T_EOL
    ;

// =============================================

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
    (   'bool'
    |   'boolean'
    |   'tristate'
    |   'string'
    |   'hex'
    |   'int'
    )   prompt_value?
        if_option_frag?
        T_EOL
    ;

prompt_option
    :   'prompt'
        prompt_value
        if_option_frag?
        T_EOL
    ;

default_config_option
    :
    (   'default'
    |   'def_bool'
    |   'def_tristate'
    )	expr
        if_option_frag?
        T_EOL
    ;

default_choice_option
    :
    (   'default'
    |   'def_bool'
    |   'def_tristate'
    )	T_WORD
        if_option_frag?
        T_EOL
    ;

select_option
    :   'select'
        T_WORD
        if_option_frag?
        T_EOL
    ;

range_option
    :   'range'
        from=symbol
        to=symbol
        if_option_frag?
        T_EOL
    ;

option_option
    :   'option'
        param_list=option_param_list
        T_EOL
    ;

optional_option
    :   'optional'
        T_EOL
    ;

if_option_frag
    :   'if'
        expr
    ;

depends
    :   'depends' 'on'
        expr
        T_EOL
    ;
    
visible
    :   'visible'
        if_option_frag?
        T_EOL
    ;    
    
help
    :   start=help_start
        text=help_text
        help_end
    ;
    
help_start
    :   'help'
        T_EOL
    ;

help_text
    :   T_HELP_TEXT
    ;

help_end
    :   T_EOL
    ;

option_param_list
    :
    (   modules_option_param
    |   def_config_list_option_param
    |   env_option_param
    |   all_no_config_y_option_param
    )*
    ;

modules_option_param
    :   'modules'
    ;

def_config_list_option_param
    :   'defconfig_list'
    ;

env_option_param
    :   'env' '=' prompt_value
    ;

all_no_config_y_option_param
    :   'allnoconfig_y'
    ;

prompt_value
    :   T_WORD
    |   T_WORD_QUOTE
    ;

// ================================================================
// ================================================================

// T_LPAREN > T_NOT > T_EQUAL, T_UNEQUAL > T_AND > T_OR
// T_OR < T_AND < T_EQUAL, T_UNEQUAL < T_NOT < T_LPAREN

expr
    :         left=or_expr
    ;

or_expr
    :         left=and_expr
    (   '||'  right=or_expr
    )?
    ;

and_expr
    :         left=comp_expr
    (   '&&'  right=and_expr
    )?
    ;

comp_expr
    :         left=not_expr
    (   '='   right=not_expr
    |   '!='  right=not_expr
    )?
    ;

not_expr
    :         left=list_expr
    |   '!'   right=not_expr
    ;

list_expr
    :         left=symbol
    |   '('   right=or_expr   ')'
    ;

symbol
    :    T_WORD
    |    T_WORD_QUOTE
    ;

// ================================================================
// ================================================================


COMMENT
    :   '#' ~('\r'|'\n')* T_EOL                 {$channel=HIDDEN;}
    ;

WS  :   (' '|'\t')                              {$channel=HIDDEN;}
    ;

DASHES
    :   '---'                                   {$channel=HIDDEN;}
    ;

T_EOL
    :   '\r'? '\n'
    ;

T_HELP_TEXT
    :   '\u001F' ~('\u001F')* '\u001F'
    ;

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
