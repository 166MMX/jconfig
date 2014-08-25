package com.initvoid.jconfig.zconf.statement

import com.initvoid.jconfig.zconf.property.Property
import com.initvoid.jconfig.zconf.property.PropertyContainer

abstract class Statement implements PropertyContainer, StatementContainer
{
    List<Property>   propertyList
    List<Statement>  statementList

    @Override
    boolean addProperty(Property property)
    {
        throw new UnsupportedOperationException()
    }

    @Override
    boolean addStatement(Statement statement)
    {
        throw new UnsupportedOperationException()
    }
}
