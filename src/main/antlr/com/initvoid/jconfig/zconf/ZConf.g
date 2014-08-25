grammar ZConf;

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

@parser::header {package com.initvoid.jconfig.zconf;

//import org.apache.commons.io.HexDump;
//import java.io.IOException;

import com.initvoid.jconfig.zconf.Input;
import com.initvoid.jconfig.zconf.expr.model.Expression;
import com.initvoid.jconfig.zconf.expr.model.ExpressionImpl;
import com.initvoid.jconfig.zconf.expr.model.Symbol;
import com.initvoid.jconfig.zconf.property.BooleanTypeProperty;
import com.initvoid.jconfig.zconf.property.DefaultBooleanProperty;
import com.initvoid.jconfig.zconf.property.DefaultProperty;
import com.initvoid.jconfig.zconf.property.DefaultTriStateProperty;
import com.initvoid.jconfig.zconf.property.DependsProperty;
import com.initvoid.jconfig.zconf.property.HelpProperty;
import com.initvoid.jconfig.zconf.property.HexTypeProperty;
import com.initvoid.jconfig.zconf.property.IntTypeProperty;
import com.initvoid.jconfig.zconf.property.OptionProperty;
import com.initvoid.jconfig.zconf.property.OptionalProperty;
import com.initvoid.jconfig.zconf.property.PromptProperty;
import com.initvoid.jconfig.zconf.property.Property;
import com.initvoid.jconfig.zconf.property.RangeProperty;
import com.initvoid.jconfig.zconf.property.SelectProperty;
import com.initvoid.jconfig.zconf.property.StringTypeProperty;
import com.initvoid.jconfig.zconf.property.TriStateTypeProperty;
import com.initvoid.jconfig.zconf.property.TypeProperty;
import com.initvoid.jconfig.zconf.property.VisibleProperty;
import com.initvoid.jconfig.zconf.statement.ChoiceStatement;
import com.initvoid.jconfig.zconf.statement.CommentStatement;
import com.initvoid.jconfig.zconf.statement.ConfigStatement;
import com.initvoid.jconfig.zconf.statement.IfStatement;
import com.initvoid.jconfig.zconf.statement.MainMenuStatement;
import com.initvoid.jconfig.zconf.statement.MenuConfigStatement;
import com.initvoid.jconfig.zconf.statement.MenuStatement;
import com.initvoid.jconfig.zconf.statement.SourceStatement;
import com.initvoid.jconfig.zconf.statement.Statement;

import org.apache.commons.lang3.StringEscapeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;
import java.util.LinkedList;}

@lexer::header {package com.initvoid.jconfig.zconf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;}

@parser::members {private static final Logger logger = LoggerFactory.getLogger(ZConfParser.class);

private static final Pattern SURROUNDING_QUOTES_PATTERN = Pattern.compile("^\"|\"$|^'|'$");

@Override public void displayRecognitionError(String[] tokenNames, RecognitionException ex)
{
    String hdr = getErrorHeader(ex);
    String msg = getErrorMessage(ex, tokenNames);
    if (logger.isErrorEnabled()) logger.error(hdr + " " + msg, ex);
}

private static String getQuotedStringValue(String value)
{
    if (value == null)
    {
        return null;
    }
    value = SURROUNDING_QUOTES_PATTERN.matcher(value).replaceAll("");
    value = StringEscapeUtils.unescapeJava(value);
    return value;
}}

@lexer::members {private static final Logger logger = LoggerFactory.getLogger(ZConfLexer.class);

@Override public void displayRecognitionError(String[] tokenNames, RecognitionException ex)
{
    String hdr = getErrorHeader(ex);
    String msg = getErrorMessage(ex, tokenNames);
    if (logger.isErrorEnabled()) logger.error(hdr + " " + msg, ex);
}}

input
    returns                                     [ Input result ]
    :                                           { result = new Input(); }
    (                                    options{ greedy = true; }:
        T_EOL
    )*
        sub_stmt_list=input_sub_stmt_list       { result.setStatementList($sub_stmt_list.result); }
    ;

input_sub_stmt_list
    returns                                     [ List<Statement> result ]
    :                                           { result = new LinkedList<>(); }
        main_menu_stmt?                         { result.add($main_menu_stmt.result); }
    (                                    options{ greedy = true; }:
        common_stmt                             { result.add($common_stmt.result); }
    |   choice_stmt                             { result.add($choice_stmt.result); }
    |   menu_stmt                               { result.add($menu_stmt.result); }
    |   T_EOL
    )*
    ;

