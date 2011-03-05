package biz.k11i.trie.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * ���񂳂ꂽ�p�^�[���̃��X�g��ɂ�����A�[�� (�p�^�[����̕������w�������C���f�N�X) ���܂񂾓���̋�Ԃ�\���܂��B
 * 
 * @author komiya
 */
class Range implements Iterable<Integer> {
    /** ��Ԃ̊J�n�ʒu��ێ����܂��B */
    private int first;

    /** ��Ԃ̏I�[�ʒu��ێ����܂��B���ۂ̋�Ԃ́A[first, end) �ƂȂ�܂��B */
    private int last;

    /** �[����ێ����܂��B */
    private int depth;

    /**
     * �[�� depth �� [first, end) �̋�Ԃ�\�� Range �I�u�W�F�N�g�𐶐����܂��B
     * 
     * @param first
     *            ��Ԃ̊J�n�ʒu
     * @param last
     *            ��Ԃ̏I�[�ʒu
     * @param depth
     *            �[��
     */
    Range(int first, int last, int depth) {
        if (first >= last) {
            throw new IllegalArgumentException(
                    "'first' must be less than 'last'");
        }

        this.first = first;
        this.last = last;
        this.depth = depth;
    }

    /**
     * ��Ԃ̊J�n�ʒu��ԋp���܂��B
     * 
     * @return
     */
    int getFirst() {
        return first;
    }

    /**
     * ��Ԃ̏I�[�ʒu��ԋp���܂��B
     * 
     * @return
     */
    int getLast() {
        return last;
    }

    /**
     * ���̋�Ԃ̐[�����擾���܂��B
     * 
     * @return
     */
    int getDepth() {
        return depth;
    }

    /**
     * ��ԓ��̐��l�������ɗ񋓂��� Iterator �I�u�W�F�N�g�𐶐����A�ԋp���܂��B
     * 
     * @return
     */
    @Override
    public Iterator<Integer> iterator() {
        return new IteratorImpl();
    }

    /**
     * iterator() �̌Ăяo���ŕԋp����� Iterator �C���^�t�F�[�X�̎����N���X�ł��B
     * <p>
     * ���̃N���X�̋�Ԃ̐��l���A�����ɗ񋓂��܂��B
     * </p>
     * 
     * @author komiya
     */
    private class IteratorImpl implements Iterator<Integer> {
        /** ���� next() �̌Ăяo���ŕԋp���ׂ��l��ێ����܂��B */
        private int currentValue = first;

        @Override
        public boolean hasNext() {
            if (currentValue < last) {
                return true;
            }

            return false;
        }

        @Override
        public Integer next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            int result = currentValue;
            currentValue++;

            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
