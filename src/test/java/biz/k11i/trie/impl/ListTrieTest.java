/**
 * 
 */
package biz.k11i.trie.impl;

import biz.k11i.trie.Trie;

/**
 * ListTrie �N���X�̒P�̃e�X�g�����{���܂��B
 * 
 * @author komiya
 */
public class ListTrieTest extends TrieTestBase {
    @Override
    protected Trie create() {
        return new ListTrie();
    }
}
