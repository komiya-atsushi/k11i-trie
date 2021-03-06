package biz.k11i.trie.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Stack;

import biz.k11i.trie.Trie;

public class DoubleArrayTrie implements Trie {
    /**
     * Double-Array を表します。
     * 
     * @author komiya
     */
    private static class DoubleArray {
        /** 要素が何も設定されてないことを表す値です */
        private static final int NOT_ENTRY = -1;

        /**
         * 子ノードと親ノードの関係を確かめるために利用します。
         * <p>
         * この配列に設定されている 0 以上の値が、有効な要素となります。<br />
         * 有効な要素が存在しない場合には、NOT_ENTRY を含む負数が設定されています。
         * </p>
         */
        private List<Integer> check;

        /**
         * 子ノードが配置されている、Double-Array 上の開始インデクスを表します。
         * <p>
         * この配列の有効な要素が取りえる値は、-Character.MAX_VALUE から base.size() までの値です。<br />
         * -Character.MAX_VALUE 未満の値は、その利用が予約されています。
         * </p>
         */
        private List<Integer> base;

        /**
         * Double-Array オブジェクトを生成します。
         */
        DoubleArray() {
            clear();
        }

        /**
         * この Double-Array を空にします。
         */
        void clear() {
            check = new ArrayList<Integer>();
            base = new ArrayList<Integer>();

            check.add(0);
            base.add(1);
        }

