package biz.k11i.trie.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

import biz.k11i.trie.Trie;

public class DoubleArrayTrie implements Trie {
    /**
     * Double-Array ��\���܂��B
     * 
     * @author komiya
     */
    private static class DoubleArray {
        /** �v�f�������ݒ肳��ĂȂ����Ƃ�\���l�ł� */
        private static final int NOT_ENTRY = -1;

        /**
         * �q�m�[�h�Ɛe�m�[�h�̊֌W���m���߂邽�߂ɗ��p���܂��B
         * <p>
         * ���̔z��ɐݒ肳��Ă��� 0 �ȏ�̒l���A�L���ȗv�f�ƂȂ�܂��B<br />
         * �L���ȗv�f�����݂��Ȃ��ꍇ�ɂ́ANOT_ENTRY ���܂ޕ������ݒ肳��Ă��܂��B
         * </p>
         */
        private List<Integer> check;

        /**
         * �q�m�[�h���z�u����Ă���ADouble-Array ��̊J�n�C���f�N�X��\���܂��B
         * <p>
         * ���̔z��̗L���ȗv�f����肦��l�́A-Character.MAX_VALUE ���� base.size() �܂ł̒l�ł��B<br />
         * -Character.MAX_VALUE �����̒l�́A���̗��p���\�񂳂�Ă��܂��B
         * </p>
         */
        private List<Integer> base;

        /**
         * Double-Array �I�u�W�F�N�g�𐶐����܂��B
         */
        DoubleArray() {
            clear();
        }

        /**
         * ���� Double-Array ����ɂ��܂��B
         */
        void clear() {
            check = new ArrayList<Integer>();
            base = new ArrayList<Integer>();

            check.add(0);
            base.add(1);
        }

        /**
         * �w�肳�ꂽ�����̈ꗗ���ADouble-Array ��ɔz�u���܂��B
         * 
         * @param parentIndex
         *            �q�m�[�h�� CHECK �ɐݒ肳���A�e�m�[�h�̃C���f�N�X���w�肵�܂��B
         * @param codes
         *            (�R���p�N�g�ɕ\�����ꂽ) �����̈ꗗ��\���܂��B
         * @return �q�m�[�h���z�u����Ă���J�n�C���f�N�X���ԋp����܂��B
         */
        int allocate(int parentIndex, List<Character> codes) {
            if (parentIndex < 0 || check.size() <= parentIndex) {
                throw new IllegalArgumentException(String.format(
                        "'parentIndex' must be 0 <= parentIndex < %d : %d",
                        check.size(), parentIndex));
            }
            if (codes == null) {
                throw new NullPointerException("'codes' must not be null.");
            }
            if (codes.isEmpty()) {
                throw new IllegalArgumentException("'codes' must not be empty.");
            }

            // �����̔z�u���\�ȋ󂫗̈��T������
            int baseIndex = findSpace(codes);

            // baseIndex ���̂͋󂢂Ă��Ȃ��Ă��n�j
            base.set(parentIndex, baseIndex);

            for (Character code : codes) {
                int childIndex = baseIndex + code.charValue();

                // ���̏�Ԃł� check �z��̂݁A�ݒ肵�Ă���
                check.set(childIndex, parentIndex);
            }

            return baseIndex;
        }

        /**
         * �󂫗̈��T���o���A���̂Ƃ��� base �̒l��ԋp���܂��B
         * 
         * @param chars
         * @return
         */
        private int findSpace(List<Character> chars) {
            char minCh = chars.get(0);
            char maxCh = chars.get(chars.size() - 1);

            // �i�C�[�u�ȒT��������
            for (int i = 0; i < check.size(); i++) {
                boolean found = true;

                for (int j = 0; j < chars.size(); j++) {
                    char ch = chars.get(j);
                    int offset = ch - minCh;

                    int index = i + offset;
                    if (index >= check.size()) {
                        // �����̋󂫗̈悪���݂��Ȃ��ꍇ�́A�g�������Č����������Ƃɂ���
                        int requiredSize = (i + maxCh - minCh + 1)
                                - check.size();
                        expandArrays(requiredSize);
                        break;
                    }

                    if (check.get(index) != NOT_ENTRY) {
                        found = false;
                        break;
                    }
                }

                if (found) {
                    return i - minCh;
                }
            }

            // �Ō�܂œ��B���Č�����Ȃ���΁A�󂫗̈���쐬���đΏ�����
            int requiredSize = maxCh - minCh + 1;
            expandArrays(requiredSize);

            return check.size() - minCh - requiredSize;
        }

