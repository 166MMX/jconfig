package com.initvoid.jconfig.zconf

import org.antlr.runtime.RecognitionException
import org.antlr.runtime.RecognizerSharedState
import org.antlr.runtime.TokenStream
import org.apache.commons.lang3.StringEscapeUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.regex.Pattern

abstract class Parser extends org.antlr.runtime.Parser
{
    protected static final Logger logger = LoggerFactory.getLogger(ZConfParser.class)

    private static final Pattern SURROUNDING_QUOTES_PATTERN = ~/^"|"\u0024|^'|'\u0024/

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

    protected static String getQuotedStringValue(String value)
    {
        if (value == null)
        {
            return null
        }
        value = SURROUNDING_QUOTES_PATTERN.matcher(value).replaceAll('')
        value = StringEscapeUtils.unescapeJava(value)
        return value
    }
}
