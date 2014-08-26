package com.initvoid.jconfig.zconf

import groovy.transform.CompileStatic
import org.antlr.runtime.RecognitionException
import org.antlr.runtime.RecognizerSharedState
import org.antlr.runtime.TokenStream
import org.apache.commons.lang3.StringEscapeUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
abstract class Parser extends org.antlr.runtime.Parser
{
    protected static final Logger logger = LoggerFactory.getLogger(ZConfParser.class)

    Parser(TokenStream input) {
        super(input)
    }

    Parser(TokenStream input, RecognizerSharedState state) {
        super(input, state)
    }

    @Override public void displayRecognitionError(String[] tokenNames, RecognitionException ex)
    {
        String hdr = getErrorHeader(ex)
        String msg = getErrorMessage(ex, tokenNames)
        if (logger.isErrorEnabled()) logger.error("$hdr $msg", ex)
    }

    protected static String parseJavaString(String value)
    {
        if (value == null)
        {
            return null
        }
        value = value[1..-2]
        value = StringEscapeUtils.unescapeJava(value)
        return value
    }

    protected static String stripHelpTextDelimiters(String value)
    {
        if (value == null)
        {
            return null
        }
        value = value[1..-2]
        return value
    }
}
