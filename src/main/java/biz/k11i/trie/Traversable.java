package biz.k11i.trie;

public interface Traversable<T> {
    public static final char TERM_CH = 0xfffe;

    public static interface Node {
        // this is marker interface
    }

    /**
     * ルートノードを返却します。
     * 
     * @return
     */
    Node getRoot();

    /**
     * 指定されたノードにぶら下がる子ノードを返却します。
     * 
     * @param node
     * @return
     */
    Iterable<Node> getChildren(Node node);

    /**
     * 指定されたノードより辿れる、ch の文字に対応する子ノードを返却します。
     * 
     * @param node
     * @param ch
     * @return
     */
    Node getChild(Node node, char ch);

    /**
     * 指定されたノードの親に当たるノードを返却します。
     * 
     * @param node
     * @return
     */
    Node getParent(Node node);

    /**
     * 指定されたノードに紐づく付加情報を返却します。
     * 
     * @param node
     * @return
     */
    T getObject(Node node);

    /**
     * 指定されたノードに付加情報を紐づけます。
     * 
     * @param node
     * @param obj
     */
    void setObject(Node node, T obj);
}
