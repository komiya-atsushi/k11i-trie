/**
 * 
 */
package biz.k11i.trie.impl;

import biz.k11i.trie.Trie;

/**
 * ListTrie クラスの単体テストを実施します。
 * 
 * @author komiya
 */
public class ListTrieTest extends TrieTestBase {
    @Override
    protected Trie create() {
        return new ListTrie();
    }
}
