package biz.k11i.trie.impl;

import biz.k11i.trie.Trie;

/**
 * DoubleArrayTrie �̒P�̃e�X�g�����{���܂��B
 * 
 * @author komiya
 */
public class DoubleArrayTrieTest extends TrieTestBase {

    @Override
    protected Trie create() {
        return new DoubleArrayTrie();
    }
}
