package com.initvoid.jconfig

import groovy.transform.CompileStatic
import org.apache.commons.io.HexDump

import java.util.regex.Pattern

@CompileStatic
class PreProcessor
{
    static Pattern  HELP_PATTERN    = ~/(?i)\s*(---)?\s*help\s*(---)?\s*/
    static String   HELP_DELIMITER  = '\u001F'
    static int      INIT_LENGTH     = -1

    static void process(Reader reader, Writer writer)
    {
        boolean  firstLineInStream  = true

        boolean  contextHelpActive  = false
        boolean  endHelp            = false
        boolean  endHelpWritten     = false
        boolean  startHelp          = false
        boolean  startHelpWritten   = false
        int      firstIndent        = INIT_LENGTH
        int      currentIndent      = INIT_LENGTH

        String   currentLine        = reader.readLine()
        String   nextLine

        while (currentLine != null)
        {
            nextLine = reader.readLine()

            if (contextHelpActive && endHelpWritten)
            {
                contextHelpActive  = false
                endHelp            = false
                endHelpWritten     = false
                startHelp          = false
                startHelpWritten   = false
                firstIndent        = INIT_LENGTH
                currentIndent      = INIT_LENGTH
            }
            if (contextHelpActive)
            {
                if (!startHelpWritten)
                {
                    startHelp = true
                }

                if (currentLine.find(~/[ \t]+/) != null)
                {
                    currentIndent = indentLength(currentLine)
                    if (firstIndent != INIT_LENGTH && currentIndent < firstIndent)
                    {
                        endHelp = true
                    }
                }
                if (currentLine.find(~/[ \t]*$/) != null && nextLine?.find(~/^[^ \t]/) != null)
                {
                    endHelp = true
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
                contextHelpActive  = true
                currentLine        = currentLine.tr('-', ' ')
            }

            if (endHelp && !endHelpWritten && startHelpWritten)
            {
                writer.write(HELP_DELIMITER)
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

            if (startHelp && !startHelpWritten)
            {
                writer.write(HELP_DELIMITER)
                startHelpWritten = true
            }

            writer.write(currentLine)

            currentLine = nextLine
        }

        if (contextHelpActive && !endHelpWritten)
        {
            writer.write(HELP_DELIMITER)
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

        @Override
        void run()
        {
            PreProcessor.process(reader, writer)
            writer.close()
        }

        static void process(Reader reader, Writer writer)
        {
            def worker = new ThreadWorker()
            worker.reader = reader
            worker.writer = writer
            def thread = new Thread(worker)
            thread.daemon = true
            thread.start()
        }
    }
}