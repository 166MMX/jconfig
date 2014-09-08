package com.initvoid.antlr3

import groovy.transform.CompileStatic
import org.antlr.runtime.Token
import org.antlr.runtime.TokenSource
import org.antlr.runtime.TokenStream

@CompileStatic
class LazyTokenStream implements TokenStream
{
    protected TokenSource tokenSource
    protected boolean fetchedEOF = false

    protected List<Token> tokenList = new ArrayList<>()

    protected int lastMarker = -1
    protected int pointer = -1
    protected int range = -1

    LazyTokenStream()
    {
    }

    LazyTokenStream(TokenSource tokenSource)
    {
        this.tokenSource = tokenSource
    }

    protected void setup()
    {
        pointer = 0
        range = 0
    }

    void reset ()
    {
        tokenList.clear()

        lastMarker = -1
        pointer = -1
        range = 0
    }

    String toString(int start, int stop)
    {
        return null
    }

    String toString(Token start, Token stop)
    {
        return null
    }

    void consume()
    {
        if (pointer == -1)
            setup()
        if (fetchedEOF && pointer == lastValidIndex)
            return

        pointer++
    }

    int size()
    {
        return tokenList.size()
    }

    int range()
    {
        if (pointer == -1)
            setup()

        return range
    }

    int index()
    {
        if (pointer == -1)
            setup()

        return pointer
    }

    int mark()
    {
        if (pointer == -1)
            setup()

        lastMarker = pointer
        return lastMarker
    }

    void release(int marker)
    {
    }

    void rewind(int marker)
    {
        if (marker < 0)
            throw new IllegalArgumentException('marker must be greater then 0')

        release(marker)
        seek(marker)
    }

    void rewind()
    {
        if (lastMarker == -1)
            throw new IllegalStateException('mark() must be called at least once before calling rewind()')

        seek(lastMarker)
    }

    void seek(int index)
    {
        if (index < 0)
            pointer = 0
        else if (fetchedEOF && index > lastValidIndex)
            pointer = lastValidIndex
        else
            pointer = index
    }

    protected Token LB(int k)
    {
        if (pointer == -1)
            setup()
        if (k == 0)
            return null
        if (k < 0)
            return LT(-k)

        int index = pointer - k
        if (index < 0)
            return null

        Token token = get(index)
        return token
    }

    Token LT(int k)
    {
        if (pointer == -1)
            setup()
        if (k == 0)
            return null
        if (k < 0)
            return LB(-k)

        int index = pointer + k - 1

        Token token = get(index)
        if (token.tokenIndex > range)
            range = token.tokenIndex
        return token
    }

    int LA(int k)
    {
        if (k == 0)
            throw new IllegalArgumentException("k must be greater or lesser then but not equal 0")

        Token token = LT(k)
        if (!token)
            return Token.EOF

        return token.type
    }

    Token get(int index)
    {
        Token token

        if (index < 0)
            throw new NoSuchElementException("token index $index is out of range 0..$lastValidIndex")
        else if (index < tokenList.size())
            token = tokenList[index]
        else if (fetchedEOF)
            token = tokenList[-1]
        else if (sync(index))
            token = tokenList[index]
        else
            token = tokenList[-1]

        return token
    }

    protected boolean sync(int index)
    {
        boolean fullySynced

        int amount = index - lastValidIndex
        if (amount > 0 )
            fullySynced = fetch(amount)
        else
            fullySynced = true

        return fullySynced
    }

    protected boolean fetch(int amount)
    {
        boolean fullyFetched

        while (amount > 0)
        {
            Token token = tokenSource.nextToken()
            token.setTokenIndex(tokenList.size())
            tokenList.add(token)
            amount--
            if (token.type == Token.EOF)
            {
                fetchedEOF = true
                break
            }
        }
        fullyFetched = amount == 0

        return fullyFetched
    }

    protected int getLastValidIndex()
    {
        return tokenList.size() - 1
    }

    void setTokenSource(TokenSource tokenSource)
    {
        this.tokenSource = tokenSource
        fetchedEOF = false
        reset()
    }

    TokenSource getTokenSource()
    {
        return this.tokenSource
    }

    String getSourceName()
    {
        return tokenSource.sourceName
    }
}
