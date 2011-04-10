package biz.k11i.trie;

/**
 * 文字列を保持する Trie です。
 * <p>
 * Trie が提供すべき機能とその詳細について、定義します。
 * </p>
 * 
 * @author komiya
 */
public interface Trie {
    /**
     * 指定された文字列の集合をもとに、Trie を構築します。
     * <p>
     * このメソッドを呼び出された StringTrie オブジェクトは、すでに保持している文字列を破棄し、 与えられた文字列の集合のみからなる Trie
     * を新たに構築します。
     * </p>
     * <p>
     * このインタフェースを実装するサブクラスは、このメソッドを必ず提供する必要があります。
     * </p>
     * 
     * @param patterns
     *            文字列の集合
     */
    void build(Iterable<String> patterns);

    /**
     * この Trie が保持している文字列をすべて破棄します。
     * <p>
     * このインタフェースを実装するサブクラスは、このメソッドを必ず提供する必要があります。
     * </p>
     */
    void clear();

    /**
     * この Trie に、指定された文字列の集合を新たに加えます。
     * <p>
     * すでに Trie 内に存在する文字列はそのままに、与えられた文字列すべてを Trie に追加します。
     * </p>
     * 
     * @param patterns
     *            文字列の集合
     */
    void addAll(Iterable<String> patterns);

    /**
     * 指定された文字列を、この Trie に追加します。
     * 
     * @param pattern
     *            この Trie に追加する文字列
     */
    void add(String pattern);

    /**
     * 指定された文字列が、この Trie に含まれる場合に true を返却します。
     * 
     * @param pattern
     * @return
     */
    boolean contains(String pattern);

    /**
     * 指定されたテキストの接頭辞に一致する、Trie の中のすべての文字列を列挙します。
     * 
     * @param text
     *            テキスト
     * @return テキストの接頭辞に一致する文字列の集合
     */
    Iterable<String> searchPrefixOf(String text);

    /**
     * 指定された文字列を接頭辞としてもつ、Trie の中のすべての文字列を列挙します。
     * 
     * @param prefix
     *            接頭辞
     * @return
     */
    Iterable<String> searchPredictive(String prefix);
}
