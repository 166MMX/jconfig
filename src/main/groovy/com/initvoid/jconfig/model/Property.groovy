package com.initvoid.jconfig.model

import com.initvoid.jconfig.zconf.expr.model.Expression

class Property
{
    //Property       next
    Symbol         symbol
    PropertyType   type
    String         text
    Expression     visible
    Expression     condition
    Menu           menu
    SourceLocator  source
}
