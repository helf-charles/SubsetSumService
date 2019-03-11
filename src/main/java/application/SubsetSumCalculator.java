package application;

import application.bean.SubsetSum;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A calculator meant to be a generic solution for all general instances of the Subset Sum
 * problem set.  Consists of one public method to take input and multiple private methods to
 * break the problem down into manageable steps.
 */
public class SubsetSumCalculator {
    static Logger log = LoggerFactory.getLogger("TestApplication");

    /**
     * Takes in a list of integers and attempts to determine whether a subset of that list exists
     * such that the sum of that subset's elements equals exactly 0.  If no such subset within the
     * list exists, it will return NULL.  Otherwise, it will return a list of each qualifying
     * subset.
     *
     * This method starts by separating positive and negative values into separate list.  It then
     * creates two maps to contain all the possible summations of positive and negative elements.
     * Lastly, it searches through these lists for potential matches.  Each match is added into
     * a result.  Once all feasible summations are found, they are returned.
     *
     * @param   startList  the target list we intend to inspect
     * @param   target     the target value we wish to obtain via our summations
     * @return             the result of our inspection of the target list
     */
    public static List<SubsetSum> calculate(List<Integer> startList, int target) {
        log.info("Starting calculate()...");
        List<SubsetSum> result;

        List<Integer> positives = getSigned(startList, false);
        List<Integer> negatives = getSigned(startList, true);
        log.info("Positives contains: " + positives);
        log.info("Negatives contains: " + negatives);

        List<SubsetSum> summationsPos = getSummations(positives);
        List<SubsetSum> summationsNeg = getSummations(negatives);

        result = findMatches(summationsPos, summationsNeg, target);
        log.info("Method calculate() has finished.");

        return result;
    }


    /**
     * Takes in a list of integers and a flag to extract either all positive or all negative
     * values from the target list.
     *
     * @param  startList  the target list we intend to inspect
     * @param  isNeg      a flag denoting whether we want to extract positive or negative values
     * @return            a list of either all positive or all negative integer values
     */
    private static List<Integer> getSigned(List<Integer> startList, boolean isNeg) {
        log.info("Starting getSigned()...");
        List<Integer> result = new ArrayList<>();

        for (int item : startList) {
            if (isNeg) {
                if (item < 0) {
                    result.add(item);
                }
            } else {
                if (item > 0) {
                    result.add(item);
                }
            }
        }
        log.info("Method getSigned() has finished.");
        return result;
    }

