package com.initvoid.antlr3

import org.antlr.runtime.ClassicToken
import org.antlr.runtime.IntStream
import org.antlr.runtime.Token
import org.antlr.runtime.TokenSource
import spock.lang.Specification

abstract class IntStreamSpec extends Specification
{
    static final int EOF     = Token.EOF
    static final int TYPE_A  = 4
    static final int TYPE_B  = 5
    static final int TYPE_C  = 6
    static final int TYPE_D  = 7

    TokenSource  tokenSource
    IntStream    tokenStream

    Token token1
    Token token2
    Token token3
    Token token4
    Token token5
    Token token6
    Token token7
    Token token8
    Token token9

    def setup()
    {
        token1 = new ClassicToken(TYPE_A, '1', Token.DEFAULT_CHANNEL)
        token2 = new ClassicToken(TYPE_B, '2', Token.HIDDEN_CHANNEL)
        token3 = new ClassicToken(TYPE_D, '2.5', Token.DEFAULT_CHANNEL)
        token4 = new ClassicToken(TYPE_A, '3', Token.DEFAULT_CHANNEL)
        token5 = new ClassicToken(TYPE_C, '4', Token.HIDDEN_CHANNEL)
        token6 = new ClassicToken(TYPE_B, '5', Token.HIDDEN_CHANNEL)
        token7 = new ClassicToken(TYPE_C, '6', Token.DEFAULT_CHANNEL)
        token8 = new ClassicToken(TYPE_A, '7', Token.HIDDEN_CHANNEL)
        token9 = new ClassicToken(EOF, null, Token.DEFAULT_CHANNEL)

        tokenSource = Mock(TokenSource)
        tokenSource.nextToken() >>> [token1, token2, token3, token4, token5, token6, token7, token8, token9]
        tokenSource.getSourceName() >> 'FancySourceName'

        tokenStream = new LazyTokenStream(tokenSource)
    }

    def consume()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()

        then:
        tokenStream.index() == 2
    }

    def 'consume without look ahead stream end'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()

        then:
        tokenStream.index() == 10
    }

    def 'look ahead and consume past stream end'()
    {
        when:
        tokenStream.LA(10)
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()

        then:
        tokenStream.index() == 8
    }

    def 'consume past stream end and look ahead'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.LA(1)

        then:
        tokenStream.index() == 10
    }

    def 'consume past stream end and look back'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.LA(-1)

        then:
        tokenStream.index() == 10
    }

    def 'LA k = 0'()
    {
        when:
        tokenStream.LA(0)

        then:
        IllegalArgumentException e = thrown()
        e.cause == null
    }

    def 'LA k = 2'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()

        then:
        tokenStream.LA(2) == TYPE_C
        tokenStream.LA(2) == TYPE_C
    }

    def 'LA k = -2'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()

        then:
        tokenStream.LA(-2) == TYPE_B
        tokenStream.LA(-2) == TYPE_B
    }

    def 'LA k = -10'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()

        then:
        tokenStream.LA(-10) == EOF
        tokenStream.LA(-10) == EOF
    }

    def 'LA k = 10'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()

        then:
        tokenStream.LA(10) == EOF
        tokenStream.LA(10) == EOF
    }

    def 'mark with setup'()
    {
        when:
        def marker = tokenStream.mark()
        tokenStream.consume()
        tokenStream.consume()

        then:
        marker == 0
    }

    def 'mark'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        def marker = tokenStream.mark()

        then:
        marker == 2
    }

    def 'index with setup'()
    {
        when:
        def index = tokenStream.index()
        tokenStream.consume()
        tokenStream.consume()

        then:
        index == 0
    }

    def 'index'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        def index = tokenStream.index()

        then:
        index == 2
    }

    def 'rewind with marker'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        def marker1 = tokenStream.mark()
        tokenStream.consume()
        def marker2 = tokenStream.mark()
        tokenStream.consume()
        tokenStream.rewind(marker1)

        then:
        marker1 == 2
        marker2 == 3
        tokenStream.index() == 2
    }

    def rewind()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        def marker1 = tokenStream.mark()
        tokenStream.consume()
        def marker2 = tokenStream.mark()
        tokenStream.consume()
        tokenStream.rewind()

        then:
        marker1 == 2
        marker2 == 3
        tokenStream.index() == 3
    }

    def 'rewind with illegal arg'()
    {
        when:
        tokenStream.rewind(-10)

        then:
        IllegalArgumentException e = thrown()
        e.cause == null
    }

    def 'rewind at illegal state'()
    {
        when:
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.consume()
        tokenStream.rewind()

        then:
        IllegalStateException e = thrown()
        e.cause == null
    }

    def 'seek reverse past start'()
    {
        when:
        tokenStream.LA(100)
        tokenStream.seek(-100)

        then:
        tokenStream.index() == 0
    }

    def 'seek'()
    {
        when:
        tokenStream.LA(100)
        tokenStream.seek(3)

        then:
        tokenStream.index() == 3
    }

    def 'seek twice'()
    {
        when:
        tokenStream.LA(100)
        tokenStream.seek(3)
        tokenStream.seek(3)

        then:
        tokenStream.index() == 3
    }

    def 'seek forward past end'()
    {
        when:
        tokenStream.LA(100)
        tokenStream.seek(100)

        then:
        tokenStream.index() == 8
    }

    def 'size unused'()
    {
        expect:
        tokenStream.size() == 0
    }

    def 'size middle stream'()
    {
        when:
        tokenStream.LA(3)

        then:
        tokenStream.size() == 3
    }

    def 'size end stream reached'()
    {
        when:
        tokenStream.consume()
        tokenStream.LA(100)

        then:
        tokenStream.size() == 9
    }

    def getSourceName()
    {
        expect:
        tokenStream.sourceName == 'FancySourceName'
    }

}
