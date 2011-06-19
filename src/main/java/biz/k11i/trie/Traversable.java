package biz.k11i.trie;

import java.util.Iterator;
import java.util.NoSuchElementException;

public interface Traversable {
    final char TERMINATION_CODE = '\uffff';

    /**
     * 滞在先のノードをルートノードとします。
     * 
     * @return
     */
    void moveToRoot();

    /**
     * 現在滞在しているノードに遷移したときに辿った枝の文字を返却します。
     * <p>
     * </p>
     * 
     * @return
     */
    char getChar();

    /**
     * ルートノードから現在滞在しているノードに到達するまでに辿った枝の文字を連結し、返却します。
     * 
     * @return
     */
    String getCurrentString();

    /**
     * 現在滞在しているノードから、指定された文字に対応する枝を辿って子供のノードに遷移します。
     * 
     * @param ch
     * @return
     */
    boolean followChild(char ch);

    /**
     * 現在滞在しているノードから、最初の子供のノードに遷移します。
     * 
     * @return
     */
    boolean followFirstChild();

    /**
     * 現在滞在しているノードで終端する文字列が含まれる場合に、true を返却します。
     * 
     * @return
     */
    boolean isTerminated();

    /**
     * 現在滞在しているノードと親が同じ、次の兄弟ノードに遷移します。
     * <p>
     * 現在のノードが最後の兄弟ノードである場合にこのメソッドが呼び出されると、false を返却します。 それ以外の場合は true を返却します。
     * </p>
     * 
     * @return
     */
    boolean nextSibling();

    /**
     * 現在滞在しているノードから親のノードに戻ります。
     * <p>
     * ルートノードに戻った場合、false を返却します。それ以外の場合は true を返却します。
     * </p>
     * 
     * @return
     */
    boolean backtrack();
}

/**
 * 共通接頭辞探索の実装例。
 * 
 * @author komiya
 */
class CommonPrefixSearcher implements Iterator<String> {
    private Traversable traversable;

    private String pattern;

    private int charIndex;

    private String nextString;

    public CommonPrefixSearcher(Traversable traversable, String pattern) {
        this.traversable = traversable;
        this.pattern = pattern;
    }

    @Override
    public boolean hasNext() {
        if (nextString != null) {
            return true;
        }

        while (charIndex < pattern.length()) {
            char ch = pattern.charAt(charIndex);
            charIndex++;

            if (!traversable.followChild(ch)) {
                return false;
            }

            if (traversable.isTerminated()) {
                nextString = traversable.getCurrentString();
                break;
            }
        }

        return false;
    }

    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        String result = nextString;
        nextString = null;

        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

/**
 * 予測検索の実装例。
 * 
 * @author komiya
 */
class PredictiveSearcher implements Iterator<String> {
    private Traversable traversable;

    private String pattern;

    private int charIndex;

    private String nextString;

    private int depth = -1;

    PredictiveSearcher(Traversable traversable, String pattern) {
        this.traversable = traversable;
        this.pattern = pattern;
        prepare();
    }

    private void prepare() {
        // まずは完全一致検索をする
        while (charIndex < pattern.length()) {
            char ch = pattern.charAt(charIndex);
            charIndex++;

            if (!traversable.followChild(ch)) {
                // 一致するものが存在しない
                return;
            }
        }
        
        depth = 0;

        if (traversable.isTerminated()) {
            // exactMatch する文字列が存在するなら、それを次の返却値として用意しておく
            nextString = traversable.getCurrentString();
        }
    }

    @Override
    public boolean hasNext() {
        if (nextString != null) {
            return true;
        }

        while (succ()) {
            if (traversable.isTerminated()) {
                nextString = traversable.getCurrentString();
                return true;
            }
        }

        return false;
    }

    /**
     * 次の候補ノードに移動します。
     * 
     * @return
     */
    private boolean succ() {
        if (depth < 0) {
            return false;
        }
        
        // まずは子供のノードを辿ってみる
        if (traversable.followFirstChild()) {
            depth++;
            return true;
        }

        while (depth > 0) {
            // 子供ノードが存在しなければ、兄弟ノードを辿ってみる
            if (traversable.nextSibling()) {
                return true;
            }

            traversable.backtrack();
            depth--;
        }

        return false;
    }

    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        String result = nextString;
        nextString = null;

        return result;
    }

    @Override
    public void remove() {
        // TODO Auto-generated method stub

    }

}
