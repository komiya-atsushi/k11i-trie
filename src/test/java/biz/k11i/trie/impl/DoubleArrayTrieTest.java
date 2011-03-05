package biz.k11i.trie.impl;

import biz.k11i.trie.Trie;

/**
 * DoubleArrayTrie の単体テストを実施します。
 * 
 * @author komiya
 */
public class DoubleArrayTrieTest extends TrieTestBase {

    @Override
    protected Trie create() {
        return new DoubleArrayTrie();
    }
}
