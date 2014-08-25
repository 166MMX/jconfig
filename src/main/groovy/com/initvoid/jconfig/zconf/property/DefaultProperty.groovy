package com.initvoid.jconfig.zconf.property

import com.initvoid.jconfig.zconf.expr.model.Expression

class DefaultProperty extends ConditionalProperty
{
    String      choiceValue
    Expression  configExpression
}
