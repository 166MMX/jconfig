import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.net.SocketAppender
import com.initvoid.jconfig.PreProcessor
import com.initvoid.jconfig.zconf.Input
import com.initvoid.jconfig.zconf.ZConfLexer
import com.initvoid.jconfig.zconf.ZConfParser
import org.antlr.runtime.ANTLRReaderStream
import org.antlr.runtime.CommonTokenStream
import org.slf4j.LoggerFactory
import org.slf4j.MDC

import java.nio.file.DirectoryStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

LoggerContext loggerContext = LoggerFactory.ILoggerFactory as LoggerContext

SocketAppender socketAppender = new SocketAppender()
socketAppender.remoteHost = 'localhost'
socketAppender.includeCallerData = true
socketAppender.context = loggerContext
socketAppender.start()

Logger logger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME)
logger.level = Level.DEBUG
logger.addAppender(socketAppender)


//Input result = test('/Users/jharth/IdeaProjects/kconfig-to-xml/src/main/script/res/arch_mips_Kconfig.cache')

List<Input> result = testDirectoryEntries(Paths.get('/Users/jharth/IdeaProjects/kconfig-to-xml/src/main/script/res'), '*.cache')


socketAppender.stop()
loggerContext.stop()

'nop'

List<Input> testDirectoryEntries(Path dir, String glob)
{
    def result = [] as List<Input>

    DirectoryStream<Path> stream = null
    try
    {
        stream = Files.newDirectoryStream(dir, glob)
        for (Path entry : stream)
        {
            result << test(entry)
        }
    }
    finally
    {
        stream.close()
    }

    result
}

Input test(URL url)
{
    test(url.newReader(), url as String)
}

Input test(String path)
{
    test(Paths.get(path))
}

Input test(Path path)
{
     test(path.newReader(), path as String)
}

Input test(Reader reader, String reference)
{
    PipedWriter pipedWriter = new PipedWriter()
    PipedReader pipedReader = new PipedReader(pipedWriter)

    PreProcessor.ThreadWorker.process(reader, pipedWriter)

    Input result = null

    MDC.put('reference', reference)

    try
    {
        ANTLRReaderStream antlrReaderStream = new ANTLRReaderStream(pipedReader)

        ZConfLexer lexer = new ZConfLexer(antlrReaderStream)
        if (lexer.numberOfSyntaxErrors > 0)
        {
            println "Lexer  SyntaxErrors $lexer.numberOfSyntaxErrors"
        }

        CommonTokenStream tokens = new CommonTokenStream(lexer)
        ZConfParser parser = new ZConfParser(tokens)

        result = parser.input()
        if (parser.numberOfSyntaxErrors > 0)
        {
            println "Parser SyntaxErrors $parser.numberOfSyntaxErrors"
        }
    }
    finally
    {
        MDC.clear()
    }

    result
}
