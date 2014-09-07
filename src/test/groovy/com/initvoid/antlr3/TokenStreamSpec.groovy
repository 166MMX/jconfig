package com.initvoid.antlr3

import org.antlr.runtime.TokenStream

abstract class TokenStreamSpec extends IntStreamSpec
{
    TokenStream tokenStream

    def setup()
    {
        tokenStream = new LazyTokenStream(tokenSource)
    }

    def 'LT k = 0'()
    {
        expect:
        tokenStream.LT(0) == null
        tokenStream.LT(0) == null
    }

    def 'LT k = 2'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()

        then:
        tokenStream.LT(2) == token5
        tokenStream.LT(2) == token5
    }

    def 'LT k = -2'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()

        then:
        tokenStream.LT(-2) == token2
        tokenStream.LT(-2) == token2
    }

    def 'LT k = -10'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()

        then:
        tokenStream.LT(-10) == null
        tokenStream.LT(-10) == null
    }

    def 'LT k = 10'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()

        then:
        tokenStream.LT(10) == token9
        tokenStream.LT(10) == token9
    }

    def 'range with setup'()
    {
        expect:
        tokenStream.range() == 0
    }

    def 'range with only consume'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()

        then:
        tokenStream.range() == 0
    }

    def 'range with look ahead'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.LA(1)
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()

        then:
        tokenStream.range() == 3
    }

    def 'range source stream end'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.LA(100)
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()

        then:
        tokenStream.range() == 8
    }

    // TODO
    def get()
    {

    }

    def 'test getTokenSource'()
    {
        expect:
        tokenStream.tokenSource == tokenSource
    }

}
