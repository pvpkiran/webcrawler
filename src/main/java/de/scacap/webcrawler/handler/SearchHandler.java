package de.scacap.webcrawler.handler;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*+
   Generic Interface to handle REST calls. Have used default methods so that
   we can re use this if we were to use another implementation(for example  : Google Custom Search API)
   of the interface.
 */
public interface SearchHandler {

  List<String> handleSearchAndGetTop5Libraries(final String keyword);

  default void addToMap (final Map<String, Integer> jsMap, final String js) {
    if(jsMap.containsKey(js))
      jsMap.put(js, jsMap.get(js) +1);
    else
      jsMap.put(js, 1);
  }

  // To handle cases like jquery.js?ver=1.4.5 and jquery.min.js
  default String handleDuplication(final String substring) {
    final  String afterVersionRemoval = substring.contains("?") ? substring.substring(0, substring.indexOf("?")) : substring;

    String finalString = afterVersionRemoval;
    if(afterVersionRemoval.contains(".min")) {
      int minIndex = afterVersionRemoval.indexOf(".min");
      finalString = afterVersionRemoval.substring(0, minIndex).concat(afterVersionRemoval.substring(minIndex + 4));
    }

    return finalString;
  }

  default Map<String,Integer> sortByValues(final Map<String,Integer> map){
    final List<Map.Entry<String,Integer>> entries = new LinkedList<>(map.entrySet());

    final Comparator<Map.Entry<String, Integer>> comparing = Comparator.comparing(Map.Entry::getValue);
    entries.sort(comparing.reversed()); // To have values in decreasing order.

    return entries
        .stream()
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));
  }
}
