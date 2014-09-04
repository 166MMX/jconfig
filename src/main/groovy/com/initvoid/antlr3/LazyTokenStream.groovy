package com.initvoid.antlr3

import groovy.transform.CompileStatic
import org.antlr.runtime.Token
import org.antlr.runtime.TokenSource
import org.antlr.runtime.TokenStream
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
class LazyTokenStream implements TokenStream {

    protected TokenSource tokenSource

    protected List<Token> tokenList = new ArrayList<>()

    protected int lastMarker = -1
    protected int pointer = -1
    protected int range = -1
    protected boolean sourceEndReached = false

    LazyTokenStream() {}

    LazyTokenStream(TokenSource tokenSource)
    {
        setTokenSource(tokenSource)
    }

    protected setup()
    {
        pointer = 0
    }

    void reset ()
    {
        tokenList.clear()
        pointer = -1
        lastMarker = -1
        range = -1
        sourceEndReached = false
    }

    void setTokenSource(TokenSource tokenSource) {
        this.tokenSource = tokenSource
        tokenList.clear()
        pointer = -1
    }

    TokenSource getTokenSource() {
        return this.tokenSource
    }

    String toString(int start, int stop) {
        return null
    }

    String toString(Token start, Token stop) {
        return null
    }

    void consume() {
        if (pointer == -1) setup()
        pointer++
    }

    int range() {
        return this.range
    }

    int mark() {
        if (pointer == -1) setup()
        lastMarker = pointer
        return lastMarker
    }

    int index() {
        if (pointer == -1) setup()
        return pointer
    }

    void rewind(int marker) {
        seek(marker)
    }

    void rewind() {
        if (lastMarker == -1) throw new IllegalStateException('mark() must be called at least once before calling rewind()')
        seek(lastMarker)
    }

    void release(int marker) {

    }

    void seek(int index) {
        pointer = index
    }

    int size() {
        return tokenList.size()
    }

    String getSourceName() {
        return tokenSource.sourceName
    }

    protected Token LB(int k) {
        if (pointer == -1) setup()
        if (k == 0) return null
        int index = pointer - k
        if (index < 0) return null
        return get(index)
    }

    Token LT(int k) {
        if (pointer == -1) setup()
        if (k == 0) return null
        if (k < 0) return LB(-k)
        int index = pointer + k - 1
        if (index > range && !sourceEndReached) range = index
        return get(index)
    }

    int LA(int k) {
        return LT(k).type
    }

    Token get(int index) {
        Token token = null
        if ( index < 0 || sourceEndReached && index >= tokenList.size() ) {
            throw new NoSuchElementException("token index $index out of range 0..${tokenList.size() - 1}")
        }
        if (index < tokenList.size())
        {
            token = tokenList[index]
        }
        else if (sourceEndReached)
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

    boolean sync(int index) {
        int amount = index - tokenList.size() + 1
        if (amount > 0 ) return fetch(amount)
        return true
    }

    boolean fetch(int amount) {
        int fetched = 0
        for (int i = 1; i <= amount; i++) {
            Token token = tokenSource.nextToken()
            token.setTokenIndex(tokenList.size())
            tokenList.add(token)
            fetched++
            if (token.type == Token.EOF) {
                sourceEndReached = true
                break
            }
        }
        return amount == fetched
    }
}
