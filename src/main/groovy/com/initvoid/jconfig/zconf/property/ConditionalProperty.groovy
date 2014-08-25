package com.initvoid.jconfig.zconf.property

import com.initvoid.jconfig.zconf.expr.model.Expression

abstract class ConditionalProperty extends Property
{
    Expression condition
}