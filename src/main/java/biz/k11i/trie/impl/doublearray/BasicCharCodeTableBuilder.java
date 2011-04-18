package biz.k11i.trie.impl.doublearray;

import java.util.Collection;

public class BasicCharCodeTableBuilder implements CharCodeTableBuilder {

    @Override
    public CharCodeTable build(Collection<String> patterns) {
        char[] table = new char[Character.MAX_VALUE + 1];

        // �g���Ă��镶���𒊏o����
        for (String pat : patterns) {
            for (int j = 0; j < pat.length(); j++) {
                char ch = pat.charAt(j);
                table[ch] = 1;
            }
        }

        // �g���Ă��镶���ɑ΂��āA1 ���珇�ɃR�[�h�����蓖�Ă�
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

        // ������I�[��\�������ɂ́A��ɃR�[�h 0 �����蓖�Ă�
        table[Character.MAX_VALUE] = 0;

        CharCodeTable result = new CharCodeTable(table, usedCharCount);
        
        return result;
    }

}
