public class Trie {

    private final TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode node = root;
        int i = 0;
        while (i < word.length()) {
            char c = word.charAt(i);
            if (!node.contains(c)) {
                node.put(c);
            }
            node = node.get(c);
            if (c == 'Q') {
                i++; // Skip "Qu"
                if (i == word.length() || word.charAt(i) != 'U') {
                    // Ignore "Q" and "Qx"
                    return;
                }
            }
            i++;
        }
        node.setEnd(word);
    }

    public boolean search(String word) {
        TrieNode node = root;
        int i = 0;
        while (i < word.length()) {
            char c = word.charAt(i);
            if (node.contains(c)) {
                node = node.get(c);
            } else {
                return false;
            }
            if (c == 'Q') {
                i++; // Skip "Qu"
                if (i == word.length() || word.charAt(i) != 'U') {
                    // Ignore "Q" and "Qx"
                    return false;
                }
            }
            i++;
        }
        return node.isEnd();
    }

    public TrieNode prefixNode(char c, TrieNode cache) {
        if (cache == null) {
            cache = root;
        }
        if (cache.contains(c)) {
            cache = cache.get(c);
        } else {
            return null;
        }
        return cache;
    }

}