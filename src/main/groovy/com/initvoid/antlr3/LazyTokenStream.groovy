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
        setTokenSource(tokenSource)
    }

    protected void setup()
    {
        pointer = 0
    }

    void reset ()
    {
        tokenList.clear()
        pointer = -1
        lastMarker = -1
        range = -1
        fetchedEOF = false
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
        if (pointer == -1) setup()
        if (fetchedEOF && pointer == lastIndex) return
        pointer++
    }

    int size()
    {
        return tokenList.size()
    }

    int range()
    {
        return range
    }

    int index()
    {
        if (pointer == -1) setup()
        return pointer
    }

    int mark()
    {
        if (pointer == -1) setup()
        lastMarker = pointer
        return lastMarker
    }

    void release(int marker)
    {
    }

    void rewind(int marker)
    {
        release(marker)
        seek(marker)
    }

    void rewind()
    {
        if (lastMarker == -1) throw new IllegalStateException('mark() must be called at least once before calling rewind()')
        seek(lastMarker)
    }

    void seek(int index)
    {
        if (index < 0) pointer = 0
        if (fetchedEOF && index > lastIndex) pointer = lastIndex
        pointer = index
    }

    protected Token LB(int k)
    {
        if (pointer == -1) setup()
        if (k == 0) return null
        int index = pointer - k
        if (index < 0) return null
        Token token = get(index)
        return token
    }

    Token LT(int k)
    {
        if (pointer == -1) setup()
        if (k == 0) return null
        if (k < 0) return LB(-k)
        int index = pointer + k - 1
        if (fetchedEOF && index > lastIndex) return null
        Token token = get(index)
        if (token.tokenIndex > range) range = token.tokenIndex
        return token
    }

    int LA(int k)
    {
        if (k == 0) throw new IllegalArgumentException("k must be greater or lesser then 0 but not equal 0")
        Token token = LT(k)
        if (!token) throw new NoSuchElementException("relative position $k to current index pointer $pointer is out of range 0..$lastIndex")
        return token.type
    }

    Token get(int index)
    {
        Token token = null

        if (index < 0)
        {
            throw new NoSuchElementException("token index $index out of range 0..$lastIndex")
        }
        else if (index < tokenList.size())
        {
            token = tokenList[index]
        }
        else if (fetchedEOF)
        {
            token = tokenList[-1]
        }

        if (!token)
        {
            if (sync(index))
            {
                token = tokenList[index]
            }
            else
            {
                token = tokenList[-1]
            }
        }

        return token
    }

    protected boolean sync(int index)
    {
        int amount = index - lastIndex
        if (amount > 0 ) return fetch(amount)
        return true
    }

    protected boolean fetch(int amount)
    {
        int fetched = 0
        for (int i = 0; i < amount; i++)
        {
            Token token = tokenSource.nextToken()
            token.setTokenIndex(tokenList.size())
            tokenList.add(token)
            fetched++
            if (token.type == Token.EOF)
            {
                fetchedEOF = true
                break
            }
        }
        return amount == fetched
    }

    protected int getLastIndex()
    {
        return tokenList.size() - 1
    }

    void setTokenSource(TokenSource tokenSource)
    {
        this.tokenSource = tokenSource
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
