package biz.k11i.trie.impl.doublearray;

import java.util.Collection;

public class BasicCharCodeTableBuilder implements CharCodeTableBuilder {

    @Override
    public CharCodeTable build(Collection<String> patterns) {
        char[] table = new char[Character.MAX_VALUE + 1];

        // 使われている文字を抽出する
        for (String pat : patterns) {
            for (int j = 0; j < pat.length(); j++) {
                char ch = pat.charAt(j);
                table[ch] = 1;
            }
        }

        // 使われている文字に対して、1 から順にコードを割り当てる
        char seqNum = 1;
        int usedCharCount = 0;
        for (int i = 0; i < Character.MAX_VALUE; i++) {
            if (table[i] > 0) {
                table[i] = seqNum;
                seqNum++;
                usedCharCount++;

            } else {
                table[i] = Character.MAX_VALUE;
            }
        }

        // 文字列終端を表す文字には、常にコード 0 を割り当てる
        table[Character.MAX_VALUE] = 0;

        CharCodeTable result = new CharCodeTable(table, usedCharCount);
        
        return result;
    }

}
