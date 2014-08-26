package com.initvoid.jconfig.zconf

import groovy.transform.CompileStatic
import org.antlr.runtime.CharStream
import org.antlr.runtime.RecognitionException
import org.antlr.runtime.RecognizerSharedState
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
abstract class Lexer extends org.antlr.runtime.Lexer
{
    Lexer() {
    }

    Lexer(CharStream input) {
        super(input)
    }

    Lexer(CharStream input, RecognizerSharedState state) {
        super(input, state)
    }

    protected static final Logger logger = LoggerFactory.getLogger(ZConfLexer.class)

    @Override public void displayRecognitionError(String[] tokenNames, RecognitionException ex)
    {
        String hdr = getErrorHeader(ex)
        String msg = getErrorMessage(ex, tokenNames)
        if (logger.isErrorEnabled()) logger.error(hdr + " " + msg, ex)
    }
}
