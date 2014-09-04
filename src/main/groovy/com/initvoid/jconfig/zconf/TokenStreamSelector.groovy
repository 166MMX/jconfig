package com.initvoid.jconfig.zconf

import groovy.transform.CompileStatic
import org.antlr.runtime.Token
import org.antlr.runtime.TokenSource
import org.antlr.runtime.TokenStream

@CompileStatic
public class TokenStreamSelector implements TokenStream
{
    Set<String> stringHashSet = new HashSet<>()
    Set<TokenStream> tokenStreamHashSet = new HashSet<>()

    Map<String, TokenStream> tokenStreamHashMap = new HashMap<>()
    Map<TokenStream, String> stringHashMap = new HashMap<>()

    Stack<TokenStream> tokenStreamStack = new Stack<>()

    public TokenStream current = null

    String getCurrentStreamName()
    {
        stringHashMap[current]
    }

    void addInputStream(TokenStream stream, String name)
    {
        stringHashSet << name
        tokenStreamHashSet << stream

        tokenStreamHashMap[name] = stream
        stringHashMap[stream] = name

        current = stream
    }

    void pop()
    {
        if (tokenStreamStack.size() > 0)
            current = tokenStreamStack.pop()
    }

    void push(TokenStream stream)
    {
        tokenStreamStack.push(current)
        select(stream)
    }

    void push(String name)
    {
        tokenStreamStack.push(current)
        select(name)
    }

    void select(TokenStream stream)
    {
        if (!tokenStreamHashSet.contains(stream))
        {
            throw new IllegalArgumentException("Given stream not registered")
        }
        current = stream
    }

    void select(String name)
    {
        if (!stringHashSet.contains(name))
        {
            throw new IllegalArgumentException("Given stream name '$name' not registered")
        }
        def stream = tokenStreamHashMap[name]
        current = stream
    }

    void undoSelect()
    {
        current = tokenStreamStack.peek()
    }

    Token LT(int k) {
        current.LT(k)
    }

    int range() {
        current.range()
    }

    Token get(int i) {
        current.get(i)
    }

    TokenSource getTokenSource() {
        current.getTokenSource()
    }

    String toString(int start, int stop) {
        current.toString(start, stop)
    }

    String toString(Token start, Token stop) {
        current.toString(start, stop)
    }

    void consume() {
        current.consume()
    }

    int LA(int i) {
        current.LA(i)
    }

    int mark() {
        current.mark()
    }

    int index() {
        current.index()
    }

    void rewind(int marker) {
        current.rewind(marker)
    }

    void rewind() {
        current.rewind()
    }

    void release(int marker) {
         current.release(marker)
    }

    void seek(int index) {
        current.seek(index)
    }

    int size() {
        current.size()
    }

    String getSourceName() {
        current.getSourceName()
    }
}

