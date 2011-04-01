package biz.k11i.trie.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import biz.k11i.trie.Trie;

/**
 * �A�����X�g�ɂ��P���� Trie �̎����ł��B
 * 
 * @author komiya
 */
public class ListTrie implements Trie {
    static class TrieNode {
        /** ������I�[��\���m�[�h�ł� */
        private static final TrieNode TERMINATION_NODE;

        static {
            TERMINATION_NODE = new TrieNode('\ufffe');
            TERMINATION_NODE.children = Collections.emptyList();
        }

        /** ���̃m�[�h�Ɛe�m�[�h�̊Ԃ̃G�b�W�Ɋ��蓖�Ă�ꂽ���� */
        private char ch;

        /** �����̏����ɕ��ׂ�ꂽ�q�m�[�h�̈ꗗ */
        private List<TrieNode> children = new LinkedList<TrieNode>();

        /**
         * �G�b�W�Ɋ��蓖�Ă��镶�����w�肵�āATrieNode �I�u�W�F�N�g�𐶐����܂��B
         * 
         * @param ch
         */
        TrieNode(char ch) {
            this.ch = ch;
        }

        /**
         * ���̃m�[�h�̔z���ɁA�w�肳�ꂽ�������G�b�W�Ɋ��蓖�Ă��q�m�[�h�̃I�u�W�F�N�g���擾�܂��͐������A�ԋp���܂��B
         * 
         * @param childChar
         * @return
         */
        TrieNode getOrCreateChild(char childChar) {
            for (ListIterator<TrieNode> i = children.listIterator(); i
                    .hasNext();) {
                TrieNode childNode = i.next();
                if (childChar == childNode.ch) {
                    return childNode;
                }

                if (childChar < childNode.ch) {
                    TrieNode result = new TrieNode(childChar);
                    i.previous();
                    i.add(result);
                    return result;
                }
            }

            TrieNode result = new TrieNode(childChar);
            children.add(result);

            return result;
        }

        /**
         * �w�肳�ꂽ���������q�m�[�h��T���o���A�ԋp���܂��B
         * 
         * @param childChar
         * @return
         */
        TrieNode findChild(char childChar) {
            for (TrieNode childNode : children) {
                if (childNode.ch == childChar) {
                    return childNode;
                }
            }

            return null;
        }

        /**
         * ���̃m�[�h�z���̂��ׂĂ̎q�m�[�h��j�����܂��B
         */
        void clearChildren() {
            children.clear();
        }

        /**
         * ���̃m�[�h�z���ɁA������I�[��\���m�[�h��ǉ����܂��B
         */
        void addTermination() {
            if (!hasTermination()) {
                children.add(TERMINATION_NODE);
            }
        }

        /**
         * ���̃m�[�h�z���ɁA������I�[��\���m�[�h�����݂���ꍇ�� true ��ԋp���܂��B
         * 
         * @return
         */
        boolean hasTermination() {
            if (children.isEmpty()
                    || !TERMINATION_NODE
                            .equals(children.get(children.size() - 1))) {
                return false;
            }

            return true;
        }
    }

    /** ���� Trie �ɂ����郋�[�g�m�[�h */
    private TrieNode root = new TrieNode('\uffff');

    @Override
    public void build(Iterable<String> patterns) {
        clear();
        addAll(patterns);
    }

    @Override
    public void clear() {
        root.clearChildren();
    }

    @Override
    public void addAll(Iterable<String> patterns) {
        for (String pattern : patterns) {
            add(pattern);
        }
    }

    @Override
    public void add(String pattern) {
        TrieNode parent = root;

        for (int i = 0; i < pattern.length(); i++) {
            char ch = pattern.charAt(i);
            TrieNode child = parent.getOrCreateChild(ch);
            parent = child;
        }

        parent.addTermination();
    }

    @Override
    public boolean contains(String pattern) {
        TrieNode current = root;

        for (int i = 0; i < pattern.length(); i++) {
            char ch = pattern.charAt(i);
            current = current.getOrCreateChild(ch);
            if (current == null) {
                return false;
            }
        }

        if (current.hasTermination()) {
            return true;
        }

        return false;
    }

    @Override
    public Iterable<String> searchPrefixOf(String text) {
        return new IterableImpl(text) {

            @Override
            Iterator<String> createIterator(TrieNode root, CharSequence text) {
                return new SearchPrefixOfIterator(root, text);
            }
        };
    }

    /**
     * search �n���\�b�h�̖߂�l�ƂȂ� Iterable �I�u�W�F�N�g�̒��ۃN���X�ł��B
     * <p>
     * Iterator �ŕ\�������T���헪�I�u�W�F�N�g�̐����ɁAFactory Method �p�^�[���𗘗p���Ă��܂��B
     * </p>
     * 
     * @author komiya
     */
    private abstract class IterableImpl implements Iterable<String> {
        private CharSequence text;

        IterableImpl(CharSequence text) {
            this.text = text;
        }

        @Override
        public Iterator<String> iterator() {
            return createIterator(root, text);
        }

        /**
         * �T�����ʂ�񋓂��� Iterator �I�u�W�F�N�g�𐶐����A�ԋp���܂��B
         * 
         * @param root
         *            Trie �̃��[�g�m�[�h
         * @param text
         *            �T���Ώۂ̕�����
         * @return
         */
        abstract Iterator<String> createIterator(TrieNode root,
                CharSequence text);
    }

    /**
     * �w�肳�ꂽ�e�L�X�g�̐ړ����ɍ��v���镶����� Trie ����T���o���A�񋓂��s�� Iterator �̎����N���X�ł��B
     * 
     * @author komiya
     */
    private static class SearchPrefixOfIterator implements Iterator<String> {
        /** ����Ώۂ̃e�L�X�g */
        private CharSequence text;

        /** �������� text �̕����ʒu��\���C���f�N�X */
        private int currentIndex;

        /** �������� Trie �̃m�[�h */
        private TrieNode current;

        /** ���� next() ���\�b�h�̌Ăяo���ŕԋp���ׂ��I�u�W�F�N�g */
        private String nextObject;

        public SearchPrefixOfIterator(TrieNode root, CharSequence text) {
            this.current = root;
            this.text = text;
        }

        @Override
        public boolean hasNext() {
            if (nextObject != null) {
                return true;
            }

            while (currentIndex < text.length() && current != null) {
                char ch = text.charAt(currentIndex);
                currentIndex++;

                current = current.findChild(ch);
                if (current == null) {
                    return false;
                }

                if (current.hasTermination()) {
                    nextObject = text.subSequence(0, currentIndex).toString();
                    return true;
                }
            }

            return false;
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            String result = nextObject;
            nextObject = null;

            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException(
                    "This method is not implemented.");
        }

    }

    @Override
    public Iterable<String> searchPredictive(String prefix) {
        throw new UnsupportedOperationException(
                "This method is not implemented.");
    }
}