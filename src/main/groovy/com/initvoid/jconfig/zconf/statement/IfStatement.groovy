package com.initvoid.jconfig.zconf.statement

import com.initvoid.jconfig.zconf.expr.model.Expression
import com.initvoid.jconfig.zconf.property.Property

class IfStatement extends Statement
{
    Expression condition

    static boolean validProperty(Property property)
    {
        false
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
