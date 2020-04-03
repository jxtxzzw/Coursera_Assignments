import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class WordNet {

    // id 对应的同义词集
    private final ArrayList<String> synsetList = new ArrayList<>();
    // 单词对应的 id，注意一个单词可能有多个 id 中都出现了
    private final HashMap<String, List<Integer>> synsetToIdMap = new HashMap<>();
    private final SAP sap;

    public WordNet(String synsets, String hypernyms) {
        validate(synsets);
        validate(hypernyms);
        In in;
        int vertexNumber = 0;

        in = new In(synsets);
        while (in.hasNextLine()) {
            // 顶点个数 = 同义词集的个数
            vertexNumber++;
            String[] ss = in.readLine().split(",");
            int id = Integer.parseInt(ss[0]);
            // 因为有 API 是返回整个同义词集，所以先存下来，避免后续需要的时候遍历 List 去拼接
            synsetList.add(ss[1]);
            String[] words = ss[1].split(" ");
            // 将 id 与同义词 List 放进 HashMap
            for (String word : words) {
                if (!synsetToIdMap.containsKey(word)) {
                    synsetToIdMap.put(word, new ArrayList<>());
                }
                synsetToIdMap.get(word).add(id);
            }
        }

        Digraph g = new Digraph(vertexNumber);
        in = new In(hypernyms);
        while (in.hasNextLine()) {
            String[] ss = in.readLine().split(",");
            int id = Integer.parseInt(ss[0]);
            // 每一组上位词的对应关系就是一条边
            for (int i = 1; i < ss.length; i++) {
                g.addEdge(id, Integer.parseInt(ss[i]));
            }
        }

        validate(g);

        sap = new SAP(g);
    }

    private void validate(Digraph g) {
        assert g != null;
        int vertexNumber = g.V();
        int rootNumber = 0;
        for (int i = 0; i < vertexNumber; i++) {
            // 出度为 0 的点是根节点（没有上位词的同义词集）
            if (g.outdegree(i) == 0) {
                rootNumber++;
            }
        }
        // 根节点不足 1 或者大于 1 都不满足条件
        if (rootNumber != 1) {
            throw new IllegalArgumentException();
        }
        // The program uses neither 'DirectedCycle' nor 'Topological' to check whether the digraph is a DAG.
        DirectedCycle dc = new DirectedCycle(g);
        if (dc.hasCycle()) {
            throw new IllegalArgumentException();
        }
    }

    private void validate(String string) {
        if (string == null) {
            throw new IllegalArgumentException();
        }
    }

    public Iterable<String> nouns() {
        // 时刻注意这种类型返回的时候一定要不可变，所以这里返回的时候返回一个新的，不返回原来那个
        return new ArrayList<>(synsetToIdMap.keySet());
    }

    public boolean isNoun(String word) {
        validate(word);
        // 直接查缓存就可以了
        return synsetToIdMap.containsKey(word);
    }

    public int distance(String nounA, String nounB) {
        validate(nounA);
        validate(nounB);
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }
        return sap.length(synsetToIdMap.get(nounA), synsetToIdMap.get(nounB));
    }

    public String sap(String nounA, String nounB) {
        return synsetList.get(sap.ancestor(synsetToIdMap.get(nounA), synsetToIdMap.get(nounB)));
    }

    public static void main(String[] args) {
        WordNet wordNet = new WordNet("tiny_synsets.txt", "tiny_hypernyms.txt");
        System.out.println(wordNet.nouns());
        System.out.println(wordNet.isNoun("wordnet"));
        System.out.println(wordNet.distance("WordNet3.1", "wordnet"));
        System.out.println(wordNet.sap("wordnet", "WordNet3.1"));
    }
}
