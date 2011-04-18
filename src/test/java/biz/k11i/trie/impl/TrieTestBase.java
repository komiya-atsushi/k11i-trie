/**
 * 
 */
package biz.k11i.trie.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import biz.k11i.trie.Trie;

/**
 * @author komiya
 * 
 */
public abstract class TrieTestBase {
    protected abstract Trie create();

    @Test
    public void testConstruct() {
        Trie trie = create();
        Assert.assertNotNull(trie);
    }

    /**
     * [ "a" ] からなる Trie の構築が正しく行えるかを試験します。
     */
    @Test
    public void testBuildOnly1() {
        Trie trie = create();

        List<String> patterns = Arrays.asList("a");
        trie.build(patterns);

        Assert.assertNotNull(trie);
    }

    /**
     * [ "a", "b", "c", "d", "e" ] からなる Trie の構築が正しく行えるかを試験します。
     */
    @Test
    public void testBuildOnly2() {
        Trie trie = create();

        List<String> patterns = Arrays.asList("a", "b", "c", "d", "e");
        trie.build(patterns);

        Assert.assertNotNull(trie);
    }

    /**
     * [ "aa" ] からなる Trie の構築が正しく行えるかを試験します。
     */
    @Test
    public void testBuildOnly3() {
        Trie trie = create();

        List<String> patterns = Arrays.asList("aa");
        trie.build(patterns);

        Assert.assertNotNull(trie);
    }

    /**
     * [ "a" ] からなる Trie に対し、contains("a") の呼び出し結果が true となることを試験します。
     */
    @Test
    public void testContains1() {
        Trie trie = create();

        List<String> patterns = Arrays.asList("a");
        trie.build(patterns);

        Assert.assertTrue(trie.contains("a"));
        Assert.assertFalse(trie.contains("b"));
        Assert.assertFalse(trie.contains("aa"));
        Assert.assertFalse(trie.contains("ab"));
    }

    /**
     * [ "a", "b", "c", "d", "e" ] からなる Trie に対し、それぞれを指定して contains() を呼び出した結果が
     * true となることを試験します。
     */
    @Test
    public void testContains2() {
        Trie trie = create();

        List<String> patterns = Arrays.asList("a", "b", "c", "d", "e");
        trie.build(patterns);

        Assert.assertTrue(trie.contains("a"));
        Assert.assertTrue(trie.contains("b"));
        Assert.assertTrue(trie.contains("c"));
        Assert.assertTrue(trie.contains("d"));
        Assert.assertTrue(trie.contains("e"));
        Assert.assertFalse(trie.contains("f"));
        Assert.assertFalse(trie.contains("a1"));
    }

    /**
     * [ "aa" ] からなる Trie に対し、"aa" を指定して contains() を呼び出した結果が true となることを試験します。
     */
    @Test
    public void testContains3() {
        Trie trie = create();

        List<String> patterns = Arrays.asList("aa");
        trie.build(patterns);

        Assert.assertTrue(trie.contains("aa"));
        Assert.assertFalse(trie.contains("aaa"));
        Assert.assertFalse(trie.contains("a"));
    }

    @Test
    public void testContains4() {
        Trie trie = create();

        List<String> patterns = Arrays.asList("a", "aaaaa", "aaa");
        trie.build(patterns);

        Assert.assertTrue(trie.contains("a"));
        Assert.assertTrue(trie.contains("aaa"));
        Assert.assertTrue(trie.contains("aaaaa"));
        Assert.assertFalse(trie.contains("aa"));
        Assert.assertFalse(trie.contains("aaaa"));
        Assert.assertFalse(trie.contains("aaaaaa"));
    }

    @Test
    public void testContains5() {
        Trie trie = create();

        List<String> patterns = Arrays.asList("a", "abcde", "abc");
        trie.build(patterns);

        Assert.assertTrue(trie.contains("a"));
        Assert.assertTrue(trie.contains("abc"));
        Assert.assertTrue(trie.contains("abcde"));
        Assert.assertFalse(trie.contains("ab"));
        Assert.assertFalse(trie.contains("abcd"));
        Assert.assertFalse(trie.contains("abcdef"));
        Assert.assertFalse(trie.contains("bc"));
        Assert.assertFalse(trie.contains("cd"));
        Assert.assertFalse(trie.contains("de"));
    }

