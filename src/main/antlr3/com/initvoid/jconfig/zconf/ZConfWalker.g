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
    :   s_list=statement_list                   { result.setStatementList($s_list.list); }
        EOF
    ;

statement_list
    returns                                     [ List<Statement> list = new LinkedList<>() ]
    :   V_STATEMENT_LIST
    (   options{greedy=true;}:
        item=main_menu_stmt                     { list.add($item.result); }
    |   item=if_stmt                            { list.add($item.result); }
    |   item=comment_stmt                       { list.add($item.result); }
    |   item=config_stmt                        { list.add($item.result); }
    |   item=menu_config_stmt                   { list.add($item.result); }
    |   item=source_stmt                        { list.add($item.result); }
    |   item=choice_stmt                        { list.add($item.result); }
    |   item=menu_stmt                          { list.add($item.result); }
    )+
    ;

option_list
    returns                                     [ List<Property> list = new LinkedList<>() ]
    :	V_OPTION_LIST
    (   options{greedy=true;}:
    	item=type_option                        { list.add($item.result); }
    |   item=prompt_option                      { list.add($item.result); }
    |   item=select_option                      { list.add($item.result); }
    |   item=range_option                       { list.add($item.result); }
    |   item=default_choice_option              { list.add($item.result); }
    |   item=default_config_option              { list.add($item.result); }
    |   item=optional_option                    { list.add($item.result); }
    |   item=option_option                      { list.add($item.result); }
    |   item=depends                            { list.add($item.result); }
    |   item=help                               { list.add($item.result); }
    )+
    ;

config_stmt
    returns                                     [ Statement result ]
    :   T_CONFIG                                { ConfigStatement o = new ConfigStatement(); }
        V_WORD                                  { o.setSymbolName($V_WORD.text); }
        o_list=option_list                      { o.setPropertyList($o_list.list); }
                                                { result = o; }
    ;

menu_config_stmt
    returns                                     [ Statement result ]
    :   T_MENUCONFIG                            { MenuConfigStatement o = new MenuConfigStatement(); }
        V_WORD                                  { o.setSymbolName($V_WORD.text); }
        o_list=option_list                      { o.setPropertyList($o_list.list); }
                                                { result = o; }
    ;

choice_stmt
    returns                                     [ Statement result ]
    :   T_CHOICE                                { ChoiceStatement o = new ChoiceStatement(); }
        V_WORD?                                 { o.setSymbolName($V_WORD.text); }
        o_list=option_list                      { o.setPropertyList($o_list.list); }
        s_list=statement_list                   { o.setStatementList($s_list.list); }
                                                { result = o; }
    ;

comment_stmt
    returns                                     [ Statement result ]
    :   T_COMMENT                               { CommentStatement o = new CommentStatement(); }
        V_WORD                                  { o.setPrompt($V_WORD.text); }
        o_list=option_list                      { o.setPropertyList($o_list.list); }
                                                { result = o; }
    ;

menu_stmt
    returns                                     [ Statement result ]
    :   T_MENU                                  { MenuStatement o = new MenuStatement(); }
        V_WORD                                  { o.setPrompt($V_WORD.text); }
        o_list=option_list                      { o.setPropertyList($o_list.list); }
        s_list=statement_list                   { o.setStatementList($s_list.list); }
                                                { result = o; }
    ;

if_stmt
    returns                                     [ Statement result ]
    :   T_IF                                    { IfStatement o = new IfStatement(); }
        e=expr                                  { o.setCondition($e.result); }
        s_list=statement_list                   { o.setStatementList($s_list.list); }
                                                { result = o; }
    ;

source_stmt
    returns                                     [ Statement result ]
    :   T_SOURCE                                { SourceStatement o = new SourceStatement(); }
        V_WORD                                  { o.setPath($V_WORD.text); }
                                                { result = o; }
    ;

main_menu_stmt
    returns                                     [ Statement result ]
    :   T_MAINMENU                              { MainMenuStatement o = new MainMenuStatement(); }
        V_WORD                                  { o.setPrompt($V_WORD.text); }
                                                { result = o; }
    ;

// ================================================================

