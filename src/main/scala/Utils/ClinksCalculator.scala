package Utils

/** Contains the function necessary to calculate the number of *clinks* when n
  * people want to cheers.
  */
object ClinksCalculator:
  /** Calculate the factorial of a given number
    * @param n
    *   the number to compute
    * @return
    *   n!
    */
  // TODO - Part 1 Step 1
  def factorial(n: Int): BigInt =
    if n == 0 then 1
    else n * factorial(n - 1)

  /** Calculate the combination of two given numbers
    * @param n
    *   the first number
    * @param k
    *   the second number
    * @return
    *   n choose k
    */
  // TODO - Part 1 Step 1
  def calculateCombination(n: Int, k: Int): Int =
    // CHECK conversion long to int ok
    (factorial(n) / (factorial(k) * factorial(n - k))).toInt
end ClinksCalculator
