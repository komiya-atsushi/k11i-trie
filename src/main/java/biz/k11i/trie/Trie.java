package biz.k11i.trie;

/**
 * �������ێ����� Trie �ł��B
 * <p>
 * Trie ���񋟂��ׂ��@�\�Ƃ��̏ڍׂɂ��āA��`���܂��B
 * </p>
 * 
 * @author komiya
 */
public interface Trie {
    /**
     * �w�肳�ꂽ������̏W�������ƂɁATrie ���\�z���܂��B
     * <p>
     * ���̃��\�b�h���Ăяo���ꂽ StringTrie �I�u�W�F�N�g�́A���łɕێ����Ă��镶�����j�����A �^����ꂽ������̏W���݂̂���Ȃ� Trie
     * ��V���ɍ\�z���܂��B
     * </p>
     * <p>
     * ���̃C���^�t�F�[�X����������T�u�N���X�́A���̃��\�b�h��K���񋟂���K�v������܂��B
     * </p>
     * 
     * @param patterns
     *            ������̏W��
     */
    void build(Iterable<String> patterns);

    /**
     * ���� Trie ���ێ����Ă��镶��������ׂĔj�����܂��B
     * <p>
     * ���̃C���^�t�F�[�X����������T�u�N���X�́A���̃��\�b�h��K���񋟂���K�v������܂��B
     * </p>
     */
    void clear();

    /**
     * ���� Trie �ɁA�w�肳�ꂽ������̏W����V���ɉ����܂��B
     * <p>
     * ���ł� Trie ���ɑ��݂��镶����͂��̂܂܂ɁA�^����ꂽ�����񂷂ׂĂ� Trie �ɒǉ����܂��B
     * </p>
     * 
     * @param patterns
     *            ������̏W��
     */
    void addAll(Iterable<String> patterns);

    /**
     * �w�肳�ꂽ��������A���� Trie �ɒǉ����܂��B
     * 
     * @param pattern
     *            ���� Trie �ɒǉ����镶����
     */
    void add(String pattern);

    /**
     * �w�肳�ꂽ�����񂪁A���� Trie �Ɋ܂܂��ꍇ�� true ��ԋp���܂��B
     * 
     * @param pattern
     * @return
     */
    boolean contains(String pattern);

    /**
     * �w�肳�ꂽ�e�L�X�g�̐ړ����Ɉ�v����ATrie �̒��̂��ׂĂ̕������񋓂��܂��B
     * 
     * @param text
     *            �e�L�X�g
     * @return �e�L�X�g�̐ړ����Ɉ�v���镶����̏W��
     */
    Iterable<String> searchPrefixOf(String text);

    /**
     * �w�肳�ꂽ�������ړ����Ƃ��Ă��ATrie �̒��̂��ׂĂ̕������񋓂��܂��B
     * 
     * @param prefix
     *            �ړ���
     * @return
     */
    Iterable<String> searchPredictive(String prefix);
}
