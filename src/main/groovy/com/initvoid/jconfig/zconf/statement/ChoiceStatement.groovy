package com.initvoid.jconfig.zconf.statement

import com.initvoid.jconfig.zconf.property.DefaultProperty
import com.initvoid.jconfig.zconf.property.DependsProperty
import com.initvoid.jconfig.zconf.property.HelpProperty
import com.initvoid.jconfig.zconf.property.OptionalProperty
import com.initvoid.jconfig.zconf.property.PromptProperty
import com.initvoid.jconfig.zconf.property.Property
import com.initvoid.jconfig.zconf.property.TypeProperty

class ChoiceStatement extends SymbolStatement
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
        property instanceof TypeProperty      ||
        property instanceof PromptProperty    ||
        property instanceof DefaultProperty   ||
        property instanceof OptionalProperty  ||
        property instanceof DependsProperty   ||
        property instanceof HelpProperty
    }

    @Override
    boolean addStatement(Statement statement)
    {
        if (validStatement(statement))
        {
            statementList.add(statement)
        }
        else
        {
            false
        }
    }

    static boolean validStatement(Statement statement)
    {
        statement instanceof IfStatement          ||
        statement instanceof CommentStatement     ||
        statement instanceof ConfigStatement      ||
        statement instanceof MenuConfigStatement  ||
        statement instanceof SourceStatement
    }
}
