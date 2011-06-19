package biz.k11i.trie;

import java.util.Iterator;
import java.util.NoSuchElementException;

public interface Traversable {
    final char TERMINATION_CODE = '\uffff';

    /**
     * �؍ݐ�̃m�[�h�����[�g�m�[�h�Ƃ��܂��B
     * 
     * @return
     */
    void moveToRoot();

    /**
     * ���ݑ؍݂��Ă���m�[�h�ɑJ�ڂ����Ƃ��ɒH�����}�̕�����ԋp���܂��B
     * <p>
     * </p>
     * 
     * @return
     */
    char getChar();

    /**
     * ���[�g�m�[�h���猻�ݑ؍݂��Ă���m�[�h�ɓ��B����܂łɒH�����}�̕�����A�����A�ԋp���܂��B
     * 
     * @return
     */
    String getCurrentString();

    /**
     * ���ݑ؍݂��Ă���m�[�h����A�w�肳�ꂽ�����ɑΉ�����}��H���Ďq���̃m�[�h�ɑJ�ڂ��܂��B
     * 
     * @param ch
     * @return
     */
    boolean followChild(char ch);

    /**
     * ���ݑ؍݂��Ă���m�[�h����A�ŏ��̎q���̃m�[�h�ɑJ�ڂ��܂��B
     * 
     * @return
     */
    boolean followFirstChild();

    /**
     * ���ݑ؍݂��Ă���m�[�h�ŏI�[���镶���񂪊܂܂��ꍇ�ɁAtrue ��ԋp���܂��B
     * 
     * @return
     */
    boolean isTerminated();

    /**
     * ���ݑ؍݂��Ă���m�[�h�Ɛe�������A���̌Z��m�[�h�ɑJ�ڂ��܂��B
     * <p>
     * ���݂̃m�[�h���Ō�̌Z��m�[�h�ł���ꍇ�ɂ��̃��\�b�h���Ăяo�����ƁAfalse ��ԋp���܂��B ����ȊO�̏ꍇ�� true ��ԋp���܂��B
     * </p>
     * 
     * @return
     */
    boolean nextSibling();

    /**
     * ���ݑ؍݂��Ă���m�[�h����e�̃m�[�h�ɖ߂�܂��B
     * <p>
     * ���[�g�m�[�h�ɖ߂����ꍇ�Afalse ��ԋp���܂��B����ȊO�̏ꍇ�� true ��ԋp���܂��B
     * </p>
     * 
     * @return
     */
    boolean backtrack();
}

/**
 * ���ʐړ����T���̎�����B
 * 
 * @author komiya
 */
class CommonPrefixSearcher implements Iterator<String> {
    private Traversable traversable;

    private String pattern;

    private int charIndex;

    private String nextString;

    public CommonPrefixSearcher(Traversable traversable, String pattern) {
        this.traversable = traversable;
        this.pattern = pattern;
    }

    @Override
    public boolean hasNext() {
        if (nextString != null) {
            return true;
        }

        while (charIndex < pattern.length()) {
            char ch = pattern.charAt(charIndex);
            charIndex++;

            if (!traversable.followChild(ch)) {
                return false;
            }

            if (traversable.isTerminated()) {
                nextString = traversable.getCurrentString();
                break;
            }
        }

        return false;
    }

    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        String result = nextString;
        nextString = null;

        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

/**
 * �\�������̎�����B
 * 
 * @author komiya
 */
class PredictiveSearcher implements Iterator<String> {
    private Traversable traversable;

    private String pattern;

    private int charIndex;

    private String nextString;

    private int depth = -1;

    PredictiveSearcher(Traversable traversable, String pattern) {
        this.traversable = traversable;
        this.pattern = pattern;
        prepare();
    }

    private void prepare() {
        // �܂��͊��S��v����������
        while (charIndex < pattern.length()) {
            char ch = pattern.charAt(charIndex);
            charIndex++;

            if (!traversable.followChild(ch)) {
                // ��v������̂����݂��Ȃ�
                return;
            }
        }
        
        depth = 0;

        if (traversable.isTerminated()) {
            // exactMatch ���镶���񂪑��݂���Ȃ�A��������̕ԋp�l�Ƃ��ėp�ӂ��Ă���
            nextString = traversable.getCurrentString();
        }
    }

    @Override
    public boolean hasNext() {
        if (nextString != null) {
            return true;
        }

        while (succ()) {
            if (traversable.isTerminated()) {
                nextString = traversable.getCurrentString();
                return true;
            }
        }

        return false;
    }

    /**
     * ���̌��m�[�h�Ɉړ����܂��B
     * 
     * @return
     */
    private boolean succ() {
        if (depth < 0) {
            return false;
        }
        
        // �܂��͎q���̃m�[�h��H���Ă݂�
        if (traversable.followFirstChild()) {
            depth++;
            return true;
        }

        while (depth > 0) {
            // �q���m�[�h�����݂��Ȃ���΁A�Z��m�[�h��H���Ă݂�
            if (traversable.nextSibling()) {
                return true;
            }

            traversable.backtrack();
            depth--;
        }

        return false;
    }

    @Override
    public String next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        String result = nextString;
        nextString = null;

        return result;
    }

    @Override
    public void remove() {
        // TODO Auto-generated method stub

    }

}
