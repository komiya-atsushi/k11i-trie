package biz.k11i.trie.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Stack;

import biz.k11i.trie.Trie;

/**
 * 連結リストによる単純な Trie の実装です。
 * 
 * @author komiya
 */
public class ListTrie implements Trie {
    static class TrieNode {
        /** 文字列終端を表すノードです */
        private static final TrieNode TERMINATION_NODE;

        static {
            TERMINATION_NODE = new TrieNode('\ufffe');
            TERMINATION_NODE.children = Collections.emptyList();
        }

        /** このノードと親ノードの間のエッジに割り当てられた文字 */
        private char ch;

        /** 文字の昇順に並べられた子ノードの一覧 */
        private List<TrieNode> children = new LinkedList<TrieNode>();

        /**
         * エッジに割り当てられる文字を指定して、TrieNode オブジェクトを生成します。
         * 
         * @param ch
         */
        TrieNode(char ch) {
            this.ch = ch;
        }

        /**
         * このノードの配下に、指定された文字をエッジに割り当てた子ノードのオブジェクトを取得または生成し、返却します。
         * 
         * @param childChar
         * @return
         */
        TrieNode getOrCreateChild(char childChar) {
            for (ListIterator<TrieNode> i = children.listIterator(); i
                    .hasNext();) {
                TrieNode childNode = i.next();
                if (childChar == childNode.ch) {
                    return childNode;
                }

                if (childChar < childNode.ch) {
                    TrieNode result = new TrieNode(childChar);
                    i.previous();
                    i.add(result);
                    return result;
                }
            }

            TrieNode result = new TrieNode(childChar);
            children.add(result);

            return result;
        }

        /**
         * 指定された文字を持つ子ノードを探し出し、返却します。
         * 
         * @param childChar
         * @return
         */
        TrieNode findChild(char childChar) {
            for (TrieNode childNode : children) {
                if (childNode.ch == childChar) {
                    return childNode;
                }
            }

            return null;
        }

        /**
         * このノード配下のすべての子ノードを破棄します。
         */
        void clearChildren() {
            children.clear();
        }

        /**
         * このノード配下に、文字列終端を表すノードを追加します。
         */
        void addTermination() {
            if (!hasTermination()) {
                children.add(TERMINATION_NODE);
            }
        }

        /**
         * このノード配下に、文字列終端を表すノードが存在する場合に true を返却します。
         * 
         * @return
         */
        boolean hasTermination() {
            if (children.isEmpty()
                    || !TERMINATION_NODE
                            .equals(children.get(children.size() - 1))) {
                return false;
            }

            return true;
        }

        Iterator<TrieNode> iterateChildren() {
            return children.iterator();
        }

        char getCharacter() {
            return ch;
        }
    }

    /** この Trie におけるルートノード */
    private TrieNode root = new TrieNode('\uffff');

    @Override
    public void build(Iterable<String> patterns) {
        clear();
        addAll(patterns);
    }

    @Override
    public void clear() {
        root.clearChildren();
    }

    @Override
    public void addAll(Iterable<String> patterns) {
        for (String pattern : patterns) {
            add(pattern);
        }
    }

    @Override
    public void add(String pattern) {
        TrieNode parent = root;

        for (int i = 0; i < pattern.length(); i++) {
            char ch = pattern.charAt(i);
            TrieNode child = parent.getOrCreateChild(ch);
            parent = child;
        }

        parent.addTermination();
    }

    @Override
    public boolean contains(String pattern) {
        TrieNode exactMatchNode = findExactMatch(root, pattern);
        if (exactMatchNode.hasTermination()) {
            return true;
        }

        return false;
    }

    private static TrieNode findExactMatch(TrieNode root, String pattern) {
        TrieNode current = root;

        for (int i = 0; i < pattern.length() && current != null; i++) {
            char ch = pattern.charAt(i);
            current = current.getOrCreateChild(ch);
        }

        return current;
    }