type_option
    returns                                     [ Property result ]
    :                                           { TypeProperty o = null; }
    (   T_TYPE_BOOL                             { o = new BooleanTypeProperty(); }
    |   T_TYPE_BOOLEAN                          { o = new BooleanTypeProperty(); }
    |   T_TYPE_TRISTATE                         { o = new TriStateTypeProperty(); }
    |   T_TYPE_STRING                           { o = new StringTypeProperty(); }
    |   T_TYPE_HEX                              { o = new HexTypeProperty(); }
    |   T_TYPE_INT                              { o = new IntTypeProperty(); }
    )   V_WORD?                                 { o.setPrompt($V_WORD.text); }
        c=if_option_frag?                       { o.setCondition($c.result); }
                                                { result = o; }
    ;

prompt_option
    returns                                     [ Property result ]
    :   T_PROMPT                                { PromptProperty o = new PromptProperty(); }
        V_WORD                                  { o.setPrompt($V_WORD.text); }
        c=if_option_frag?                       { o.setCondition($c.result); }
                                                { result = o; }
    ;

default_config_option
    returns                                     [ Property result ]
    :                                           { DefaultProperty o = null; }
    (   T_DEFAULT                               { o = new DefaultProperty(); }
    |   T_DEFAULT_BOOL                          { o = new DefaultBooleanProperty(); }
    |   T_DEFAULT_TRISTATE                      { o = new DefaultTriStateProperty(); }
    )	e=expr                                  { o.setConfigExpression($e.result); }
        c=if_option_frag?                       { o.setCondition($c.result); }
                                                { result = o; }
    ;

default_choice_option
    returns                                     [ Property result ]
    :                                           { DefaultProperty o = null; }
    (   T_DEFAULT                               { o = new DefaultProperty(); }
    |   T_DEFAULT_BOOL                          { o = new DefaultBooleanProperty(); }
    |   T_DEFAULT_TRISTATE                      { o = new DefaultTriStateProperty(); }
    )	V_WORD                                  { o.setChoiceValue($V_WORD.text); }
        c=if_option_frag?                       { o.setCondition($c.result); }
                                                { result = o; }
    ;

select_option
    returns                                     [ Property result ]
    :   T_SELECT                                { SelectProperty o = new SelectProperty(); }
        V_WORD                                  { o.setSymbol($V_WORD.text); }
        c=if_option_frag?                       { o.setCondition($c.result); }
                                                { result = o; }
    ;

range_option
    returns                                     [ Property result ]
    :   T_RANGE                                 { RangeProperty o = new RangeProperty(); }
        from=symbol                             { o.setFrom($from.result); }
        to=symbol                               { o.setTo($to.result); }
        c=if_option_frag?                       { o.setCondition($c.result); }
                                                { result = o; }
    ;

option_option
    returns                                     [ Property result ]
    :   T_OPTION                                { OptionProperty o = new OptionProperty(); }
        param_list=option_param_list            { o.setParameterList($param_list.result); }
                                                { result = o; }
    ;

optional_option
    returns                                     [ Property result ]
    :   T_OPTIONAL                              { OptionalProperty o = new OptionalProperty(); }
                                                { result = o; }
    ;

depends
    returns                                     [ Property result ]
    :   T_DEPENDS_ON                            { DependsProperty o = new DependsProperty(); }
        e=expr                                  { o.setExpression($e.result); }
                                                { result = o; }
    ;

visible
    returns                                     [ Property result ]
    :   T_VISIBLE                               { VisibleProperty o = new VisibleProperty(); }
        c=if_option_frag?                       { o.setCondition($c.result); }
                                                { result = o; }
    ;

help
    returns                                     [ Property result ]
    :   T_HELP                                  { HelpProperty o = new HelpProperty(); }
        V_HELP_TEXT                             { o.setText($V_HELP_TEXT.text); }
                                                { result = o; }
    ;

if_option_frag
    returns                                     [ Expression result ]
    :   V_IF_FRAG
        e=expr                                  { result = $e.result; }
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
    :   V_EXPRESSION
        e=or_expr                               { result = $e.result; }
    ;

or_expr
    returns                                     [ Expression result ]
    :            left=and_expr                  { result = $left.result; }
    (   T_OR     right=or_expr                  { result = ExpressionImpl.createOrExpression(       $result,      $right.result); }
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
    |   T_OPEN_PAREN  right=expr  T_CLOSE_PAREN { result = ExpressionImpl.createListExpression(     $right.result); }
    ;

symbol
    returns                                     [ Symbol result ]
    :    V_WORD                                 { result = new Symbol($V_WORD.text); }
    ;
