package com.initvoid.antlr3

import groovy.transform.CompileStatic
import org.antlr.runtime.Token
import org.antlr.runtime.TokenSource

@CompileStatic
class LazyFilterTokenStream extends LazyTokenStream
{
    protected int channel = Token.DEFAULT_CHANNEL

    LazyFilterTokenStream()
    {
    }

    LazyFilterTokenStream(int channel)
    {
        this.channel = channel
    }

    LazyFilterTokenStream(TokenSource tokenSource)
    {
        super(tokenSource)
    }

    LazyFilterTokenStream(TokenSource tokenSource, int channel)
    {
        super(tokenSource)
        this.channel = channel
    }

    @Override
    protected Token LB(int k)
    {
        if (pointer == -1)
            setup()
        if (k == 0)
            return null
        if (k < 0)
            return LT(-k)

        if (!isTokenOnChannel(pointer))
            pointer = findPreviousOnChannelTokenIndex(1)
        int index = findPreviousOnChannelTokenIndex(k)
        if (index < 0)
            return null

        Token token = get(index)
        return token
    }

    @Override
    Token LT(int k)
    {
        if (pointer == -1)
            setup()
        if (k == 0)
            return null
        if (k < 0)
            return LB(-k)

        if (!isTokenOnChannel(pointer))
            pointer = findNextOnChannelTokenIndex(1)
        int index = findNextOnChannelTokenIndex(k)

        Token token = get(index)
        if (token.tokenIndex > range)
            range = token.tokenIndex
        return token
    }

    protected boolean isTokenOnChannel(int index)
    {
        return isTokenOnChannel(index, channel)
    }

    protected boolean isTokenOnChannel(int index, int channel)
    {
        return get(index).channel == channel
    }

    protected int findNextOnChannelTokenIndex(int amount)
    {
        int index = pointer
        while (!(fetchedEOF && index > lastValidIndex) && amount > 0)
        {
            index++
            if (isTokenOnChannel(index))
                amount--
        }
        return index
    }

    protected int findPreviousOnChannelTokenIndex(int amount)
    {
        int index = pointer
        while (index > 0 && amount > 0)
        {
            index--
            if (isTokenOnChannel(index))
                amount--
        }
        if (amount > 0)
            index = -1
        return index
    }
}