    @Override
    public Iterable<String> searchPrefixOf(String text) {
        return new IterableImpl(text) {

            @Override
            Iterator<String> createIterator(TrieNode root, String text) {
                return new SearchPrefixOfIterator(root, text);
            }
        };
    }

    /**
     * search 系メソッドの戻り値となる Iterable オブジェクトの抽象クラスです。
     * <p>
     * Iterator で表現される探索戦略オブジェクトの生成に、Factory Method パターンを利用しています。
     * </p>
     * 
     * @author komiya
     */
    private abstract class IterableImpl implements Iterable<String> {
        private String text;

        IterableImpl(String text) {
            this.text = text;
        }

        @Override
        public Iterator<String> iterator() {
            return createIterator(root, text);
        }

        /**
         * 探索結果を列挙する Iterator オブジェクトを生成し、返却します。
         * 
         * @param root
         *            Trie のルートノード
         * @param text
         *            探索対象の文字列
         * @return
         */
        abstract Iterator<String> createIterator(TrieNode root, String text);
    }

    /**
     * 指定されたテキストの接頭辞に合致する文字列を Trie から探し出し、列挙を行う Iterator の実装クラスです。
     * 
     * @author komiya
     */
    private static class SearchPrefixOfIterator implements Iterator<String> {
        /** 操作対象のテキスト */
        private CharSequence text;

        /** 走査中の text の文字位置を表すインデクス */
        private int currentIndex;

        /** 走査中の Trie のノード */
        private TrieNode current;

        /** 次の next() メソッドの呼び出しで返却すべきオブジェクト */
        private String nextObject;

        SearchPrefixOfIterator(TrieNode root, CharSequence text) {
            this.current = root;
            this.text = text;
        }

        @Override
        public boolean hasNext() {
            if (nextObject != null) {
                return true;
            }

            while (currentIndex < text.length() && current != null) {
                char ch = text.charAt(currentIndex);
                currentIndex++;

                current = current.findChild(ch);
                if (current == null) {
                    return false;
                }

                if (current.hasTermination()) {
                    nextObject = text.subSequence(0, currentIndex).toString();
                    return true;
                }
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
            throw new UnsupportedOperationException(
                    "This method is not implemented.");
        }

    }

    @Override
    public Iterable<String> searchPredictive(String prefix) {
        return new IterableImpl(prefix) {

            @Override
            Iterator<String> createIterator(TrieNode root, String text) {
                return new PredictiveSearcher(root, text);
            }
        };
    }

    private static class PredictiveSearcher implements Iterator<String> {
        private TrieNode root;

        private String pattern;

        private boolean prepared;

        private Stack<Iterator<TrieNode>> nodes = new Stack<Iterator<TrieNode>>();;

        private Stack<Character> chars = new Stack<Character>();

        private String nextObject;

        PredictiveSearcher(TrieNode root, String pattern) {
            this.root = root;
            this.pattern = pattern;
        }

        /**
         * 候補探索を始めるための準備を行います。
         */
        private void prepare() {
            if (prepared) {
                return;
            }

            prepared = true;

            TrieNode node = findExactMatch(root, pattern);
            if (node == null) {
                return;
            }

            nodes.push(Collections.singleton(node).iterator());
            chars.push('\uffff'); // ダミー値
        }

        @Override
        public boolean hasNext() {
            if (nextObject != null) {
                return true;
            }

            prepare();

            while (!nodes.isEmpty()) {
                Iterator<TrieNode> iter = nodes.pop();
                if (!iter.hasNext()) {
                    chars.pop();
                    continue;
                }

                TrieNode n = iter.next();
                nodes.push(iter);
                nodes.push(n.iterateChildren());
                chars.push(n.getCharacter());

                if (n.hasTermination()) {
                    StringBuilder sb = new StringBuilder(pattern);

                    for (int i = 2; i < chars.size(); i++) {
                        sb.append(chars.get(i));
                    }
                    nextObject = sb.toString();

                    return true;
                }
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
            // TODO Auto-generated method stub

        }

    }
}
