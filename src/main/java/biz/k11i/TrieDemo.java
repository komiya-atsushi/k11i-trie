package biz.k11i;

import java.util.Iterator;

import biz.k11i.trie.Trie;
import biz.k11i.trie.impl.ListTrie;

public class TrieDemo {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Trie trie = new ListTrie();
        
        trie.add("a");
        trie.add("to");
        trie.add("tea");
        trie.add("ted");
        trie.add("ten");
        trie.add("i");
        trie.add("in");
        trie.add("inn");
        trie.add("internal");
        trie.add("internet");
        

        for (String words : trie.searchPredictive("")) {
            System.out.println(words);
        }
    }
}
