package biz.k11i.trie.impl.doublearray;

/**
 * Double Array �ɂ����镶���ƃR�[�h�̑Ή��֌W��\���܂��B
 * 
 * @author komiya
 */
public class CharCodeTable {
    /** ��������R�[�h�֕ϊ����邽�߂̃e�[�u�� */
    private char[] charToCodeTable;

    /** �R�[�h���當���𕜌����邽�߂̃e�[�u�� */
    private char[] codeToCharTable;

    /** ���p����Ă��镶���� */
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
     * �w�肳�ꂽ�������R�[�h�ɕϊ����A���̌��ʂ�ԋp���܂��B
     * 
     * @param ch
     * @return
     */
    public char lookupCharToCode(char ch) {
        return charToCodeTable[ch];
    }

    /**
     * �w�肳�ꂽ�R�[�h�𕶎��ɕϊ����A���̌��ʂ�ԋp���܂��B
     * 
     * @param code
     * @return
     */
    public char lookupCodeToChar(char code) {
        return codeToCharTable[code];
    }

    /**
     * ���p����Ă��镶���̎�ސ���ԋp���܂��B
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