        /**
         * 指定された文字の一覧を、Double-Array 上に配置します。
         * 
         * @param parentIndex
         *            子ノードの CHECK に設定される、親ノードのインデクスを指定します。
         * @param codes
         *            (コンパクトに表現された) 文字の一覧を表します。
         * @return 子ノードが配置されている開始インデクスが返却されます。
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

            // 文字の配置が可能な空き領域を探しだす
            int baseIndex = findSpace(codes);

            // baseIndex 自体は空いていなくてもＯＫ
            base.set(parentIndex, baseIndex);

            for (Character code : codes) {
                int childIndex = baseIndex + code.charValue();

                // この状態では check 配列のみ、設定しておく
                check.set(childIndex, parentIndex);
            }

            return baseIndex;
        }

        /**
         * 空き領域を探し出し、そのときの base の値を返却します。
         * 
         * @param chars
         * @return
         */
        private int findSpace(List<Character> chars) {
            char minCh = chars.get(0);
            char maxCh = chars.get(chars.size() - 1);

            // ナイーブな探索をする
            for (int i = 0; i < check.size(); i++) {
                boolean found = true;

                for (int j = 0; j < chars.size(); j++) {
                    char ch = chars.get(j);
                    int offset = ch - minCh;

                    int index = i + offset;
                    if (index >= check.size()) {
                        // 既存の空き領域が存在しない場合は、拡張をして見つかったことにする
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

            // 最後まで到達して見つからなければ、空き領域を作成して対処する
            int requiredSize = maxCh - minCh + 1;
            expandArrays(requiredSize);

            return check.size() - minCh - requiredSize;
        }

        /**
         * CHECK/BASE 配列の末尾にダミー要素を追加し、空き領域を作ります。
         * 
         * @param requiredEntryCount
         */
        private void expandArrays(int requiredEntryCount) {
            for (int i = 0; i < requiredEntryCount; i++) {
                check.add(NOT_ENTRY);
                base.add(0);
            }
        }

        /**
         * 指定された Double Array 上のインデクスより、code に対応する枝をたどります。
         * 
         * @param index
         * @param code
         * @return code に対応する次のインデクス。code に対応する枝が存在しない場合は、負数が返却されます。
         */
        int retrieve(int index, char code) {
            if (!isIndexInRange(index)) {
                throw new IllegalArgumentException(String.format(
                        "'index' must be 0 <= 'index ' < %d: %d", check.size(),
                        index));
            }

            int nextIndex = base.get(index) + code;
            if (!isIndexInRange(nextIndex)) {
                return -1; // TODO マジックナンバーの置き換えと、説明
            }

            if (check.get(nextIndex) != index) {
                return -1;
            }

            return nextIndex;
        }

        /**
         * 指定されたインデクスが Double Array の範囲内に収まっている場合に、true を返却します。
         * 
         * @param index
         * @return
         */
        private boolean isIndexInRange(int index) {
            if (0 <= index && index < check.size()) {
                return true;
            }

            return false;
        }

        /**
         * 指定された Double Array 上のインデクスから、終端文字への枝が伸びている場合に true を返却します。
         * 
         * @param index
         * @return
         */
        boolean hasTerminationCode(int index) {
            int nextIndex = retrieve(index, TERMINATION_CODE);
            if (nextIndex >= 0) {
                return true;
            }

            return false;
        }
    }

    /**
     * 構築処理の入力データであるパターン配列における区間を表現します。
     * 
     * @author komiya
     */
    private static class PatternArrayRange extends Range {
        /** 親ノードに対応する、Double-Array 上のインデクスを表します。 */
        private int parentDoubleArrayIndex;

        /**
         * このコンストラクタは、他のインナークラスに対して隠蔽されます。
         * <p>
         * [first, last) の区間に対応します。
         * </p>
         * 
         * @param first
         *            最初を表すインデクス。
         * @param last
         *            最後を表すインデクス。
         * @param depth
         *            パターンの文字の深さを指定します。
         */
        private PatternArrayRange(int first, int last, int depth) {
            super(first, last, depth);
        }

        /**
         * PatternArrayRange オブジェクトを生成します。
         * 
         * @param first
         *            最初を表すインデクス。
         * @param last
         *            最後を表すインデクス。
         * @param depth
         *            パターンの文字の深さを指定します。
         * @param arrayIndex
         *            親ノードに対応する、Double-Array 上のインデクスを指定します。
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
     * あるトライ上のノード以下に存在する文字の集合を構築するためのクラスです。
     * 
     * @author komiya
     */
    private static class CodeSet {
        /** 出現した文字がこのリストに記録されます。 */
        private List<CodeInfo> codes = new ArrayList<CodeInfo>();

        /** 最後に出現した文字の情報が保持されます。 */
        private CodeInfo lastCode;

        /** 最後に addCodeWithIndex() が呼ばれたときの currentIndex の値が保持されます。 */
        private int lastIndex = -1;

        CodeSet(int beginIndex) {
            if (beginIndex < 0) {
                throw new IllegalArgumentException("'beginIndex' must be >= 0");
            }

            lastIndex = beginIndex;
        }

        /**
         * 指定された文字をこの文字集合に追加します。
         * 
         * @param code
         *            文字
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
         * 文字出現を確認する処理のクロージングをします。
         */
        void finish() {
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
        boolean isEmpty() {
            return codes.isEmpty();
        }

        /**
         * 文字の一覧を返却します。
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
        /** 文字 */
        private char code;

        /** パターン配列上での、この文字の出現開始位置。文字列の深さに依存します。 */
        private int beginIndex;

        /** パターン配列上で、この文字が出現しなくなる位置。 */
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

    /** 終端を表す Double Array 内での文字表現です */
    private static final char TERMINATION_CODE = '\u0000';

    /** Double-Array を保持します */
    private DoubleArray doubleArray = new DoubleArray();

    /** UTF-16 文字から Double Array 内部における文字表現（コード）へ変換するためのテーブル */
    private char[] codeTable;

    /** 利用されている文字数 */
    private int numUsedChars;

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
     * ソート済みのパターン配列を元に、Double-Array を用いた Trie を構築します。
     * 
     * @param patterns
     */
    public void buildFromSortedPatterns(List<String> patterns) {
        char[] codeTable = buildCharToCodeTable(patterns);
        buildTrieBreadthFirst(patterns, codeTable);

        this.codeTable = codeTable;
    }

    /**
     * すべてのパターンを１文字ずつ走査し、文字からコードへの変換を行うテーブルを構築します。
     * 
     * @param patterns
     * @return
     */
    private char[] buildCharToCodeTable(Iterable<String> patterns) {
        // TODO いずれはこの処理で、頻度順に並び替えるようにする (int[])
        char[] result = new char[Character.MAX_VALUE + 1];

        for (String pat : patterns) {
            for (int j = 0; j < pat.length(); j++) {
                char ch = pat.charAt(j);
                result[ch] = 1;
            }
        }

        // 順にコードを割り当てる
        char seqNum = 1;
        for (int i = 0; i < result.length; i++) {
            if (result[i] > 0) {
                result[i] = seqNum;
                seqNum++;

            } else {
                result[i] = Character.MAX_VALUE;
            }
        }

        // 文字列終端を表す文字には、常にコード 0 を割り当てる
        result[Character.MAX_VALUE] = 0;

        return result;
    }

    private void buildTrieBreadthFirst(List<String> patterns, char[] codeTable) {
        Queue<PatternArrayRange> queue = new LinkedList<PatternArrayRange>();
        queue.add(new PatternArrayRange(0, patterns.size(), 0, 0));

        while (!queue.isEmpty()) {
            PatternArrayRange range = queue.poll();

            // TODO TAIL を構築するなら、ここで range が 1 しかないことを確認する

            int depth = range.getDepth();
            CodeSet codeSet = new CodeSet(range.getFirst());

            // 今の区間において、各パターンの depth 文字目にある文字を収集する
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

            // Double-Array に配置する
            int baseIndex = doubleArray.allocate(
                    range.getParentDoubleArrayIndex(),
                    codeSet.getAsCharacterList());

            // 区間の情報を更新し、待ち行列に順次追加する
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
                // コードに変換した結果が '\uffff' になる場合は、
                // 現在のノードにおいて、その文字が子供に存在しないことを表す
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
        return new AbstractIterable(text) {

            @Override
            Iterator<String> createIterator(DoubleArrayTrie trie, String pattern) {
                return new CommonPrefixSearcher(pattern, doubleArray, codeTable);
            }
        };
    }

    @Override
    public Iterable<String> searchPredictive(String prefix) {
        // TODO Auto-generated method stub
        return null;
    }

    private abstract class AbstractIterable implements Iterable<String> {
        private String pattern;

        AbstractIterable(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public Iterator<String> iterator() {
            return createIterator(DoubleArrayTrie.this, pattern);
        }

        abstract Iterator<String> createIterator(DoubleArrayTrie trie,
                String pattern);
    }

    /**
     * 共通接頭辞を検索・列挙する機能を提供します。
     * 
     * @author komiya
     */
    private static class CommonPrefixSearcher implements Iterator<String> {
        /** 探索対象文字列 */
        private String pattern;

        /** 探索対象文字列の操作位置 */
        private int patternIndex;

        /** Double Array で表現されたトライ */
        private DoubleArray doubleArray;

        private int doubleArrayIndex;

        /** Trie 内での文字表現（コード）から UTF-16 文字に変換するためのテーブル */
        private char[] codeTable;

        private String nextObject;

        CommonPrefixSearcher(String pattern, DoubleArray doubleArray,
                char[] codeTable) {
            this.pattern = pattern;
            this.doubleArray = doubleArray;
            this.codeTable = codeTable;
        }

        @Override
        public boolean hasNext() {
            if (nextObject != null) {
                return true;
            }

            while (patternIndex < pattern.length()) {

                // 文字に対応する Trie 内コードに変換する
                char code = codeTable[pattern.charAt(patternIndex)];
                if (code == Character.MAX_VALUE) {
                    // TODO Character.MAX_VALUE を別の定数に置き換えたい。
                    finish();
                    break;
                }

                // Double Array の走査位置を更新する
                doubleArrayIndex = doubleArray.retrieve(doubleArrayIndex, code);
                if (doubleArrayIndex < 0) {
                    finish();
                    break;
                }

                patternIndex++;
                if (doubleArray.hasTerminationCode(doubleArrayIndex)) {
                    nextObject = pattern.substring(0, patternIndex);
                    return true;
                }
            }

            return false;
        }

        private void finish() {
            patternIndex = pattern.length();
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
            throw new UnsupportedOperationException();
        }
    }
    
    private static class DoubleArrayNode {
        private int index;
        
        private char code;
        
        DoubleArrayNode(int index, char code) {
            this.index = index;
            this.code = code;
        }
    }
    
    private class NodeIterator implements Iterator<DoubleArrayNode> {
        private int parentIndex;

        private char currentCode;

        private DoubleArrayNode nextObject;

        NodeIterator(int parentIndex) {
            this.parentIndex = parentIndex;
        }

        @Override
        public boolean hasNext() {
            if (nextObject != null) {
                return true;
            }

//            int childIndex = -1;
//            while (currentCode < numUsedChars
//                    && (childIndex = doubleArray.retrieve(parentIndex,
//                            currentCode)) < 0) {
//                currentCode++;
//            }
//
//            if (childIndex >= 0) {
//
//            }
            
            return false;
        }

        @Override
        public DoubleArrayNode next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            DoubleArrayNode result = nextObject;
            nextObject = null;

            return result;
        }

        @Override
        public void remove() {
            throw new NoSuchElementException();
        }
    }

    private class PredictiveSearcher implements Iterator<String> {
        private String pattern;

        private Stack<Iterator<Integer>> indexes = new Stack<Iterator<Integer>>();

        private Stack<Character> chars = new Stack<Character>();

        private String nextObject;

        PredictiveSearcher(String pattern) {
            this.pattern = pattern;
        }

        private void prepare() {
            // exact match をここで実施する
        }

        @Override
        public boolean hasNext() {
            if (nextObject != null) {
                return true;
            }

            prepare();

            while (!indexes.isEmpty()) {
                Iterator<Integer> iter = indexes.pop();
                if (!iter.hasNext()) {
                    chars.pop();
                    continue;
                }

                Integer index = iter.next();
                indexes.push(iter);

                // TODO ここで文字終端のチェックをする
                // Iterator<Integer> childrenIter = ;

            }

            return false;
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
            throw new UnsupportedOperationException();
        }
    }
}
