import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        // input
        StringBuilder sb = new StringBuilder();
        while (!BinaryStdIn.isEmpty()) {
            sb.append(BinaryStdIn.readChar());
        }
        int length = sb.length();
        CircularSuffixArray csa = new CircularSuffixArray(sb.toString());

        // find "first" and output
        for (int i = 0; i < length; i++) {
            if (csa.index(i) == 0) {
                // write a 32-bit int
                BinaryStdOut.write(i);
                break;
            }
        }

        // find the string "t"
        for (int i = 0; i < length; i++) {
            // the i-th original suffix string
            int index = csa.index(i);
            // get the index of its last character
            int lastIndex = (length - 1 + index) % length;
            // append these characters, and then we get "t"
            BinaryStdOut.write(sb.charAt(lastIndex));
        }

        BinaryStdOut.close();

    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {

        // input
        int first = BinaryStdIn.readInt();
        StringBuilder t = new StringBuilder();
        while (!BinaryStdIn.isEmpty()) {
            t.append(BinaryStdIn.readChar());
        }
        int length = t.length();

        // compute frequency counts
        final int EXTENDED_ASCII_INDEX_MAX = 256;
        int[] count = new int[EXTENDED_ASCII_INDEX_MAX + 1];
        for (int i = 0; i < length; i++) {
            count[t.charAt(i) + 1]++;
        }
        // transform counts to indices
        for (int i = 0; i < EXTENDED_ASCII_INDEX_MAX; i++) {
            count[i + 1] += count[i];
        }
        // generate next array
        int[] next = new int[length];
        for (int i = 0; i < length; i++) {
            char c = t.charAt(i);
            next[count[c]] = i;
            count[c]++;
        }

        // output
        for (int i = 0; i < length; i++) {
            BinaryStdOut.write(t.charAt(next[first]));
            first = next[first];
        }
        BinaryStdOut.close();

    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if ("-".equals(args[0])) {
            transform();
        } else {
            inverseTransform();
        }
    }

}