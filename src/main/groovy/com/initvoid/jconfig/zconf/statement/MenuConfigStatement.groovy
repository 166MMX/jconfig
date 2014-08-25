package com.initvoid.jconfig.zconf.statement

import com.initvoid.jconfig.zconf.property.DefaultProperty
import com.initvoid.jconfig.zconf.property.DependsProperty
import com.initvoid.jconfig.zconf.property.HelpProperty
import com.initvoid.jconfig.zconf.property.OptionProperty
import com.initvoid.jconfig.zconf.property.PromptProperty
import com.initvoid.jconfig.zconf.property.Property
import com.initvoid.jconfig.zconf.property.RangeProperty
import com.initvoid.jconfig.zconf.property.SelectProperty
import com.initvoid.jconfig.zconf.property.TypeProperty

class MenuConfigStatement extends SymbolStatement
{
    @Override
    boolean addProperty(Property property)
    {
        if (validProperty(property))
        {
            propertyList.add(property)
        }
        else
        {
            false
        }
    }

    static boolean validProperty(Property property)
    {
        property instanceof TypeProperty     ||
        property instanceof PromptProperty   ||
        property instanceof DefaultProperty  ||
        property instanceof SelectProperty   ||
        property instanceof RangeProperty    ||
        property instanceof OptionProperty   ||
        property instanceof DependsProperty  ||
        property instanceof HelpProperty
    }

    static boolean validStatement(Statement statement)
    {
        false
    }
}
