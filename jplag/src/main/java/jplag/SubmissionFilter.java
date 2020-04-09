package jplag;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Abstract class providing a filter interface. Instances of this class can be used
 * to determine whether two submissions should be checked against each other.
 */
public abstract class SubmissionFilter {

    /**
     * Should the given Submissions be checked against each other?
     */
    public abstract boolean shouldCheck(Submission s1, Submission s2);

    /**
     * A Filter that only checks Submission pairs listed in the given File.
     */
    public static class WhitelistFilter extends ListFilter {
        public WhitelistFilter(String whitelistFile) throws IOException {
            loadPairsFromFile(whitelistFile);
        }

        @Override
        public boolean shouldCheck(Submission s1, Submission s2) {
            return isPairListed(s1, s2);
        }
    }

    /**
     * A Filter that only checks Submission pairs NOT listed in the given File.
     */
    public static class BlacklistFilter extends ListFilter {
        public BlacklistFilter(String blacklistFile) throws IOException {
            loadPairsFromFile(blacklistFile);
        }

        @Override
        public boolean shouldCheck(Submission s1, Submission s2) {
            return !isPairListed(s1, s2);
        }
    }


    /**
     * An abstract Filter class that manages a Set of Submission names.
     */
    static abstract class ListFilter extends SubmissionFilter {
        private final Set<SubmissionNamePair> listedPairs = new HashSet<SubmissionNamePair>();

        protected void loadPairsFromFile(String filepath) throws IOException {
            try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
                for (String line; (line = reader.readLine()) != null; ) {
                    String[] pairs = line.split(";");

                    for (int i = 0; i < pairs.length; i++) {
                        for (int j = i + 1; j < pairs.length; j++) {
                            listedPairs.add(new SubmissionNamePair(pairs[i], pairs[j]));
                            listedPairs.add(new SubmissionNamePair(pairs[j], pairs[i]));
                        }
                    }
                }
            }
        }

        public boolean isPairListed(Submission s1, Submission s2) {
            return listedPairs.contains(new SubmissionNamePair(s1.name, s2.name));
        }
    }

    static class SubmissionNamePair {
        private final String s1, s2;

        public SubmissionNamePair(String s1, String s2) {
            this.s1 = s1;
            this.s2 = s2;
        }

        @Override
        public int hashCode() {
            return Objects.hash(s1, s2);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof SubmissionNamePair)) {
                return false;
            }
            SubmissionNamePair other = (SubmissionNamePair) obj;
            return Objects.equals(s1, other.s1) && Objects.equals(s2, other.s2);
        }
    }
}
