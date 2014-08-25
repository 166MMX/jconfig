package com.initvoid.jconfig.model

enum SymbolFlag
{
    CONST                   (0x000001), // symbol is const
    CHECK                   (0x000008), // used during dependency checking

    CHOICE                  (0x000010), // start of a choice block (null name)
    CHOICE_VALUE            (0x000020), // used as a value in a choice block
    VALID                   (0x000080), // set when symbol.curr is calculated

    OPTIONAL                (0x000100), // choice is optional - values can be 'n'
    WRITE                   (0x000200), // write symbol to file (KCONFIG_CONFIG)
    CHANGED                 (0x000400), // ?

    AUTO                    (0x001000), // value from environment variable
    CHECKED                 (0x002000), // used during dependency checking
    WARNED                  (0x008000), // warning has been issued

    DEF                     (0x010000),  // First bit of SYMBOL_DEF */
    DEF_USER                (0x010000),  // symbol.def[S_DEF_USER] is valid */
    DEF_AUTO                (0x020000),  // symbol.def[S_DEF_AUTO] is valid */
    DEF3                    (0x040000),  // symbol.def[S_DEF_3] is valid */
    DEF4                    (0x080000),  // symbol.def[S_DEF_4] is valid */

    NEED_SET_CHOICE_VALUES  (0x100000),  // choice values need to be set before calculating this symbol value
    ALL_NO_CONFIG_Y         (0x200000)   // Set symbol to y if allnoconfig; used for symbols that hide others

    SymbolFlag(int value)
    {

    }
}