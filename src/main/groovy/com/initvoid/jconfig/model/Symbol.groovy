package com.initvoid.jconfig.model

abstract class Symbol
{
    static def MAX_LENGTH = 256
    static def HASH_SIZE = 9973

    //Symbol           next
    String           name
    SymbolType       type
    SymbolValue      currentValue
    SymbolValue      defaultValue
    TriState         visible
    def              flags
    //Property         property
    List<Property>   properties

    ExpressionValue  forwardDependencies
    ExpressionValue  reverseDependencies

    SourceLocator    source

    static List<Symbol>  instances

    Symbol()
    {

    }

    abstract boolean isValueWithinRange(String value)

    abstract boolean setValue(String value)

    abstract String getDefaultStringValue()

    abstract String getStringValue()

    abstract void calculateValue()

    abstract void calculateVisibility()

    Property getChoiceProperty ()
    {
        Property property = null
        Util.for_all_choices(this, { Property p ->
            property = p
        })
        property
    }

    Property getEnvironmentProperty()
    {
        Property property = null
        Util.for_all_properties(this, PropertyType.ENV, { Property p ->
            property = p
        })
        property
    }

    boolean isChoice()
    {
        flags & SymbolFlag.CHOICE
    }

    boolean isChoiceValue()
    {
        flags & SymbolFlag.CHOICE_VALUE
    }

    boolean isOptional()
    {
        flags & SymbolFlag.OPTIONAL
    }

    boolean hasValue()
    {
        flags & SymbolFlag.DEF_USER
    }

}
