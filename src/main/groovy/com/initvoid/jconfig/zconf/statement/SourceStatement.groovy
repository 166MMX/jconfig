package com.initvoid.jconfig.zconf.statement

import com.initvoid.jconfig.zconf.property.PromptProperty
import com.initvoid.jconfig.zconf.property.Property

class SourceStatement extends Statement
{
    String path

    static boolean validProperty(Property property)
    {
        false
    }

    static boolean validStatement(Statement statement)
    {
        false
    }
}
