package com.initvoid.jconfig

import groovy.transform.CompileStatic

import java.util.regex.Pattern

@CompileStatic
class PreProcessor
{
    static Pattern  HELP_PATTERN    = ~/(?i)^\s*(---)?\s*help\s*(---)?\s*$/
    static String   HELP_DELIMITER  = '\u001F'
    static int      INIT_LENGTH     = -1

    static void process(InputStream inputStream, OutputStream outputStream)
    {
        OutputStreamWriter  outputStreamWriter  = new OutputStreamWriter(outputStream)
        InputStreamReader   inputStreamReader   = new InputStreamReader(inputStream)
        BufferedReader      bufferedReader      = new BufferedReader(inputStreamReader)

        boolean  firstLineInStream    = true

        boolean  contextHelpActive    = false
        boolean  writeEndMarker       = false
        boolean  endMarkerWritten     = false
        boolean  writeStartMarker     = false
        boolean  startMarkerWritten   = false
        int      firstHelpLineIndent  = INIT_LENGTH

        String   line

        while ((line = bufferedReader.readLine()) != null)
        {
            if (contextHelpActive && endMarkerWritten)
            {
                contextHelpActive    = false
                writeEndMarker       = false
                endMarkerWritten     = false
                writeStartMarker     = false
                startMarkerWritten   = false
                firstHelpLineIndent  = INIT_LENGTH
            }
            if (contextHelpActive)
            {
                if (!startMarkerWritten)
                {
                    writeStartMarker = true
                }

                boolean emptyLine = line.length() == 0
                int currentHelpLineIndent = indentLength(line)

                if (emptyLine)
                {
                    'nop'
                }
                else if (firstHelpLineIndent == INIT_LENGTH)
                {
                    firstHelpLineIndent = currentHelpLineIndent
                }
                else if (currentHelpLineIndent < firstHelpLineIndent)
                {
                    writeEndMarker = true
                }
            }
            else if (line.matches(HELP_PATTERN))
            {
                contextHelpActive  = true
                line               = line.tr('-', ' ')
            }

            if (writeEndMarker && !endMarkerWritten)
            {
                outputStreamWriter.write(HELP_DELIMITER)
                endMarkerWritten = true
            }

            if (firstLineInStream)
            {
                firstLineInStream = false
            }
            else
            {
                // end previous line
                outputStreamWriter.write('\n')
            }

            if (writeStartMarker && !startMarkerWritten)
            {
                outputStreamWriter.write(HELP_DELIMITER)
                startMarkerWritten = true
            }

            outputStreamWriter.write(line)
        }

        if (contextHelpActive && !endMarkerWritten)
        {
            outputStreamWriter.write(HELP_DELIMITER)
        }

        // end last line
        outputStreamWriter.write('\n')

        outputStreamWriter.flush()
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
        def bufferedInputStream    = url.newInputStream()
        def byteArrayOutputStream  = new ByteArrayOutputStream()

        process(bufferedInputStream, byteArrayOutputStream)

        def result = byteArrayOutputStream.toString()

        println result

        'nop'
    }

}