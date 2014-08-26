import com.initvoid.jconfig.PreProcessor

def url = 'https://raw.githubusercontent.com/torvalds/linux/master/arch/x86/Kconfig'.toURL()
def file = new File('/Users/jharth/IdeaProjects/kconfig-to-xml/src/main/script/res/arch_mips_Kconfig.cache')
//Reader reader = url.newReader()
Reader reader = file.newReader()

StringWriter writer = new StringWriter()

PreProcessor.process(reader, writer)

def result = writer.toString()

'nop'