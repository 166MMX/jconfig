package com.initvoid.antlr3

class LazyTokenStreamSpec extends TokenStreamSpec
{
    LazyTokenStream tokenStream

    def setup()
    {
        tokenStream = new LazyTokenStream(tokenSource)
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
        tokenStream.LB(2) == token2
        tokenStream.LB(2) == token2
    }

    def 'LB k = -2'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()

        then:
        tokenStream.LB(-2) == token5
        tokenStream.LB(-2) == token5
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

    def 'index with reset'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        def index1 = tokenStream.index()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        def index2= tokenStream.index()
        tokenStream.reset()

        then:
        index1 == 3
        index2 == 10
        tokenStream.index() == 0
    }

    def 'range with reset'()
    {
        when:
        tokenStream.LA(3)
        def range1 = tokenStream.range()
        tokenStream.LA(7)
        def range2= tokenStream.range()
        tokenStream.LA(10)
        def range3= tokenStream.range()
        tokenStream.reset()

        then:
        range1 == 2
        range2 == 6
        range3 == 8
        tokenStream.range() == 0
    }

    def 'rewind with reset'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        def marker1 = tokenStream.mark()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.reset()
        tokenStream.rewind()

        then:
        marker1 == 2
        IllegalStateException e = thrown()
        e.cause == null
    }

    def 'size with reset'()
    {
        when:
        tokenStream.LA(3)
        def size1 = tokenStream.size()
        tokenStream.LA(7)
        def size2 = tokenStream.size()
        tokenStream.reset()

        then:
        size1 == 3
        size2 == 7
        tokenStream.size() == 0
    }

    def getLastValidIndex()
    {
        when:
        tokenStream.LA(3)
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.mark()
        tokenStream.consume()
        tokenStream.LA(4)
        tokenStream.consume()

        then:
        tokenStream.lastValidIndex == 6
    }

    def setTokenSource()
    {
        when:
        tokenStream.LA(3)
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.mark()
        tokenStream.consume()
        tokenStream.LA(4)
        tokenStream.consume()
        def size = tokenStream.size()
        def range = tokenStream.range()
        def index = tokenStream.index()
        tokenStream.tokenSource = tokenSource

        then:
        size == 7
        range == 6
        index == 4
        tokenStream.size() == 0
        tokenStream.range() == 0
        tokenStream.index() == 0
    }
}
