tree grammar ZConfWalker;

options {
    language     = Java;
    tokenVocab   = ZConfMain;
    tokenVocab   = ZConfHelp;
    ASTLabelType = CommonTree;
}

@header { package com.initvoid.jconfig.zconf;

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

import java.util.LinkedList;}

content
    returns                                     [ Input result = new Input() ]
    :   statement_list                          { result.setStatementList($statement_list.list); }
        EOF
    ;

statement_list
    returns                                     [ List<Statement> list = new LinkedList<>(); ]
    :   V_STATEMENT_LIST
    (   options{greedy=true;}:
        main_menu_stmt                          { list.add($main_menu_stmt.result); }
    |   if_stmt                                 { list.add($if_stmt.result); }
    |   comment_stmt                            { list.add($comment_stmt.result); }
    |   config_stmt                             { list.add($config_stmt.result); }
    |   menu_config_stmt                        { list.add($menu_config_stmt.result); }
    |   source_stmt                             { list.add($source_stmt.result); }
    |   choice_stmt                             { list.add($choice_stmt.result); }
    |   menu_stmt                               { list.add($menu_stmt.result); }
    )*
    ;

option_list
    returns                                     [ List<Property> list = new LinkedList<>(); ]
    :	V_OPTION_LIST
    (   options{greedy=true;}:
    	type_option                             { list.add($type_option.result); }
    |   prompt_option                           { list.add($prompt_option.result); }
    |   select_option                           { list.add($select_option.result); }
    |   range_option                            { list.add($range_option.result); }
    |   default_choice_option                   { list.add($default_choice_option.result); }
    |   default_config_option                   { list.add($default_config_option.result); }
    |   optional_option                         { list.add($optional_option.result); }
    |   option_option                           { list.add($option_option.result); }
    |   depends                                 { list.add($depends.result); }
    |   help                                    { list.add($help.result); }
    )*
    ;

config_stmt
    returns                                     [ ConfigStatement result = new ConfigStatement(); ]
    :   T_CONFIG  V_WORD                        { result.setSymbolName($V_WORD.text); }
        option_list                             { result.setPropertyList($option_list.list); }
    ;

menu_config_stmt
    returns                                     [ MenuConfigStatement result = new MenuConfigStatement(); ]
    :   T_MENUCONFIG  V_WORD                    { result.setSymbolName($V_WORD.text); }
        option_list                             { result.setPropertyList($option_list.list); }
    ;

choice_stmt
    returns                                     [ ChoiceStatement result = new ChoiceStatement(); ]
    :   T_CHOICE  V_WORD?                       { result.setSymbolName($V_WORD.text); }
        option_list                             { result.setPropertyList($option_list.list); }
        statement_list                          { result.setStatementList($statement_list.list); }
    ;

comment_stmt
    returns                                     [ CommentStatement result = new CommentStatement(); ]
    :   T_COMMENT  V_WORD                       { result.setPrompt($V_WORD.text); }
        option_list                             { result.setPropertyList($option_list.list); }
    ;

menu_stmt
    returns                                     [ MenuStatement result = new MenuStatement(); ]
    :   T_MENU  V_WORD                          { result.setPrompt($V_WORD.text); }
        option_list                             { result.setPropertyList($option_list.list); }
        statement_list                          { result.setStatementList($statement_list.list); }
    ;

if_stmt
    returns                                     [ IfStatement result = new IfStatement(); ]
    :   T_IF  expr                              { result.setCondition($expr.result); }
        statement_list                          { result.setStatementList($statement_list.list); }
    ;

source_stmt
    returns                                     [ SourceStatement result = new SourceStatement(); ]
    :   T_SOURCE  V_WORD                        { result.setPath($V_WORD.text); }
    ;

main_menu_stmt
    returns                                     [ MainMenuStatement result = new MainMenuStatement(); ]
    :   T_MAINMENU  V_WORD                      { result.setPrompt($V_WORD.text); }
    ;

// ================================================================

type_option
    returns                                     [ TypeProperty result ]
    :
    (   T_TYPE_BOOL                             { result = new BooleanTypeProperty(); }
    |   T_TYPE_BOOLEAN                          { result = new BooleanTypeProperty(); }
    |   T_TYPE_TRISTATE                         { result = new TriStateTypeProperty(); }
    |   T_TYPE_STRING                           { result = new StringTypeProperty(); }
    |   T_TYPE_HEX                              { result = new HexTypeProperty(); }
    |   T_TYPE_INT                              { result = new IntTypeProperty(); }
    )   V_WORD?                                 { result.setPrompt($V_WORD.text); }
        if_option_frag?                         { result.setCondition($if_option_frag.result); }
    ;

