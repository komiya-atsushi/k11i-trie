package biz.k11i.trie;

public interface Traversable<T> {
    public static final char TERM_CH = 0xfffe;

    public static interface Node {
        // this is marker interface
    }

    /**
     * ���[�g�m�[�h��ԋp���܂��B
     * 
     * @return
     */
    Node getRoot();

    /**
     * �w�肳�ꂽ�m�[�h�ɂԂ牺����q�m�[�h��ԋp���܂��B
     * 
     * @param node
     * @return
     */
    Iterable<Node> getChildren(Node node);

    /**
     * �w�肳�ꂽ�m�[�h���H���Ach �̕����ɑΉ�����q�m�[�h��ԋp���܂��B
     * 
     * @param node
     * @param ch
     * @return
     */
    Node getChild(Node node, char ch);

    /**
     * �w�肳�ꂽ�m�[�h�̐e�ɓ�����m�[�h��ԋp���܂��B
     * 
     * @param node
     * @return
     */
    Node getParent(Node node);

    /**
     * �w�肳�ꂽ�m�[�h�ɕR�Â��t������ԋp���܂��B
     * 
     * @param node
     * @return
     */
    T getObject(Node node);

    /**
     * �w�肳�ꂽ�m�[�h�ɕt������R�Â��܂��B
     * 
     * @param node
     * @param obj
     */
    void setObject(Node node, T obj);
}
