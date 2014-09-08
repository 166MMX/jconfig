package com.initvoid.antlr3

class LazyFilterTokenStreamSpec extends LazyTokenStreamSpec
{
    LazyFilterTokenStream tokenStream

    def setup()
    {
        tokenStream = new LazyFilterTokenStream(tokenSource)
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

        then:
        tokenStream.LT(2) == token7
        tokenStream.LT(2) == token7
    }

    def 'LT k = -2'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()

        then:
        tokenStream.LT(-2) == token1
        tokenStream.LT(-2) == token1
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


    def 'LB k = 0'()
    {
        expect:
        tokenStream.LB(0) == null
        tokenStream.LB(0) == null
    }

    def 'LB k = 2'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()

        then:
        tokenStream.LB(2) == token1
        tokenStream.LB(2) == token1
    }

    def 'LB k = -2'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()

        then:
        tokenStream.LB(-2) == token7
        tokenStream.LB(-2) == token7
    }

    def 'LB k = -10'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()

        then:
        tokenStream.LB(-10) == token9
        tokenStream.LB(-10) == token9
    }

    def 'LB k = 10'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()

        then:
        tokenStream.LB(10) == null
        tokenStream.LB(10) == null
    }

}
