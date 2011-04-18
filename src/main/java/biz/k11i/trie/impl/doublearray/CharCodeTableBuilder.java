package biz.k11i.trie.impl.doublearray;

import java.util.Collection;

/**
 * 
 * 
 * @author komiya
 */
interface CharCodeTableBuilder {
    CharCodeTable build(Collection<String> sortedPatterns);
}