    /**
     * Takes in a list of integers and computes all the possible values that can be generated by summing one or more
     * elements of the list together.  For an input list of size X, we know that the size of any subset generated
     * will have a size Y of between exactly 1 and X.  To ensure that we find every possible subset, taking a systematic
     * brute-force approach makes sense.  Because summations utilize the mathematical operation of addition, we know
     * that order does not matter.
     *
     * In this case, we can decide to start with a target subset of size 1 and iterate through the original list to
     * search for every possible subset that exists of size 1.  By starting with all the left-most elements, then, we
     * can gradually substitute in elements to the right in such a manner as to guarantee that we've found every
     * combination of every element possible that can comprise a subset of size Y.  Suppose we want to find all the
     * possible summations of all the possible subsets within the following list:
     *
     * [1, 3, 5, 7]
     *
     * In the original list, each element has a fixed index that can be referenced to uniquely identify it.  We can
     * use this property to our advantage.  Since we are starting with a subset of size 1, our process of discovering
     * all subsets of size 1 within the input list will be rather trivial.  By advancing the right-most element of our
     * subset 1 index position at a time, we can exhaustively describe the resulting subsets using the diagrams below
     * (where the letter 'n' is used to denote the index we're currently looking at):
     *
     *
     * [1, 3, 5, 7]
     *  n
     * [[1]]
     *
     *
     * [1, 3, 5, 7]
     *     n
     * [[3]]
     *
     *
     * [1, 3, 5, 7]
     *        n
     * [[5]]
     *
     *
     * [1, 3, 5, 7]
     *           n
     * [[7]]
     *
     *
     * This process of iterating through the entire input list, left to right, correctly gives us all of the possible
     * subsets of size 1 that can be summed to give us an answer.  In this case, the summations are absolutely trivial
     * because they consist of a single argument (i.e. summing all the elements within the subset [[5]] gives us a sum
     * of 5).  However, this general process remains valid for all non-trivial examples, as well.
     *
     *
     * @param  startList  the initial values for which we wish to acquire summations
     * @return            all of the summations that are possible from the initial values
     */
    public static List<SubsetSum> getSummations(List<Integer> startList) {
        log.info("Starting getSummations()...");
        List<SubsetSum> summations = new ArrayList<>();
        List<Integer> positions = new ArrayList<>();
        int listSize = startList.size();
        int subsetSize;
        int sum;
        boolean finished;
        for (int i = 0; i < listSize; i++) {
            positions.add(-1);
        }

        //We want to go through this entire process for every possible subset size within this list
        for (subsetSize = 0; subsetSize < listSize; subsetSize++) {

            //We want to establish the correct starting Positions for the elements our subset intends to look at
            for (int j = 0; j < subsetSize; j++) {
                positions.set(j, j);
            }

            finished = false;
            while (!finished) {

                //Check if this is the last loop, then reset values
                finished = checkFinished(positions, subsetSize, listSize);
                List<Integer> subset = new ArrayList<>();
                SubsetSum subsetSum = new SubsetSum();
                sum = 0;

                //Add together every element we're currently looking at
                for (int j = 0; j < subsetSize; j++) {
                    sum = sum + startList.get(positions.get(j));
                    subset.add(startList.get(positions.get(j)));
                }

                //Then store it within our Summations list and update the Positions list
                subsetSum.setSum(sum);
                subsetSum.setSubset(subset);
                summations.add(subsetSum);
                positions = adjustPositions(positions, subsetSize, listSize);
            }
        }
        log.info("Method getSummations() has finished.");
        return summations;
    }

    /**
     * As part of the getSummations() method, this method attempts to adjust the indices of the Positions list properly.
     * Consider, as an extension of the previous example, searching for every possible subset of size 2 that exists
     * within the following list (where the numbers beneath the list indicate the index associated with that particular
     * element within the array):
     *
     * [1, 3, 5, 7]
     *  1  2  3  4
     *
     * As mentioned before, each element in this list has a fixed index that we can use to our advantage.  Let's then
     * create a second list to track positions.  Let's further specify that, because we wish to start from left to
     * right, we will have this second list initialized to contain the indices of the two left-most elements:
     *
     * [1, 2]
     *
     * Representing each of these as the letters 'm' and 'n', we can conceptualize our initial view / processing of the
     * list as:
     *
     * [1, 3, 5, 7]
     *  m  n
     * [[1, 3]]
     *
     * Following the previously-mentioned pattern of advancing the right-most element within our subset by 1, our next
     * few steps should resemble:
     *
     *
     * [1, 3, 5, 7]
     *  m     n
     * [[1, 5]]
     * [1, 3]
     *
     *
     * [1, 3, 5, 7]
     *  m        n
     * [[1, 7]]
     * [1, 4]
     *
     *
     * Remember that our current subset size Y is 2.  Since the Yth element in our Positions list is pointing at the Xth
     * element in our actual list, we can't advance the Yth element any further.  Instead, we must advance the (Y-1)th
     * element in our Positions list.  We then must reset the Yth element such that it immediately follows the (Y-1)th
     * element.  This operation gives us:
     *
     * [1, 3, 5, 7]
     *     m  n
     * [[3, 5]]
     * [2, 3]
     *
     * The pattern, at this point, should be becoming clear.  The remaining steps are:
     *
     *
     * [1, 3, 5, 7]
     *     m     n
     * [[3, 7]]
     * [2, 4]
     *
     *
     * [1, 3, 5, 7]
     *        m  n
     * [[5, 7]]
     * [3, 4]
     *
     *
     * For any Target List Size of X and Subset Size of Y, we initialize a Positions List such that the size of the
     * Positions List is equal to Y and such that the values of the Positions List are initialized to the values
     * [1, 2, 3, ... Y].  The initial Subset is equal to the contents of [[Target[Positions[1]], Target[Positions[2]],
     * Target[Positions[3]], ... Target[Positions[Y]]]].  When Positions[Y] is equal to X, we check recurively to see
     * whether Positions[Y-1] is equal to (X-1).  If we find that [Positions[1] is equal to (X-Y), we can safely
     * conclude that we are finished.  Otherwise, for Positions[Y-1] that does not equal or exceed (X-1), we increment
     * Positions[Y-1] by 1 and then set Positions[Y] equal to the new Positions[Y-1] + 1.
     *
     * This method of traversing indices allows us to exhaustively describe every possible subset that can be generated
     * for any given Target List.
     *
     * @param  positions   a list containing the StartList indices of the elements in the current subset
     * @param  subsetSize  the size of the current subset
     * @param  listSize    the size of the StartList
     * @return             an updated Positions list
     */
    private static List<Integer> adjustPositions(List<Integer> positions, int subsetSize, int listSize) {
        if (subsetSize != 0) {
            if (positions.get(subsetSize - 1) == listSize - 1) {
                positions = adjustPositions(positions, subsetSize - 1, listSize - 1);
                if (subsetSize > 1) {
                    int reset = positions.get(subsetSize - 2) + 1;
                    positions.set(subsetSize - 1, reset);
                }
            } else {
                positions.set(subsetSize - 1, positions.get(subsetSize - 1) + 1);
            }
        }
        return positions;
    }

