package com.initvoid.antlr3

import groovy.transform.CompileStatic
import org.antlr.runtime.BufferedTokenStream
import org.antlr.runtime.LegacyCommonTokenStream
import org.antlr.runtime.Token
import org.antlr.runtime.TokenSource
import org.antlr.runtime.TokenStream
import org.antlr.runtime.UnbufferedTokenStream

@CompileStatic
public class TokenStreamSelector implements TokenStream
{
    protected Set<String> nameSet = new HashSet<>()
    protected Set<TokenStream> streamSet = new HashSet<>()

    protected Map<String, TokenStream> nameToStreamMap = new HashMap<>()
    protected Map<TokenStream, String> streamToNameMap = new HashMap<>()

    protected Stack<TokenStream> streamStack = new Stack<>()

    protected TokenStream input = null
    protected TokenStream originalInput = null

    void addInputStream(TokenStream stream, String name)
    {
        nameSet << name
        streamSet << stream

        nameToStreamMap[name] = stream
        streamToNameMap[stream] = name

        if (!input) selectRoot(stream)
    }

    void selectRoot(TokenStream stream)
    {
        select(stream)
        originalInput = null
        streamStack.clear()
    }

    void selectRoot(String name)
    {
        select(name)
        originalInput = null
        streamStack.clear()
    }

    void select(TokenStream stream)
    {
        if (!streamSet.contains(stream))
        {
            throw new IllegalArgumentException('Stream not registered')
        }
        if (!originalInput)
        {
            originalInput = input
        }
        input = stream
    }

    void select(String name)
    {
        if (!nameSet.contains(name))
        {
            throw new IllegalArgumentException("No named stream '$name' registered")
        }
        if (!originalInput)
        {
            originalInput = input
        }
        input = nameToStreamMap[name]
    }

    void select()
    {
        if (!originalInput) throw new IllegalStateException('No previous stream selected')
        input = originalInput
        originalInput = null
    }

    void pop()
    {
        if (streamStack.size() > 0)
            input = streamStack.pop()
    }

    void push(TokenStream stream)
    {
        if (!input) throw new IllegalStateException('No streams registered')
        streamStack.push(input)
        select(stream)
    }

    void push(String name)
    {
        if (!input) throw new IllegalStateException('No streams registered')
        streamStack.push(input)
        select(name)
    }

    void reset()
    {
        if (!input) throw new IllegalStateException('No streams registered')
        else if (input instanceof LazyTokenStream) ((LazyTokenStream) input).reset()
        else if (input instanceof BufferedTokenStream) ((BufferedTokenStream) input).reset()
        else if (input instanceof UnbufferedTokenStream) ((UnbufferedTokenStream) input).reset()
        else if (input instanceof LegacyCommonTokenStream) ((LegacyCommonTokenStream) input).reset()
        else throw new IllegalStateException("No such method on selected named stream '$streamName' implemented")
    }

    String getStreamName()
    {
        if (!input) throw new IllegalStateException('No streams registered')
        return getStreamName(input)
    }

    String getStreamName(TokenStream stream)
    {
        if (!streamSet.contains(stream)) throw new IllegalArgumentException('Stream not registered')
        return streamToNameMap[stream]
    }

    TokenStream getStream()
    {
        if (!input) throw new IllegalStateException('No streams registered')
        return input
    }

    TokenStream getStream(String name)
    {
        if (!nameSet.contains(name)) throw new IllegalArgumentException("No named stream '$name' registered")
        return nameToStreamMap[name]
    }

    Token LT(int k)
    {
        if (!input) throw new IllegalStateException('No streams registered')
        return input.LT(k)
    }

    int range()
    {
        if (!input) throw new IllegalStateException('No streams registered')
        return input.range()
    }

    Token get(int i)
    {
        if (!input) throw new IllegalStateException('No streams registered')
        input.get(i)
    }

    TokenSource getTokenSource()
    {
        if (!input) throw new IllegalStateException('No streams registered')
        return input.getTokenSource()
    }

    String toString(int start, int stop)
    {
        if (!input) throw new IllegalStateException('No streams registered')
        return input.toString(start, stop)
    }

    String toString(Token start, Token stop)
    {
        if (!input) throw new IllegalStateException('No streams registered')
        return input.toString(start, stop)
    }

    void consume()
    {
        if (!input) throw new IllegalStateException('No streams registered')
        input.consume()
    }

    int LA(int i)
    {
        if (!input) throw new IllegalStateException('No streams registered')
        return input.LA(i)
    }

    int mark()
    {
        if (!input) throw new IllegalStateException('No streams registered')
        return input.mark()
    }

    int index()
    {
        if (!input) throw new IllegalStateException('No streams registered')
        return input.index()
    }

    void rewind(int marker)
    {
        if (!input) throw new IllegalStateException('No streams registered')
        input.rewind(marker)
    }

    void rewind()
    {
        if (!input) throw new IllegalStateException('No streams registered')
        input.rewind()
    }

    void release(int marker)
    {
        if (!input) throw new IllegalStateException('No streams registered')
        input.release(marker)
    }

    void seek(int index)
    {
        if (!input) throw new IllegalStateException('No streams registered')
        input.seek(index)
    }

    int size()
    {
        if (!input) throw new IllegalStateException('No streams registered')
        return input.size()
    }

    String getSourceName()
    {
        if (!input) throw new IllegalStateException('No streams registered')
        return input.getSourceName()
    }
}

