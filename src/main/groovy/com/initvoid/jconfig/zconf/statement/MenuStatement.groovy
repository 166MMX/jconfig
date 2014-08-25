package com.initvoid.jconfig.zconf.statement

import com.initvoid.jconfig.zconf.property.DependsProperty
import com.initvoid.jconfig.zconf.property.Property
import com.initvoid.jconfig.zconf.property.VisibleProperty

class MenuStatement extends MenuEntryStatement
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
        property instanceof VisibleProperty  ||
        property instanceof DependsProperty
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
        statement instanceof SourceStatement      ||
        statement instanceof MenuStatement        ||
        statement instanceof ChoiceStatement
    }
}