        /**
         * CHECK/BASE �z��̖����Ƀ_�~�[�v�f��ǉ����A�󂫗̈�����܂��B
         * 
         * @param requiredEntryCount
         */
        private void expandArrays(int requiredEntryCount) {
            for (int i = 0; i < requiredEntryCount; i++) {
                check.add(NOT_ENTRY);
                base.add(0);
            }
        }

        int retrieve(int index, char code) {
            if (!isIndexInRange(index)) {
                throw new IllegalArgumentException(String.format(
                        "'index' must be 0 <= 'index ' < %d: %d", check.size(),
                        index));
            }

            int nextIndex = base.get(index) + code;
            if (!isIndexInRange(nextIndex)) {
                return -1; // TODO �}�W�b�N�i���o�[�̒u�������ƁA����
            }

            if (check.get(nextIndex) != index) {
                return -1;
            }

            return nextIndex;
        }

        private boolean isIndexInRange(int index) {
            if (0 <= index && index < check.size()) {
                return true;
            }

            return false;
        }

        boolean hasTerminationCode(int currentIndex) {
            int nextIndex = retrieve(currentIndex, TERMINATION_CODE);
            if (nextIndex >= 0) {
                return true;
            }

            return false;
        }
    }

    /**
     * �\�z�����̓��̓f�[�^�ł���p�^�[���z��ɂ������Ԃ�\�����܂��B
     * 
     * @author komiya
     */
    private static class PatternArrayRange extends Range {
        /** �e�m�[�h�ɑΉ�����ADouble-Array ��̃C���f�N�X��\���܂��B */
        private int parentDoubleArrayIndex;

        /**
         * ���̃R���X�g���N�^�́A���̃C���i�[�N���X�ɑ΂��ĉB������܂��B
         * <p>
         * [first, last) �̋�ԂɑΉ����܂��B
         * </p>
         * 
         * @param first
         *            �ŏ���\���C���f�N�X�B
         * @param last
         *            �Ō��\���C���f�N�X�B
         * @param depth
         *            �p�^�[���̕����̐[�����w�肵�܂��B
         */
        private PatternArrayRange(int first, int last, int depth) {
            super(first, last, depth);
        }

        /**
         * PatternArrayRange �I�u�W�F�N�g�𐶐����܂��B
         * 
         * @param first
         *            �ŏ���\���C���f�N�X�B
         * @param last
         *            �Ō��\���C���f�N�X�B
         * @param depth
         *            �p�^�[���̕����̐[�����w�肵�܂��B
         * @param arrayIndex
         *            �e�m�[�h�ɑΉ�����ADouble-Array ��̃C���f�N�X���w�肵�܂��B
         */
        PatternArrayRange(int first, int last, int depth, int arrayIndex) {
            this(first, last, depth);
            this.parentDoubleArrayIndex = arrayIndex;
        }

        int getParentDoubleArrayIndex() {
            return parentDoubleArrayIndex;
        }
    }

    /**
     * ����g���C��̃m�[�h�ȉ��ɑ��݂��镶���̏W�����\�z���邽�߂̃N���X�ł��B
     * 
     * @author komiya
     */
    private static class CodeSet {
        /** �o���������������̃��X�g�ɋL�^����܂��B */
        private List<CodeInfo> codes = new ArrayList<CodeInfo>();

        /** �Ō�ɏo�����������̏�񂪕ێ�����܂��B */
        private CodeInfo lastCode;

        /** �Ō�� addCodeWithIndex() ���Ă΂ꂽ�Ƃ��� currentIndex �̒l���ێ�����܂��B */
        private int lastIndex = -1;

        CodeSet(int beginIndex) {
            if (beginIndex < 0) {
                throw new IllegalArgumentException("'beginIndex' must be >= 0");
            }

            lastIndex = beginIndex;
        }

        /**
         * �w�肳�ꂽ���������̕����W���ɒǉ����܂��B
         * 
         * @param code
         *            ����
         */
        void add(char code) {
            if (lastCode == null) {
                lastCode = new CodeInfo(code, lastIndex);
                codes.add(lastCode);

            } else if (lastCode.getCode() != code) {
                lastCode.setEndIndex(lastIndex);

                CodeInfo newCode = new CodeInfo(code, lastIndex);
                codes.add(newCode);

                lastCode = newCode;
            }

            lastIndex++;
        }

