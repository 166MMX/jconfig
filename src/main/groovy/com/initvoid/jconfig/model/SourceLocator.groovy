package com.initvoid.jconfig.model

import java.nio.file.Path

class SourceLocator
{
    SourceLocator  parent
    Path           path
    int            line

    static List<SourceLocator>  instances

    static lookup (String name)
    {

    }
}