    /**
     * Uses the Positions list, SubsetSize value, and ListSize value to determine whether the algorithm has evaluated
     * every summation combination possible for a subset of this particular size.  If the left-most index of this
     * subset (i.e. Positions[1]) has reached position ListSize - SubsetSize (i.e. X-Y), we know that we're done.
     *
     * @param  positions   a list containing the StartList indices of the elements in the current subset
     * @param  subsetSize  the size of the current subset
     * @param  listSize    the size of the StartList
     * @return             a flag indicating whether the algorithm has found all summations for this
     *                     subset size
     */
    private static boolean checkFinished(List<Integer> positions, int subsetSize, int listSize) {
        if (positions.get(0) == (listSize - subsetSize)) {
            return true;
        }
        return false;
    }


    /**
     * Takes in the positive and negative lists and iteratively checks each summation for equality to the target
     * number.  In cases where there are both positive and negative summations, this method also iteratively sums
     * every possible combination of positive-negative summations to see if any of these results also equal the target
     * number.
     *
     * @param  positives  list of all positive summations
     * @param  negatives  list of all negative summations
     * @param  target     the target value our final sum must equal
     * @return            list of summation pairs matching the target
     */
    private static List<SubsetSum> findMatches(List<SubsetSum> positives, List<SubsetSum> negatives, int target) {
        List<SubsetSum> results = new ArrayList();

        for (SubsetSum pos : positives) {
            if (pos.getSum() == target) {
                results.add(extractValues(pos, null));
            }
        }
        for (SubsetSum neg : negatives) {
            if (neg.getSum() == target) {
                results.add(extractValues(null, neg));
            }
        }
        for (SubsetSum pos : positives) {
            for (SubsetSum neg : negatives) {
                if ((pos.getSum() + neg.getSum()) == target) {
                    results.add(extractValues(pos, neg));
                }
            }
        }

        if (results.isEmpty()) {
            return null;
        }
        return results;
    }

    /**
     * Takes in up to one positive SubsetSum and up to one negative SubsetSum, then combines them into a single,
     * unified SubsetSum.
     *
     * @param  pos  a SubsetSum consisting of only positive integer elements
     * @param  neg  a Subsetsum consisting of only negative integer elements
     * @return      the combination of pos, neg, or pos + neg
     */
    private static SubsetSum extractValues(SubsetSum pos, SubsetSum neg) {
        SubsetSum match = new SubsetSum();
        List<Integer> subset = new ArrayList();
        int sum = 0;

        if (null != pos) {
            sum += pos.getSum();
            for (int value : pos.getSubset()) {
                subset.add(value);
            }
        }
        if (null != neg) {
            sum += neg.getSum();
            for (int value : neg.getSubset()) {
                subset.add(value);
            }
        }

        match.setSum(sum);
        match.setSubset(subset);

        return match;
    }
}