common_stmt
    returns                                     [ Statement result ]
    :   if_stmt                                 { result = $if_stmt.result; }
    |   comment_stmt                            { result = $comment_stmt.result; }
    |   config_stmt                             { result = $config_stmt.result; }
    |   menu_config_stmt                        { result = $menu_config_stmt.result; }
    |   source_stmt                             { result = $source_stmt.result; }
    ;

config_stmt
    returns                                     [ ConfigStatement result ]
    :   start=config_start                      { result = $start.result; }
        option_list=config_option_list          { result.setPropertyList($option_list.result); }
    ;

config_start
    returns                                     [ ConfigStatement result ]
    :   'config'                                { result = new ConfigStatement(); }
        T_WORD                                  { result.setSymbolName($T_WORD.text); }
        T_EOL
    ;

config_option_list
    returns                                     [ List<Property> result ]
    :                                           { result = new LinkedList<>(); }
    (                                    options{ greedy = true; }:
        config_option                           { result.add($config_option.result); }
    |   symbol_option                           { result.add($symbol_option.result); }
    |   depends                                 { result.add($depends.result); }
    |   help                                    { result.add($help.result); }
    |   T_EOL
    )*
    ;

menu_config_stmt
    returns                                     [ MenuConfigStatement result ]
    :   start=menu_config_start                 { result = $start.result; }
        option_list=config_option_list          { result.setPropertyList($option_list.result); }
    ;
    
menu_config_start
    returns                                     [ MenuConfigStatement result ]
    :   'menuconfig'                            { result = new MenuConfigStatement(); }
        T_WORD                                  { result.setSymbolName($T_WORD.text); }
        T_EOL
    ;

choice_stmt
    returns                                     [ ChoiceStatement result ]
    :   start=choice_start                      { result = $start.result; }
        option_list=choice_option_list          { result.setPropertyList($option_list.result); }
        sub_stmt_list=choice_sub_stmt_list      { result.setStatementList($sub_stmt_list.result); }
        choice_end
    ;

choice_start
    returns                                     [ ChoiceStatement result ]
    :   'choice'                                { result = new ChoiceStatement(); }
        T_WORD?                                 { result.setSymbolName($T_WORD.text); }
        T_EOL
    ;

choice_option_list
    returns                                     [ List<Property> result ]
    :                                           { result = new LinkedList<>(); }
    (                                    options{ greedy = true; }:
        choice_option                           { result.add($choice_option.result); }
    |   depends                                 { result.add($depends.result); }
    |   help                                    { result.add($help.result); }
    |   T_EOL
    )*
    ;

choice_sub_stmt_list
    returns                                     [ List<Statement> result ]
    :                                           { result = new LinkedList<>(); }
    (                                    options{ greedy = true; }:
        common_stmt                             { result.add($common_stmt.result); }
    |   T_EOL
    )*
    ;
    
choice_end
    :   'endchoice'
        T_EOL
    ;

comment_stmt
    returns                                     [ CommentStatement result ]
    :   start=comment_start                     { result = $start.result; }
        option_list=comment_option_list         { result.setPropertyList($option_list.result); }
    ;
    
comment_start
    returns                                     [ CommentStatement result ]
    :   'comment'                               { result = new CommentStatement(); }
        prompt_value                            { result.setPrompt($prompt_value.result); }
        T_EOL
    ;
    
comment_option_list
    returns                                     [ List<Property> result ]
    :                                           { result = new LinkedList<>(); }
    (                                    options{ greedy = true; }:
        depends                                 { result.add($depends.result); }
    |   T_EOL
    )*
    ;

menu_stmt
    returns                                     [ MenuStatement result ]
    :   start=menu_start                        { result = $start.result; }
        option_list=menu_option_list            { result.setPropertyList($option_list.result); }
        sub_stmt_list=menu_sub_stmt_list        { result.setStatementList($sub_stmt_list.result); }
        menu_end
    ;

menu_start
    returns                                     [ MenuStatement result ]
    :   'menu'                                  { result = new MenuStatement(); }
        prompt_value                            { result.setPrompt($prompt_value.result); }
        T_EOL
    ;

menu_option_list
    returns                                     [ List<Property> result ]
    :                                           { result = new LinkedList<>(); }
    (                                    options{ greedy = true; }:
        visible                                 { result.add($visible.result); }
    |   depends                                 { result.add($depends.result); }
    |   T_EOL
    )*
    ;

menu_sub_stmt_list
    returns                                     [ List<Statement> result ]
    :                                           { result = new LinkedList<>(); }
    (                                    options{ greedy = true; }:
        common_stmt                             { result.add($common_stmt.result); }
    |   menu_stmt                               { result.add($menu_stmt.result); }
    |   choice_stmt                             { result.add($choice_stmt.result); }
    |   T_EOL
    )*
    ;

