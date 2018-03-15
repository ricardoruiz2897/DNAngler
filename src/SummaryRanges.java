import java.util.ArrayList;
import java.util.List;

public class SummaryRanges {

  public List<String> run(int[] nums) {

    List<String> ans = new ArrayList<>();

    if (nums.length == 0)
      return ans;

    Integer a = nums[0], b = nums[0];

    for (int i = 0; i < nums.length; i++) {

      if (i == nums.length - 1) {

        if (a == nums[i]) {
          ans.add(a.toString());
        } else {
          ans.add(a + " : " + b);
        }

      } else if (nums[i] + 1 != nums[i + 1]) {

        if (a >= b) {
          ans.add(a.toString());

        } else {

          ans.add(a + " : " + b);
        }

        a = nums[i + 1];

      } else {
        b = nums[i + 1];
      }
    }

    return ans;
  }
}
