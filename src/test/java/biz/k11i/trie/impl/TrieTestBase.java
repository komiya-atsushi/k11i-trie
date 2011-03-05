/**
 * 
 */
package biz.k11i.trie.impl;

import java.util.Arrays;
import java.util.List;

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
     * [ "a" ] ����Ȃ� Trie �̍\�z���������s���邩���������܂��B
     */
    @Test
    public void testBuildOnly1() {
        Trie trie = create();

        List<String> patterns = Arrays.asList("a");
        trie.build(patterns);

        Assert.assertNotNull(trie);
    }

    /**
     * [ "a", "b", "c", "d", "e" ] ����Ȃ� Trie �̍\�z���������s���邩���������܂��B
     */
    @Test
    public void testBuildOnly2() {
        Trie trie = create();

        List<String> patterns = Arrays.asList("a", "b", "c", "d", "e");
        trie.build(patterns);

        Assert.assertNotNull(trie);
    }

    /**
     * [ "aa" ] ����Ȃ� Trie �̍\�z���������s���邩���������܂��B
     */
    @Test
    public void testBuildOnly3() {
        Trie trie = create();

        List<String> patterns = Arrays.asList("aa");
        trie.build(patterns);

        Assert.assertNotNull(trie);
    }

    /**
     * [ "a" ] ����Ȃ� Trie �ɑ΂��Acontains("a") �̌Ăяo�����ʂ� true �ƂȂ邱�Ƃ��������܂��B
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
     * [ "a", "b", "c", "d", "e" ] ����Ȃ� Trie �ɑ΂��A���ꂼ����w�肵�� contains() ���Ăяo�������ʂ�
     * true �ƂȂ邱�Ƃ��������܂��B
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
     * [ "aa" ] ����Ȃ� Trie �ɑ΂��A"aa" ���w�肵�� contains() ���Ăяo�������ʂ� true �ƂȂ邱�Ƃ��������܂��B
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
}