menu_end
    :   'endmenu'
        T_EOL
    ;

if_stmt
    returns                                     [ IfStatement result ]
    :   start=if_start                          { result = $start.result; }
        sub_stmt_list=if_sub_stmt_list          { result.setStatementList($sub_stmt_list.result); }
        if_end
    ;
    
if_start
    returns                                     [ IfStatement result ]
    :   'if'                                    { result = new IfStatement(); }
        expr                                    { result.setCondition($expr.result); }
        T_EOL
    ;

if_sub_stmt_list
    returns                                     [ List<Statement> result ]
    :                                           { result = new LinkedList<>(); }
    (                                    options{ greedy = true; }:
        common_stmt                             { result.add($common_stmt.result); }
    |   menu_stmt                               { result.add($menu_stmt.result); }
    |   choice_stmt                             { result.add($choice_stmt.result); }
    |   T_EOL
    )*
    ;
    
if_end
    :   'endif'
        T_EOL
    ;

source_stmt
    returns                                     [ SourceStatement result ]
    :   start=source_start                      { result = $start.result; }
    ;
    
source_start
    returns                                     [ SourceStatement result ]
    :   'source'                                { result = new SourceStatement(); }
        prompt_value                            { result.setPath($prompt_value.result); }
        T_EOL
    ;

main_menu_stmt
    returns                                     [ MainMenuStatement result ]
    :   start=main_menu_start                   { result = $start.result; }
    ;

main_menu_start
    returns                                     [ MainMenuStatement result ]
    :   'mainmenu'                              { result = new MainMenuStatement(); }
        prompt_value                            { result.setPrompt($prompt_value.result); }
        T_EOL
    ;

// ================================================================

config_option
    returns                                     [ Property result ]
    :   type_option                             { result = $type_option.result; }
    |   prompt_option                           { result = $prompt_option.result; }
    |   default_config_option                   { result = $default_config_option.result; }
    |   select_option                           { result = $select_option.result; }
    |   range_option                            { result = $range_option.result; }
    ;

choice_option
    returns                                     [ Property result ]
    :   type_option                             { result = $type_option.result; }
    |   prompt_option                           { result = $prompt_option.result; }
    |   default_choice_option                   { result = $default_choice_option.result; }
    |   optional_option                         { result = $optional_option.result; }
    ;

symbol_option
    returns                                     [ Property result ]
    :   option_option                           { result = $option_option.result; }
    ;

type_option
    returns                                     [ TypeProperty result ]
    :
    (   'bool'                                  { result = new BooleanTypeProperty(); }
    |   'boolean'                               { result = new BooleanTypeProperty(); }
    |   'tristate'                              { result = new TriStateTypeProperty(); }
    |   'string'                                { result = new StringTypeProperty(); }
    |   'hex'                                   { result = new HexTypeProperty(); }
    |   'int'                                   { result = new IntTypeProperty(); }
    )   prompt_value?                           { result.setPrompt($prompt_value.result); }
        if_option_frag?                         { result.setCondition($if_option_frag.result); }
        T_EOL
    ;

prompt_option
    returns                                     [ PromptProperty result ]
    :   'prompt'                                { result = new PromptProperty(); }
        prompt_value                            { result.setPrompt($prompt_value.result); }
        if_option_frag?                         { result.setCondition($if_option_frag.result); }
        T_EOL
    ;

default_config_option
    returns                                     [ DefaultProperty result ]
    :
    (   'default'                               { result = new DefaultProperty(); }
    |   'def_bool'                              { result = new DefaultBooleanProperty(); }
    |   'def_tristate'                          { result = new DefaultTriStateProperty(); }
    )	expr                                    { result.setConfigExpression($expr.result); }
        if_option_frag?                         { result.setCondition($if_option_frag.result); }
        T_EOL
    ;

default_choice_option
    returns                                     [ DefaultProperty result ]
    :
    (   'default'                               { result = new DefaultProperty(); }
    |   'def_bool'                              { result = new DefaultBooleanProperty(); }
    |   'def_tristate'                          { result = new DefaultTriStateProperty(); }
    )	T_WORD                                  { result.setChoiceValue($T_WORD.text); }
        if_option_frag?                         { result.setCondition($if_option_frag.result); }
        T_EOL
    ;

select_option
    returns                                     [ SelectProperty result ]
    :   'select'                                { result = new SelectProperty(); }
        T_WORD                                  { result.setSymbol($T_WORD.text); }
        if_option_frag?                         { result.setCondition($if_option_frag.result); }
        T_EOL
    ;

