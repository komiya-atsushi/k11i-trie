package biz.k11i.trie.impl.doublearray;

/**
 * Double Array における文字とコードの対応関係を表します。
 * 
 * @author komiya
 */
public class CharCodeTable {
    /** 文字からコードへ変換するためのテーブル */
    private char[] charToCodeTable;

    /** コードから文字を復元するためのテーブル */
    private char[] codeToCharTable;

    /** 利用されている文字数 */
    private int usedCharCount;

    CharCodeTable(char[] charToCodeTable, int usedCharCount) {
        this.charToCodeTable = charToCodeTable;
        this.usedCharCount = usedCharCount;

        codeToCharTable = new char[Character.MAX_VALUE + 1];
        for (int i = 0; i < charToCodeTable.length; i++) {
            codeToCharTable[codeToCharTable[i]] = (char) i;
        }
    }

    /**
     * 指定された文字をコードに変換し、その結果を返却します。
     * 
     * @param ch
     * @return
     */
    public char lookupCharToCode(char ch) {
        return charToCodeTable[ch];
    }

    /**
     * 指定されたコードを文字に変換し、その結果を返却します。
     * 
     * @param code
     * @return
     */
    public char lookupCodeToChar(char code) {
        return codeToCharTable[code];
    }

    /**
     * 利用されている文字の種類数を返却します。
     * 
     * @return
     */
    public int getUsedCharCount() {
        return usedCharCount;
    }
    
    public CodeSet createEmptyCodeSet(int beginIndex) {
        return new CodeSet(this, beginIndex);
    }
}
