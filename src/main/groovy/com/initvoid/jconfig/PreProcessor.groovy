package com.initvoid.jconfig

import groovy.transform.CompileStatic

import java.util.regex.Pattern

@CompileStatic
class PreProcessor
{
    static Pattern  HELP_PATTERN    = ~/(?i)^(\s|---)*\bhelp\b(\s|---)*/
    static String   HELP_DELIMITER  = '\u001F'
    static int      INIT_LENGTH     = -1

    static void process(Reader reader, Writer writer)
    {
        process(reader, writer, HELP_DELIMITER)
    }

    static void process(Reader reader, Writer writer, String helpDelimiter)
    {
        // init
        boolean  firstLineInStream           = true

        boolean  helpContextActive           = false
        boolean  endHelpBeforeCurrentLine    = false
        boolean  endHelpAfterCurrentLine     = false
        boolean  endHelpWritten              = false
        boolean  startHelpBeforeCurrentLine  = false
        boolean  startHelpWritten            = false
        int      firstIndent                 = INIT_LENGTH
        int      currentIndent               = INIT_LENGTH

        // start
        String   currentLine                 = reader.readLine()
        String   nextLine

        while (currentLine != null)
        {
            nextLine = reader.readLine()

            // reset
            if (helpContextActive && endHelpWritten)
            {
                helpContextActive           = false
                endHelpBeforeCurrentLine    = false
                endHelpAfterCurrentLine     = false
                endHelpWritten              = false
                startHelpBeforeCurrentLine  = false
                startHelpWritten            = false
                firstIndent                 = INIT_LENGTH
                currentIndent               = INIT_LENGTH
            }

            // analyze the content carefully choose some actions
            if (helpContextActive)
            {
                if (!startHelpWritten)
                {
                    startHelpBeforeCurrentLine = true
                }

                if (currentLine.find(~/[ \t]+/) != null)
                {
                    currentIndent = indentLength(currentLine)
                    if (firstIndent != INIT_LENGTH && currentIndent < firstIndent)
                    {
                        endHelpBeforeCurrentLine = true
                    }
                }
                if (currentLine.find(~/[ \t]*$/) != null && nextLine?.find(~/^[^ \t]/) != null)
                {
                    endHelpAfterCurrentLine = true
                }
                else if (currentLine.find(~/[ \t]*$/) != null)
                {
                    'nop'
                }
                else if (currentLine.find(~/[^ \t].*/) != null && firstIndent == INIT_LENGTH)
                {
                    firstIndent = currentIndent
                }
            }
            else if (currentLine.find(HELP_PATTERN) != null)
            {
                helpContextActive  = true
                currentLine        = currentLine.tr('-', ' ')
            }

            // do the hard work
            if (endHelpBeforeCurrentLine && !endHelpWritten && startHelpWritten)
            {
                writer.write(helpDelimiter)
                endHelpWritten = true
            }

            if (firstLineInStream)
            {
                firstLineInStream = false
            }
            else
            {
                // end previous line
                writer.write('\n')
            }

            if (startHelpBeforeCurrentLine && !startHelpWritten)
            {
                writer.write(helpDelimiter)
                startHelpWritten = true
            }

            writer.write(currentLine)

            if (endHelpAfterCurrentLine && !endHelpWritten)
            {
                writer.write(helpDelimiter)
                endHelpWritten = true
            }

            currentLine = nextLine
        }

        // finish help context if started and left open
        if (helpContextActive)
        {
            if (startHelpWritten && !endHelpWritten)
            {
                writer.write(helpDelimiter)
                endHelpWritten = true
            }
        }

        // end last line
        writer.write('\n')

        writer.flush()
    }

    static int indentLength (String line)
    {
        int lineLength = line.length()
        int indentLength = 0

        for (int i = 0; i < lineLength; i++)
            if (line[i] == '\t')
                indentLength = (indentLength & ~7) + 8
            else if (line[i] == ' ')
                indentLength++
            else
                break

        return indentLength
    }

    static void main (String[] args)
    {
        def url = 'https://raw.githubusercontent.com/torvalds/linux/master/sound/Kconfig'.toURL()
        def reader = url.newReader()
        def writer = new StringWriter()

        process(reader, writer)

        //HexDump.dump(byteArrayOutputStream.toByteArray(), 0, System.out, 0)

        'nop'
    }

    static class ThreadWorker implements Runnable
    {
        Reader reader
        Writer writer
        String delimiter

        @Override
        void run()
        {
            PreProcessor.process(reader, writer, delimiter)
            writer.close()
        }

        static void process(Reader reader, Writer writer)
        {
            process(reader, writer, PreProcessor.HELP_DELIMITER)
        }

        static void process(Reader reader, Writer writer, String helpDelimiter)
        {
            def worker        = new ThreadWorker()
            worker.reader     = reader
            worker.writer     = writer
            worker.delimiter  = helpDelimiter
            def thread        = new Thread(worker)
            thread.daemon     = true
            thread.start()
        }
    }
}