prompt_option
    returns                                     [ PromptProperty result = new PromptProperty(); ]
    :   T_PROMPT  V_WORD                        { result.setPrompt($V_WORD.text); }
        if_option_frag?                         { result.setCondition($if_option_frag.result); }
    ;

default_config_option
    returns                                     [ DefaultProperty result ]
    :
    (   T_DEFAULT                               { result = new DefaultProperty(); }
    |   T_DEFAULT_BOOL                          { result = new DefaultBooleanProperty(); }
    |   T_DEFAULT_TRISTATE                      { result = new DefaultTriStateProperty(); }
    )	expr                                    { result.setConfigExpression($expr.result); }
        if_option_frag?                         { result.setCondition($if_option_frag.result); }
    ;

default_choice_option
    returns                                     [ DefaultProperty result ]
    :
    (   T_DEFAULT                               { result = new DefaultProperty(); }
    |   T_DEFAULT_BOOL                          { result = new DefaultBooleanProperty(); }
    |   T_DEFAULT_TRISTATE                      { result = new DefaultTriStateProperty(); }
    )	V_WORD                                  { result.setChoiceValue($V_WORD.text); }
        if_option_frag?                         { result.setCondition($if_option_frag.result); }
    ;

select_option
    returns                                     [ SelectProperty result = new SelectProperty(); ]
    :   T_SELECT  V_WORD                        { result.setSymbol($V_WORD.text); }
        if_option_frag?                         { result.setCondition($if_option_frag.result); }
    ;

range_option
    returns                                     [ RangeProperty result = new RangeProperty(); ]
    :   T_RANGE
        from=symbol                             { result.setFrom($from.result); }
        to=symbol                               { result.setTo($to.result); }
        if_option_frag?                         { result.setCondition($if_option_frag.result); }
    ;

option_option
    returns                                     [ OptionProperty result = new OptionProperty(); ]
    :   T_OPTION
        param_list=option_param_list            { result.setParameterList($param_list.result); }
    ;

optional_option
    returns                                     [ OptionalProperty result = new OptionalProperty(); ]
    :   T_OPTIONAL
    ;

if_option_frag
    returns                                     [ Expression result ]
    :   T_IF  V_EXPRESSION  expr                { result = $expr.result; }
    ;

depends
    returns                                     [ DependsProperty result = new DependsProperty();  ]
    :   T_DEPENDS_ON  expr                      { result.setExpression($expr.result); }
    ;

visible
    returns                                     [ VisibleProperty result = new VisibleProperty(); ]
    :   T_VISIBLE  if_option_frag?              { result.setCondition($if_option_frag.result); }
    ;

help
    returns                                     [ HelpProperty result = new HelpProperty(); ]
    :   T_HELP V_HELP_TEXT                      { result.setText($V_HELP_TEXT.text); }
    ;

option_param_list                               // TODO finish implementation
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
    :   T_OPT_MODULES
    ;

def_config_list_option_param
    :   T_OPT_DEFCONFIG_LIST
    ;

env_option_param
    :   T_OPT_ENV T_EQUAL V_WORD
    ;

all_no_config_y_option_param
    :   T_OPT_ALLNOCONFIG_Y
    ;

// ================================================================
// ================================================================

// T_LPAREN > T_NOT > T_EQUAL, T_UNEQUAL > T_AND > T_OR
// T_OR < T_AND < T_EQUAL, T_UNEQUAL < T_NOT < T_LPAREN

expr
    returns                                     [ Expression result ]
    :            left=and_expr                  { result = $left.result; }
    (   T_OR     right=expr                     { result = ExpressionImpl.createOrExpression(       $result,      $right.result); }
    )?
    ;

and_expr
    returns                                     [ Expression result ]
    :            left=comp_expr                 { result = $left.result; }
    (   T_AND    right=and_expr                 { result = ExpressionImpl.createAndExpression(      $result,      $right.result); }
    )?
    ;

comp_expr
    returns                                     [ Expression result ]
    :              left=not_expr                { result = $left.result; }
    (   T_EQUAL    right=not_expr               { result = ExpressionImpl.createEqualExpression(    $left.result, $right.result); }
    |   T_UNEQUAL  right=not_expr               { result = ExpressionImpl.createUnequalExpression(  $left.result, $right.result); }
    )?
    ;

not_expr
    returns                                     [ Expression result ]
    :              left=list_expr               { result = $left.result; }
    |   T_NOT      right=not_expr               { result = ExpressionImpl.createNotExpression(      $right.result); }
    ;

list_expr
    returns                                     [ Expression result ]
    :                 left=symbol               { result = $left.result; }
    |   T_OPEN_PAREN  right=expr  T_CLOSE_PAREN
                                                { result = ExpressionImpl.createListExpression(     $right.result); }
    ;

symbol
    returns                                     [ Symbol result ]
    :    V_WORD                                 { result = new Symbol($V_WORD.text); }
    ;
