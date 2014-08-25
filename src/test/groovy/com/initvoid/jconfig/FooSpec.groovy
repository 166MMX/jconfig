package com.initvoid.jconfig

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class FooSpec extends Specification
{
    @Shared
    List<String> validInput    = []
    @Shared
    List<String> validOutput   = []
    @Shared
    List<String> invalidInput  = []
    @Shared
    List<String> invalidOutput = []

    void addValidSpec(String input, String output)
    {
        validInput   << input
        validOutput  << output
    }

    void addInvalidSpec(String input, String output)
    {
        invalidInput   << input
        invalidOutput  << output
    }

    void setupSpec()
    {
//        addValidSpec("""choice
//\tprompt "Board support"
//\tdepends on BCM63XX
//\tdefault BOARD_BCM963XX
//
//config BOARD_BCM963XX
//       bool "Generic Broadcom 963xx boards"
//\tselect SSB
//       help
//         asdasd asd
//
//endchoice
//""", """choice
//\tprompt "Board support"
//\tdepends on BCM63XX
//\tdefault BOARD_BCM963XX
//
//config BOARD_BCM963XX
//       bool "Generic Broadcom 963xx boards"
//\tselect SSB
//       help
//\u001F         asdasd asd\u001F
//
//endchoice
//""")
//        addValidSpec("""choice
//\tprompt "Board support"
//\tdepends on BCM63XX
//\tdefault BOARD_BCM963XX
//
//config BOARD_BCM963XX
//       bool "Generic Broadcom 963xx boards"
//\tselect SSB
//       help
//
//endchoice
//""","""choice
//\tprompt "Board support"
//\tdepends on BCM63XX
//\tdefault BOARD_BCM963XX
//
//config BOARD_BCM963XX
//       bool "Generic Broadcom 963xx boards"
//\tselect SSB
//       help
//\u001F\u001F
//endchoice
//""")
//        addValidSpec("""if SOC_SAM_V7
//config SOC_SAMA5D3
//\tbool "SAMA5D3 family"
//\tselect SOC_SAMA5
//\tselect HAVE_FB_ATMEL
//\tselect HAVE_AT91_DBGU1
//\tselect HAVE_AT91_UTMI
//\tselect HAVE_AT91_SMD
//\tselect HAVE_AT91_USB_CLK
//\thelp
//\t  Select this if you are using one of Atmel's SAMA5D3 family SoC.
//\t  This support covers SAMA5D31, SAMA5D33, SAMA5D34, SAMA5D35, SAMA5D36.
//endif
//""","""if SOC_SAM_V7
//config SOC_SAMA5D3
//\tbool "SAMA5D3 family"
//\tselect SOC_SAMA5
//\tselect HAVE_FB_ATMEL
//\tselect HAVE_AT91_DBGU1
//\tselect HAVE_AT91_UTMI
//\tselect HAVE_AT91_SMD
//\tselect HAVE_AT91_USB_CLK
//\thelp
//\u001F\t  Select this if you are using one of Atmel's SAMA5D3 family SoC.
//\t  This support covers SAMA5D31, SAMA5D33, SAMA5D34, SAMA5D35, SAMA5D36.\u001F
//endif
//""")
        addValidSpec("""choice
\tprompt "Alpha system type"
\tdefault ALPHA_GENERIC
\t---help---
\t  This is the system type of your hardware.  A "generic" kernel will
\t  run on any supported Alpha system. However, if you configure a
\t  kernel for your specific system, it will be faster and smaller.

\t  To find out what type of Alpha system you have, you may want to
\t  check out the Linux/Alpha FAQ, accessible on the WWW from
\t  <http://www.alphalinux.org/>. In summary:

\t  Alcor/Alpha-XLT     AS 600, AS 500, XL-300, XL-366
\t  Alpha-XL            XL-233, XL-266
\t  AlphaBook1          Alpha laptop
\t  Avanti              AS 200, AS 205, AS 250, AS 255, AS 300, AS 400
\t  Cabriolet           AlphaPC64, AlphaPCI64
\t  DP264               DP264 / DS20 / ES40 / DS10 / DS10L
\t  EB164               EB164 21164 evaluation board
\t  EB64+               EB64+ 21064 evaluation board
\t  EB66                EB66 21066 evaluation board
\t  EB66+               EB66+ 21066 evaluation board
\t  Jensen              DECpc 150, DEC 2000 models 300, 500
\t  LX164               AlphaPC164-LX
\t  Lynx                AS 2100A
\t  Miata               Personal Workstation 433/500/600 a/au
\t  Marvel              AlphaServer ES47 / ES80 / GS1280
\t  Mikasa              AS 1000
\t  Noname              AXPpci33, UDB (Multia)
\t  Noritake            AS 1000A, AS 600A, AS 800
\t  PC164               AlphaPC164
\t  Rawhide             AS 1200, AS 4000, AS 4100
\t  Ruffian             RPX164-2, AlphaPC164-UX, AlphaPC164-BX
\t  SX164               AlphaPC164-SX
\t  Sable               AS 2000, AS 2100
\t  Shark               DS 20L
\t  Takara              Takara (OEM)
\t  Titan               AlphaServer ES45 / DS25 / DS15
\t  Wildfire            AlphaServer GS 40/80/160/320

\t  If you don't know what to do, choose "generic".

config ALPHA_GENERIC
\tbool "Generic"
\tdepends on TTY
""","""choice
\tprompt "Alpha system type"
\tdefault ALPHA_GENERIC
\t   help\u0020\u0020\u0020
\u001F\t  This is the system type of your hardware.  A "generic" kernel will
\t  run on any supported Alpha system. However, if you configure a
\t  kernel for your specific system, it will be faster and smaller.

\t  To find out what type of Alpha system you have, you may want to
\t  check out the Linux/Alpha FAQ, accessible on the WWW from
\t  <http://www.alphalinux.org/>. In summary:

\t  Alcor/Alpha-XLT     AS 600, AS 500, XL-300, XL-366
\t  Alpha-XL            XL-233, XL-266
\t  AlphaBook1          Alpha laptop
\t  Avanti              AS 200, AS 205, AS 250, AS 255, AS 300, AS 400
\t  Cabriolet           AlphaPC64, AlphaPCI64
\t  DP264               DP264 / DS20 / ES40 / DS10 / DS10L
\t  EB164               EB164 21164 evaluation board
\t  EB64+               EB64+ 21064 evaluation board
\t  EB66                EB66 21066 evaluation board
\t  EB66+               EB66+ 21066 evaluation board
\t  Jensen              DECpc 150, DEC 2000 models 300, 500
\t  LX164               AlphaPC164-LX
\t  Lynx                AS 2100A
\t  Miata               Personal Workstation 433/500/600 a/au
\t  Marvel              AlphaServer ES47 / ES80 / GS1280
\t  Mikasa              AS 1000
\t  Noname              AXPpci33, UDB (Multia)
\t  Noritake            AS 1000A, AS 600A, AS 800
\t  PC164               AlphaPC164
\t  Rawhide             AS 1200, AS 4000, AS 4100
\t  Ruffian             RPX164-2, AlphaPC164-UX, AlphaPC164-BX
\t  SX164               AlphaPC164-SX
\t  Sable               AS 2000, AS 2100
\t  Shark               DS 20L
\t  Takara              Takara (OEM)
\t  Titan               AlphaServer ES45 / DS25 / DS15
\t  Wildfire            AlphaServer GS 40/80/160/320

\t  If you don't know what to do, choose "generic".\u001F

config ALPHA_GENERIC
\tbool "Generic"
\tdepends on TTY
""")
        addValidSpec("""config ARCH_HIX5HD2
\tbool "Hisilicon X5HD2 family" if ARCH_MULTI_V7
\tselect CACHE_L2X0
\tselect HAVE_ARM_SCU if SMP
\tselect HAVE_ARM_TWD if SMP
\tselect PINCTRL
\tselect PINCTRL_SINGLE
\thelp
\t  Support for Hisilicon HIX5HD2 SoC family
endmenu""","""config ARCH_HIX5HD2
\tbool "Hisilicon X5HD2 family" if ARCH_MULTI_V7
\tselect CACHE_L2X0
\tselect HAVE_ARM_SCU if SMP
\tselect HAVE_ARM_TWD if SMP
\tselect PINCTRL
\tselect PINCTRL_SINGLE
\thelp
\u001F\t  Support for Hisilicon HIX5HD2 SoC family\u001F
endmenu
""")
    }

    @Unroll
    def "blue pill"()
    {
        def writer = new StringWriter()
        def reader = new StringReader(input)

        PreProcessor.process(reader, writer)

        def result = writer.toString()

        expect:
        result == output

        where:
        input   << getValidInput()
        output  << getValidOutput()
    }

    @Unroll
    def "red pill"()
    {
        def writer = new StringWriter()
        def reader = new StringReader(input)

        PreProcessor.process(reader, writer)

        def result = writer.toString()

        expect:
        result != output

        where:
        input   << getInvalidInput()
        output  << getInvalidOutput()
    }
}