        /**
         * �����o�����m�F���鏈���̃N���[�W���O�����܂��B
         */
        void finish() {
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
        boolean isEmpty() {
            return codes.isEmpty();
        }

        /**
         * �����̈ꗗ��ԋp���܂��B
         * 
         * @return
         */
        List<CodeInfo> getCodes() {
            return codes;
        }

        List<Character> getAsCharacterList() {
            List<Character> result = new ArrayList<Character>();

            for (CodeInfo code : codes) {
                result.add(code.getCode());
            }

            return result;
        }
    }

    /**
     * 
     * 
     * @author komiya
     */
    private static class CodeInfo {
        /** ���� */
        private char code;

        /** �p�^�[���z���ł́A���̕����̏o���J�n�ʒu�B������̐[���Ɉˑ����܂��B */
        private int beginIndex;

        /** �p�^�[���z���ŁA���̕������o�����Ȃ��Ȃ�ʒu�B */
        private int endIndex;

        CodeInfo(char code, int beginIndex) {
            this.code = code;
            this.beginIndex = beginIndex;
        }

        char getCode() {
            return code;
        }

        int getBeginIndex() {
            return beginIndex;
        }

        int getEndIndex() {
            return endIndex;
        }

        void setEndIndex(int endIndex) {
            this.endIndex = endIndex;
        }
    }

    private static final char TERMINATION_CODE = 0;

    /** Double-Array ��ێ����܂� */
    private DoubleArray doubleArray = new DoubleArray();

    private char[] codeTable;

    @Override
    public void build(Iterable<String> patterns) {
        List<String> temp = new ArrayList<String>();
        for (String pat : patterns) {
            temp.add(pat);
        }

        Collections.sort(temp);

        buildFromSortedPatterns(temp);
    }

    /**
     * �\�[�g�ς݂̃p�^�[���z������ɁADouble-Array ��p���� Trie ���\�z���܂��B
     * 
     * @param patterns
     */
    public void buildFromSortedPatterns(List<String> patterns) {
        char[] codeTable = buildCharToCodeTable(patterns);
        buildTrieBreadthFirst(patterns, codeTable);

        this.codeTable = codeTable;
    }

    /**
     * ���ׂẴp�^�[�����P�������������A��������R�[�h�ւ̕ϊ����s���e�[�u�����\�z���܂��B
     * 
     * @param patterns
     * @return
     */
    private char[] buildCharToCodeTable(Iterable<String> patterns) {
        char[] result = new char[Character.MAX_VALUE + 1];

        for (String pat : patterns) {
            for (int j = 0; j < pat.length(); j++) {
                char ch = pat.charAt(j);
                result[ch] = 1;
            }
        }

        // ���ɃR�[�h�����蓖�Ă�
        char seqNum = 1;
        for (int i = 0; i < result.length; i++) {
            if (result[i] > 0) {
                result[i] = seqNum;
                seqNum++;

            } else {
                result[i] = Character.MAX_VALUE;
            }
        }

        // ������I�[��\�������ɂ́A��ɃR�[�h 0 �����蓖�Ă�
        result[Character.MAX_VALUE] = 0;

        return result;
    }

    private void buildTrieBreadthFirst(List<String> patterns, char[] codeTable) {
        Queue<PatternArrayRange> queue = new LinkedList<PatternArrayRange>();
        queue.add(new PatternArrayRange(0, patterns.size(), 0, 0));

        while (!queue.isEmpty()) {
            PatternArrayRange range = queue.poll();

            // TODO TAIL ���\�z����Ȃ�A������ range �� 1 �����Ȃ����Ƃ��m�F����

            int depth = range.getDepth();
            CodeSet codeSet = new CodeSet(range.getFirst());

            // ���̋�Ԃɂ����āA�e�p�^�[���� depth �����ڂɂ��镶�������W����
            for (int index : range) {
                String pat = patterns.get(index);

                if (depth == pat.length()) {
                    codeSet.add(TERMINATION_CODE);

                } else {
                    char ch = codeTable[pat.charAt(depth)];
                    codeSet.add(ch);
                }
            }

            codeSet.finish();

            if (codeSet.isEmpty()) {
                continue;
            }

            // Double-Array �ɔz�u����
            int baseIndex = doubleArray.allocate(
                    range.getParentDoubleArrayIndex(),
                    codeSet.getAsCharacterList());

            // ��Ԃ̏����X�V���A�҂��s��ɏ����ǉ�����
            List<CodeInfo> codes = codeSet.getCodes();
            for (int j = 0; j < codes.size(); j++) {
                CodeInfo code = codes.get(j);

                if (code.getCode() != TERMINATION_CODE) {
                    PatternArrayRange newRange = new PatternArrayRange(
                            code.getBeginIndex(), code.getEndIndex(),
                            depth + 1, baseIndex + code.getCode());
                    queue.add(newRange);
                }
            }
        }
    }

