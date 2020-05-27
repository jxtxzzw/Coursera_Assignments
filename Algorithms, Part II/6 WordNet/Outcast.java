public class Outcast {
    private final WordNet wordNet;

    public Outcast(WordNet wordnet) {
        this.wordNet = wordnet;
    }

    public String outcast(String[] nouns) {
        int max = 0;
        String outcase = null;
        for (String noun : nouns) {
            int di = 0;
            for (String s : nouns) {
                di += wordNet.distance(noun, s);
            }
            if (di > max) {
                max = di;
                outcase = noun;
            }
        }
        return outcase;
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet("synsets.txt", "hypernyms.txt");
        Outcast outcast = new Outcast(wordnet);
        System.out.println(outcast.outcast("horse zebra cat bear table".split(" ")));
        System.out.println(outcast.outcast("water soda bed orange_juice milk apple_juice tea coffee".split(" ")));
        System.out.println(outcast.outcast("apple pear peach banana lime lemon blueberry strawberry mango watermelon potato".split(" ")));
    }
}