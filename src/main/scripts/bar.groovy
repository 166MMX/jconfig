import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.net.SocketAppender
import com.initvoid.jconfig.PreProcessor
import com.initvoid.jconfig.zconf.Input
import com.initvoid.jconfig.zconf.ZConfLexer
import com.initvoid.jconfig.zconf.ZConfParser
import org.antlr.runtime.ANTLRInputStream
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

//Input result = test('C:\\Users\\MMX\\IdeaProjects\\378b037ec0913cad7218\\src\\main\\script\\res\\drivers_virtio_Kconfig.cache')

List<Input> result = testDirectoryEntries(Paths.get('C:\\Users\\MMX\\IdeaProjects\\378b037ec0913cad7218\\src\\main\\script\\res'), '*.cache')

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
    test(url.newInputStream(), url as String)
}

Input test(String path)
{
    test(Paths.get(path))
}

Input test(Path path)
{
     test(path.newInputStream(), path as String)
}

Input test(BufferedInputStream bufferedInputStream, String reference)
{
    PipedOutputStream pipedOutputStream = new PipedOutputStream()
    PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream)

    PreProcessingWorker.process(bufferedInputStream, pipedOutputStream)

    Input result = null

    MDC.put('reference', reference)

    try
    {
        ANTLRInputStream antlrInputStream = new ANTLRInputStream(pipedInputStream)

        ZConfLexer lexer = new ZConfLexer(antlrInputStream)
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

class PreProcessingWorker implements Runnable
{
    InputStream inputStream
    OutputStream outputStream

    @Override
    void run()
    {
        PreProcessor.process(inputStream, outputStream)
        outputStream.close()
    }

    static void process(InputStream inputStream, OutputStream outputStream)
    {
        def worker = new PreProcessingWorker()
        worker.inputStream = inputStream
        worker.outputStream = outputStream
        def thread = new Thread(worker)
        thread.daemon = true
        thread.start()
    }
}
