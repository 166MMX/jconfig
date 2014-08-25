import com.initvoid.jconfig.PreProcessor

def url = 'https://raw.githubusercontent.com/torvalds/linux/master/arch/x86/Kconfig'.toURL()
BufferedInputStream bufferedUrlInputStream = url.newInputStream()
//def file = new File('C:\\Users\\MMX\\IdeaProjects\\378b037ec0913cad7218\\src\\main\\script\\res\\arch_x86_Kconfig.cache')
//BufferedInputStream bufferedFileInputStream = file.newInputStream()

ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()

PreProcessor.process(bufferedUrlInputStream, byteArrayOutputStream)

def result = byteArrayOutputStream.toString()

'nop'