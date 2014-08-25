package com.initvoid.jconfig.model

import com.initvoid.jconfig.zconf.expr.model.Expression

class Menu
{
    //Menu           next
    Menu           parent
    List<Menu>     list
    Symbol         symbol
    Property       prompt
    Expression     visibility
    Expression     dependency
    int            flags
    String         help
    SourceLocator  source
}