range_option
    returns                                     [ RangeProperty result ]
    :   'range'                                 { result = new RangeProperty(); }
        from=symbol                             { result.setFrom($from.result); }
        to=symbol                               { result.setTo($to.result); }
        if_option_frag?                         { result.setCondition($if_option_frag.result); }
        T_EOL
    ;

option_option
    returns                                     [ OptionProperty result ]
    :   'option'                                { result = new OptionProperty(); }
        param_list=option_param_list            { result.setParameterList($param_list.result); }
        T_EOL
    ;

optional_option
    returns                                     [ OptionalProperty result ]
    :   'optional'                              { result = new OptionalProperty(); }
        T_EOL
    ;

if_option_frag
    returns                                     [ Expression result ]
    :   'if'
        expr                                    { result = $expr.result; }
    ;

depends
    returns                                     [ DependsProperty result ]
    :   'depends' 'on'                          { result = new DependsProperty(); }
        expr                                    { result.setExpression($expr.result); }
        T_EOL
    ;
    
visible
    returns                                     [ VisibleProperty result ]
    :   'visible'                               { result = new VisibleProperty(); }
        if_option_frag?                         { result.setCondition($if_option_frag.result); }
        T_EOL
    ;    
    
help
    returns                                     [ HelpProperty result ]
    :   start=help_start                        { result = $start.result; }
        text=help_text                          { result.setText($text.result); }
        help_end
    ;
    
help_start
    returns                                     [ HelpProperty result ]
    :   'help'                                  { result = new HelpProperty(); }
        T_EOL
    ;

help_text
    returns                                     [ String result ]
    :   T_HELP_TEXT                             { result = $T_HELP_TEXT.text; }
    ;

// try { HexDump.dump((T_HELP_TEXT59!=null?T_HELP_TEXT59.getText():null).getBytes(), 0, System.out, 0); } catch (IOException e) { e.printStackTrace(); }

help_end
    :   T_EOL
    ;

option_param_list
    returns                                     [ List result ]
    :                                           { result = new LinkedList(); }
    (                                    options{ greedy = true; }:
        modules_option_param
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
    returns                                     [ String result ]
    :   T_WORD                                  { result = $T_WORD.text; }
    |   T_WORD_QUOTE                            { result = getQuotedStringValue($T_WORD_QUOTE.text); }
    ;

// ================================================================
// ================================================================

// T_LPAREN > T_NOT > T_EQUAL, T_UNEQUAL > T_AND > T_OR
// T_OR < T_AND < T_EQUAL, T_UNEQUAL < T_NOT < T_LPAREN

expr
    returns                                     [ Expression result ]
    :           left=or_expr                    { result = $left.result; }
    |   T_FOLD  left=or_expr                    { result = $left.result; }
    ;

or_expr
    returns                                     [ Expression result ]
    :           left=and_expr                   { result = $left.result; }
    (   '||'    right=or_expr                   { result = ExpressionImpl.createOrExpression(       $result,      $right.result); }
    )?
    ;

and_expr
    returns                                     [ Expression result ]
    :           left=comp_expr                  { result = $left.result; }
    (   '&&'    right=and_expr                  { result = ExpressionImpl.createAndExpression(      $result,      $right.result); }
    )?
    ;

comp_expr
    returns                                     [ Expression result ]
    :           left=not_expr                   { result = $left.result; }
    (   '='     right=not_expr                  { result = ExpressionImpl.createEqualExpression(    $left.result, $right.result); }
    |   '!='    right=not_expr                  { result = ExpressionImpl.createUnequalExpression(  $left.result, $right.result); }
    )?
    ;

not_expr
    returns                                     [ Expression result ]
    :           left=list_expr                  { result = $left.result; }
    |   '!'     right=not_expr                  { result = ExpressionImpl.createNotExpression(      $right.result); }
    ;

list_expr
    returns                                     [ Expression result ]
    :           left=symbol                     { result = $left.result; }
    |   '('     right=or_expr   ')'             { result = ExpressionImpl.createListExpression(     $right.result); }
    ;

symbol
    returns                                     [ Symbol result ]
    :    T_WORD                                 { result = new Symbol($T_WORD.text); }
    |    T_WORD_QUOTE                           { result = new Symbol(getQuotedStringValue($T_WORD_QUOTE.text)); }
    ;

// ================================================================
// ================================================================


COMMENT
    :   '#' ~('\r'|'\n')*                       {$channel=HIDDEN;}
    ;

WS  :   (' '|'\t')                              {$channel=HIDDEN;}
    ;

DASHES
    :   '---'                                   {$channel=HIDDEN;}
    ;

T_FOLD
    :   '\\' T_EOL                              {$channel=HIDDEN;}
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
