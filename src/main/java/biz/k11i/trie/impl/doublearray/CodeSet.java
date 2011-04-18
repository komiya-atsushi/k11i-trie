package biz.k11i.trie.impl.doublearray;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author komiya
 */
public class CodeSet {
    /** 出現したコードがこのリストに記録されます。 */
    private List<Code> codes = new ArrayList<Code>();

    /** 最後に出現した文字の情報が保持されます。 */
    private Code lastCode;

    /** 最後に addCodeWithIndex() が呼ばれたときの currentIndex の値が保持されます。 */
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
     * 指定された文字をこの文字集合に追加します。
     * 
     * @param ch
     *            文字
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
     * 文字出現を確認する処理のクロージングをします。
     */
    public void finish() {
        if (lastCode != null) {
            lastCode.setEndIndex(lastIndex);
            lastCode = null;
        }
    }

    /**
     * この文字集合に文字が一つも存在しない場合、true を返却します。
     * 
     * @return
     */
    public boolean isEmpty() {
        return codes.isEmpty();
    }

    /**
     * コード情報の一覧を返却します。
     * 
     * @return
     */
    public List<Code> getCodes() {
        return codes;
    }

    /**
     * Character オブジェクトの一覧として、コード情報を返却します。
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
        /** 文字に対応するコード */
        private char code;

        /** パターン配列上での、この文字の出現開始位置。文字列の深さに依存します。 */
        private int beginIndex;

        /** パターン配列上で、この文字が出現しなくなる位置。 */
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
