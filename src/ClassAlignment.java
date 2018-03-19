
public class ClassAlignment {

  public String run(int[] array1, int[] array2) {

    if (array1.length < 1 || array2.length < 1) {
      return "";
    }

    int smallest1 = array1[0];
    int largest1 = array1[0];

    for (int i = 1; i < array1.length; i++) {
      if (array1[i] > largest1)
        largest1 = array1[i];
      else if (array1[i] < smallest1)
        smallest1 = array1[i];
    }

    int smallest2 = array2[0];
    int largest2 = array2[0];

    for (int i = 1; i < array2.length; i++) {
      if (array2[i] > largest2)
        largest2 = array2[i];
      else if (array2[i] < smallest2)
        smallest2 = array2[i];
    }

    if (Math.abs(smallest1 - largest2) > Math.abs(largest1 - smallest2)) {
      return smallest1 + " : " + largest2;
    } else {
      return largest1 + " : " + smallest2;
    }
  }
}
