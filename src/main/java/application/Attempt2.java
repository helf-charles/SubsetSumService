package application;

import application.bean.SubsetSum;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Attempt2 {
    
    public static List<List<Integer>> calculate(List<Integer> startList, int target) {

        List<SubsetSum> summations = getSummations(startList);

        List<List<Integer>> result = summations.stream()
                .filter(subsetsum->subsetsum.getSum()==target)
                .map(SubsetSum::getSubset)
                .collect(Collectors.toList());

        return result;
    }
    private static List<SubsetSum> getSummations(List<Integer> startList) {
        List<SubsetSum> summations = new ArrayList<>();
        List<Integer> subset = new ArrayList<>();
        SubsetSum subsetSum = null;
        Integer sum = null;

        List<List<Integer>> subsetList = populateAllSubsets(startList);

        for(List<Integer> item:subsetList){
            subsetSum = new SubsetSum();
            subsetSum.setSubset(item);
            subsetSum.setSum(item.stream().mapToInt(Integer::intValue).sum());
            summations.add(subsetSum);
        }

        return summations;
    }

    private static List<List<Integer>> populateAllSubsets(List<Integer> startList){
        int n = startList.size();
        List<List<Integer>> results = new ArrayList<>();
        List<Integer> subset = null;
        // ayyy lmao wtf does this magic bitwise tomfoolery do?
        for (int i = 0; i < (1<<n); i++){
            subset = new ArrayList<>();
            for (int j = 0; j < n; j++){
                if ((i & (1 << j)) > 0){
                    subset.add(startList.get(j));
                }
            }
            results.add(subset);
        }
        return new ArrayList<>();
    }
}
