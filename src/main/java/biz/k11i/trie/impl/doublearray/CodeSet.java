package biz.k11i.trie.impl.doublearray;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author komiya
 */
public class CodeSet {
    /** �o�������R�[�h�����̃��X�g�ɋL�^����܂��B */
    private List<Code> codes = new ArrayList<Code>();

    /** �Ō�ɏo�����������̏�񂪕ێ�����܂��B */
    private Code lastCode;

    /** �Ō�� addCodeWithIndex() ���Ă΂ꂽ�Ƃ��� currentIndex �̒l���ێ�����܂��B */
    private int lastIndex = -1;

    private CharCodeTable table;

    CodeSet(CharCodeTable table, int beginIndex) {
        if (beginIndex < 0) {
            throw new IllegalArgumentException("'beginIndex' must be >= 0");
        }

        this.table = table;
        lastIndex = beginIndex;
    }

    /**
     * �w�肳�ꂽ���������̕����W���ɒǉ����܂��B
     * 
     * @param ch
     *            ����
     */
    public void add(char ch) {
        char code = table.lookupCharToCode(ch);

        if (lastCode == null) {
            lastCode = new Code(code, lastIndex);
            codes.add(lastCode);

        } else if (lastCode.getCode() != code) {
            lastCode.setEndIndex(lastIndex);

            Code newCode = new Code(code, lastIndex);
            codes.add(newCode);

            lastCode = newCode;
        }

        lastIndex++;
    }

    /**
     * �����o�����m�F���鏈���̃N���[�W���O�����܂��B
     */
    public void finish() {
        if (lastCode != null) {
            lastCode.setEndIndex(lastIndex);
            lastCode = null;
        }
    }

    /**
     * ���̕����W���ɕ�����������݂��Ȃ��ꍇ�Atrue ��ԋp���܂��B
     * 
     * @return
     */
    public boolean isEmpty() {
        return codes.isEmpty();
    }

    /**
     * �R�[�h���̈ꗗ��ԋp���܂��B
     * 
     * @return
     */
    public List<Code> getCodes() {
        return codes;
    }

    /**
     * Character �I�u�W�F�N�g�̈ꗗ�Ƃ��āA�R�[�h����ԋp���܂��B
     * 
     * @return
     */
    public List<Character> getCodesAsCharacterList() {
        List<Character> result = new ArrayList<Character>();

        for (Code code : codes) {
            result.add(code.getCode());
        }

        return result;
    }

    /**
     * 
     * 
     * @author komiya
     */
    public static class Code {
        /** �����ɑΉ�����R�[�h */
        private char code;

        /** �p�^�[���z���ł́A���̕����̏o���J�n�ʒu�B������̐[���Ɉˑ����܂��B */
        private int beginIndex;

        /** �p�^�[���z���ŁA���̕������o�����Ȃ��Ȃ�ʒu�B */
        private int endIndex;

        /**
         * 
         * @param code
         * @param beginIndex
         */
        private Code(char code, int beginIndex) {
            this.code = code;
            this.beginIndex = beginIndex;
        }

        public char getCode() {
            return code;
        }

        public int getBeginIndex() {
            return beginIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        private void setEndIndex(int endIndex) {
            this.endIndex = endIndex;
        }
    }

}