    @Override
    public void clear() {
        doubleArray.clear();
    }

    @Override
    public void addAll(Iterable<String> patterns) {
        throw new UnsupportedOperationException(
                "'DoubleArrayTrie#addAll() is not supported.");
    }

    @Override
    public void add(String pattern) {
        throw new UnsupportedOperationException(
                "'DoubleArrayTrie#add() is not supported.");
    }

    @Override
    public boolean contains(String pattern) {
        int currentIndex = 0;
        int nextIndex = 0;

        for (int i = 0; i < pattern.length(); i++) {
            char code = codeTable[pattern.charAt(i)];
            if (code == Character.MAX_VALUE) {
                // �R�[�h�ɕϊ��������ʂ� '\uffff' �ɂȂ�ꍇ�́A
                // ���݂̃m�[�h�ɂ����āA���̕������q���ɑ��݂��Ȃ����Ƃ�\��
                return false;
            }

            nextIndex = doubleArray.retrieve(currentIndex, code);
            if (nextIndex < 0) {
                return false;
            }

            currentIndex = nextIndex;
        }

        if (doubleArray.hasTerminationCode(currentIndex)) {
            return true;
        }

        return false;
    }

    @Override
    public Iterable<String> searchPrefixOf(String text) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterable<String> searchPredictive(String prefix) {
        // TODO Auto-generated method stub
        return null;
    }

    private abstract class IterableImpl implements Iterable<String> {
        private String pattern;

        IterableImpl(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public Iterator<String> iterator() {
            return createIterator(DoubleArrayTrie.this, pattern);
        }

        abstract Iterator<String> createIterator(DoubleArrayTrie trie,
                String pattern);
    }

    private static class CommonPrefixSearcher implements Iterator<String> {

        private String pattern;

        private int currentIndex;

        private DoubleArray doubleArray;

        private char[] charTable;

        private String nextObject;

        CommonPrefixSearcher(String pattern, DoubleArray doubleArray,
                char[] codeTable) {
            this.pattern = pattern;
            this.doubleArray = doubleArray;

            this.charTable = new char[65536];
            
            for (int i = 0 ; i < codeTable.length; i++) {
                charTable[codeTable[i]] = (char)i;
            }
        }

        @Override
        public boolean hasNext() {
            if (nextObject != null) {
                return true;
            }

            // TODO ���̋��ʐړ�����T���o��
            while (currentIndex < pattern.length() && nextObject != null) {

                // �R�[�h���當���ɋt�ϊ�����
                char code = charTable[pattern.charAt(currentIndex)];
                // TODO ���̂�����̎������K���B�Ȃ̂ŁA��Ō���������
                currentIndex++;
                if (doubleArray.hasTerminationCode(code)) {
                    nextObject = pattern.substring(0, currentIndex);
                    return true;

                } else {
                    // �J��Ԃ�
                    // TODO �ǂ��� retrieve ���Ăׂ΂����̂��A�m�F����
                }
            }
            
            return false;
            /*
             * if (currentIndex >= pattern.length()) { return false; }
             * 
             * // �R�[�h���當���ɋt�ϊ����� char ch =
             * charTable[pattern.charAt(currentIndex)]; if (ch == '\uffff') {
             * currentIndex = pattern.length(); return false; }
             * 
             * // TODO ���̂�����̎������K���B�Ȃ̂ŁA��Ō��������� currentIndex++; if
             * (doubleArray.hasTerminationCode(ch)) { nextObject =
             * pattern.substring(0, currentIndex); return true; } else { // �J��Ԃ�
             * }
             */
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            String result = nextObject;
            nextObject = null;

            return result;
        }

        @Override
        public void remove() {
            // TODO Auto-generated method stub

        }

    }
}
