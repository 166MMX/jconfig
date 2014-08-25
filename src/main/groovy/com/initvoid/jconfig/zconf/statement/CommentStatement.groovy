package com.initvoid.jconfig.zconf.statement

import com.initvoid.jconfig.zconf.property.DependsProperty
import com.initvoid.jconfig.zconf.property.Property

class CommentStatement extends MenuEntryStatement
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
        property instanceof DependsProperty
    }

    static boolean validStatement(Statement statement)
    {
        false
    }
}