    /**
     * Trie に含まれる語句と同一の語句が、searchPrefixOf() の検索対象語句として指定された場合の 動作を確認します。
     */
    @Test
    public void testSearchPrefixOf1() {
        Trie trie = create();

        List<String> patterns = Arrays.asList("a");
        trie.build(patterns);

        Iterable<String> prefixes = trie.searchPrefixOf("a");
        Assert.assertNotNull(prefixes);

        Iterator<String> iter = prefixes.iterator();
        Assert.assertNotNull(iter);

        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals("a", iter.next());

        Assert.assertFalse(iter.hasNext());
    }

    /**
     * Trie に含まれない語句が searchPrefixOf() の検索対象語句として指定された場合の 動作を確認します。
     */
    @Test
    public void testSearchPrefixOf2() {
        Trie trie = create();

        List<String> patterns = Arrays.asList("b");
        trie.build(patterns);

        Iterable<String> prefixes = trie.searchPrefixOf("a");
        Assert.assertNotNull(prefixes);

        Iterator<String> iter = prefixes.iterator();
        Assert.assertNotNull(iter);

        Assert.assertFalse(iter.hasNext());
    }

    /**
     * Trie に含まれない語句が searchPrefixOf() の検索対象語句として指定された場合の 動作を確認します。
     */
    @Test
    public void testSearchPrefixOf3() {
        Trie trie = create();

        List<String> patterns = Arrays.asList("ab");
        trie.build(patterns);

        Iterable<String> prefixes = trie.searchPrefixOf("a");
        Assert.assertNotNull(prefixes);

        Iterator<String> iter = prefixes.iterator();
        Assert.assertNotNull(iter);

        Assert.assertFalse(iter.hasNext());
    }

    /**
     * Trie に含まれない語句が searchPrefixOf() の検索対象語句として指定された場合の動作を確認します。
     * (Iterator#next() の呼び出しによる例外の発生を確認する)
     */
    @Test(expected = NoSuchElementException.class)
    public void testSearchPrefixOf3_2() {
        Trie trie = create();

        List<String> patterns = Arrays.asList("ab");
        trie.build(patterns);

        Iterable<String> prefixes = trie.searchPrefixOf("a");
        Assert.assertNotNull(prefixes);

        Iterator<String> iter = prefixes.iterator();
        Assert.assertNotNull(iter);

        // 以下を実行すると、例外が発生するはず
        iter.next();
    }

    @Test
    public void testSearchPrefixOf4() {
        Trie trie = create();

        List<String> patterns = Arrays.asList("a", "abcde", "abc");
        trie.build(patterns);

        Iterable<String> prefixes = trie.searchPrefixOf("abcdef");
        Assert.assertNotNull(prefixes);

        Iterator<String> iter = prefixes.iterator();
        Assert.assertNotNull(iter);

        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals("a", iter.next());

        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals("abc", iter.next());

        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals("abcde", iter.next());

        Assert.assertFalse(iter.hasNext());
    }

    @Test
    public void testSearchPrefixOf5() {
        Trie trie = create();

        List<String> patterns = Arrays.asList("a", "abcde", "bcde", "bcd");
        trie.build(patterns);

        Iterable<String> prefixes = trie.searchPrefixOf("abcd");
        Assert.assertNotNull(prefixes);

        Iterator<String> iter = prefixes.iterator();
        Assert.assertNotNull(iter);

        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals("a", iter.next());

        Assert.assertFalse(iter.hasNext());
    }

    @Test
    public void testSearchPrefixOf6() {
        Trie trie = create();

        List<String> patterns = Arrays.asList("a", "abcde", "bcde", "bcd");
        trie.build(patterns);

        Iterable<String> prefixes = trie.searchPrefixOf("bcdfg");
        Assert.assertNotNull(prefixes);

        Iterator<String> iter = prefixes.iterator();
        Assert.assertNotNull(iter);

        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals("bcd", iter.next());

        Assert.assertFalse(iter.hasNext());
    }
    
    private static final Integer ONE = 1;

    @Test
    public void testSearchPredictive1() {
        Trie trie = create();

        List<String> patterns = Arrays.asList("abracadabra", "bracadabra",
                "racadabra", "acadabra", "adabra", "abra", "bra", "ra", "a");

        String key = "a";
        Iterable<String> predictor = trie.searchPredictive(key);

        Set<String> expected = collectByPrefix(patterns, key);
        for (String predicted : predictor) {
            if (!expected.remove(predicted)) {
                //Assert.assert
                Assert.fail(predicted + " は存在すべきでない");
            }
        }
    }
    
    private Set<String> collectByPrefix(Iterable<String> patterns, String prefix) {
        Set<String> result = new HashSet<String>();
        
        for (String pattern : patterns) {
            if (pattern.startsWith(prefix)) {
                result.add(prefix);
            }
        }
        
        return result;
    }
}
