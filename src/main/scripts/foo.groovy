import com.initvoid.jconfig.PreProcessor

def url = 'https://raw.githubusercontent.com/torvalds/linux/master/arch/x86/Kconfig'.toURL()
def file = new File('/Users/jharth/IdeaProjects/kconfig-to-xml/src/main/script/res/arch_mips_Kconfig.cache')
//BufferedInputStream bufferedInputStream = url.newInputStream()
BufferedInputStream bufferedInputStream = file.newInputStream()

ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()

PreProcessor.process(bufferedInputStream, byteArrayOutputStream)

def result = byteArrayOutputStream.toString()

'nop'