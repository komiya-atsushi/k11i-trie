package biz.k11i.trie.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 整列されたパターンのリスト上における、深さ (パターン上の文字を指し示すインデクス) を含んだ特定の区間を表します。
 * 
 * @author komiya
 */
class Range implements Iterable<Integer> {
    /** 区間の開始位置を保持します。 */
    private int first;

    /** 区間の終端位置を保持します。実際の区間は、[first, end) となります。 */
    private int last;

    /** 深さを保持します。 */
    private int depth;

    /**
     * 深さ depth で [first, end) の区間を表す Range オブジェクトを生成します。
     * 
     * @param first
     *            区間の開始位置
     * @param last
     *            区間の終端位置
     * @param depth
     *            深さ
     */
    Range(int first, int last, int depth) {
        if (first >= last) {
            throw new IllegalArgumentException(
                    "'first' must be less than 'last'");
        }

        this.first = first;
        this.last = last;
        this.depth = depth;
    }

    /**
     * 区間の開始位置を返却します。
     * 
     * @return
     */
    int getFirst() {
        return first;
    }

    /**
     * 区間の終端位置を返却します。
     * 
     * @return
     */
    int getLast() {
        return last;
    }

    /**
     * この区間の深さを取得します。
     * 
     * @return
     */
    int getDepth() {
        return depth;
    }

    /**
     * 区間内の数値を昇順に列挙する Iterator オブジェクトを生成し、返却します。
     * 
     * @return
     */
    @Override
    public Iterator<Integer> iterator() {
        return new IteratorImpl();
    }

    /**
     * iterator() の呼び出しで返却される Iterator インタフェースの実装クラスです。
     * <p>
     * このクラスの区間の数値を、昇順に列挙します。
     * </p>
     * 
     * @author komiya
     */
    private class IteratorImpl implements Iterator<Integer> {
        /** 次の next() の呼び出しで返却すべき値を保持します。 */
        private int currentValue = first;

        @Override
        public boolean hasNext() {
            if (currentValue < last) {
                return true;
            }

            return false;
        }

        @Override
        public Integer next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            int result = currentValue;
            currentValue++;

            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
