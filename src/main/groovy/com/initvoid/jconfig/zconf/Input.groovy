package com.initvoid.jconfig.zconf

import com.initvoid.jconfig.zconf.statement.ChoiceStatement
import com.initvoid.jconfig.zconf.statement.CommentStatement
import com.initvoid.jconfig.zconf.statement.ConfigStatement
import com.initvoid.jconfig.zconf.statement.IfStatement
import com.initvoid.jconfig.zconf.statement.MainMenuStatement
import com.initvoid.jconfig.zconf.statement.MenuConfigStatement
import com.initvoid.jconfig.zconf.statement.MenuStatement
import com.initvoid.jconfig.zconf.statement.SourceStatement
import com.initvoid.jconfig.zconf.statement.Statement
import com.initvoid.jconfig.zconf.statement.StatementContainer

class Input implements StatementContainer
{
    List<Statement>  statementList

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
        statement instanceof MainMenuStatement    ||
        statement instanceof IfStatement          ||
        statement instanceof CommentStatement     ||
        statement instanceof ConfigStatement      ||
        statement instanceof MenuConfigStatement  ||
        statement instanceof SourceStatement      ||
        statement instanceof ChoiceStatement      ||
        statement instanceof MenuStatement
    }
}
