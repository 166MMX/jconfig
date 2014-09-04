package com.initvoid.antlr3

import groovy.transform.CompileStatic
import org.antlr.runtime.Token
import org.antlr.runtime.TokenSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
class LazyFilterTokenStream extends LazyTokenStream {


    protected int channel = Token.DEFAULT_CHANNEL

    LazyFilterTokenStream() {}

    LazyFilterTokenStream(int channel)
    {
        this.channel = channel
    }

    LazyFilterTokenStream(TokenSource tokenSource)
    {
        super(tokenSource)
    }

    LazyFilterTokenStream(TokenSource tokenSource, int channel) {
        super(tokenSource)
        this.channel = channel
    }

    @Override
    void consume()
    {
        super.consume()
        if (sourceEndReached && pointer >= tokenList.size()) pointer = tokenList.size() - 1
    }

    @Override
    protected Token LB(int k) {
        if (pointer == -1) setup()
        if (k == 0) return null
        if (!isTokenOnChannel(pointer)) pointer = findPreviousOnChannelTokenIndex(pointer)
        int index = pointer - k
        if (index < 0) return null
        index = findPreviousOnChannelTokenIndex(index)
        return get(index)
    }

    @Override
    Token LT(int k) {
        if (pointer == -1) setup()
        if (k == 0) return null
        if (k < 0) return LB(-k)
        if (!isTokenOnChannel(pointer)) pointer = findNextOnChannelTokenIndex(pointer)
        int index = pointer + k - 1
        if (sourceEndReached && index >= tokenList.size()) index = tokenList.size() - 1
        index = findNextOnChannelTokenIndex(index)
        if (index > range && !sourceEndReached) range = index
        return get(index)
    }

    protected boolean isTokenOnChannel(int index) {
        return isTokenOnChannel(index, channel)
    }

    protected boolean isTokenOnChannel(int index, int channel) {
        return get(index).channel == channel
    }

    protected int findNextOnChannelTokenIndex(int index) {
        while (!(sourceEndReached && index < tokenList.size()) && !isTokenOnChannel(index)) {
            index++
        }
        return index
    }

    protected int findPreviousOnChannelTokenIndex(int index) {
        while (index >= 0 && !isTokenOnChannel(index)) {
            index--
        }
        return index
    }
}
