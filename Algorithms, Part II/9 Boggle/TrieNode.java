public class TrieNode {
    private final TrieNode[] links;
    private boolean hasChild;
    // Unique ID here indicates the different getAllValidWords() call, to see if it should be added
    private int uid;
    // Build string here, because the trie will be built only once
    // Do not build strings in the DFS
    private String word;

    public TrieNode() {
        // Recall that the alphabet consists of only the 26 letters A through Z
        // Use trie 26, more space but faster than TNT
        links = new TrieNode[26];
        hasChild = false;
        uid = 0;
        word = null;
    }

    public boolean isEnd() {
        return word != null;
    }

    public void setEnd(String w) {
        this.word = w;
    }

    public TrieNode get(char c) {
        return links[c - 'A'];
    }

    public void put(char c) {
        links[c - 'A'] = new TrieNode();
        hasChild = true;
    }

    public boolean contains(char c) {
        return links[c - 'A'] != null;
    }

    public boolean hasChild() {
        return hasChild;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }

    public String getWord() {
        return word;
    }
}