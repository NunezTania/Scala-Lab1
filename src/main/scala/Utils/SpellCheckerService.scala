package Utils

trait SpellCheckerService:
  /** This dictionary is a Map object that contains valid words as keys and
    * their normalized equivalents as values (e.g. we want to normalize the
    * words "veux" and "aimerais" in one unique term: "vouloir").
    */
  val dictionary: Map[String, String]

  /** Calculate the Levenstein distance between two words.
    * @param s1
    *   the first word
    * @param s2
    *   the second word
    * @return
    *   an integer value, which indicates the Levenstein distance between "s1"
    *   and "s2"
    */
  def stringDistance(s1: String, s2: String): Int

  /** Get the syntactically closest word in the dictionary from the given
    * misspelled word, using the "stringDistance" function. If the word is a
    * number or a pseudonym, this function just returns it.
    * @param misspelledWord
    *   the mispelled word to correct
    * @return
    *   the closest normalized word from "mispelledWord"
    */
  def getClosestWordInDictionary(misspelledWord: String): String
end SpellCheckerService

class SpellCheckerImpl(val dictionary: Map[String, String])
    extends SpellCheckerService:
  def stringDistance(s1: String, s2: String): Int =
    val l1 = s1.length()
    val l2 = s2.length()
    if l1.min(l2) == 0 then l1.max(l2)
    else if s1.charAt(0) == s2.charAt(0) then
      stringDistance(s1.substring(1), s2.substring(1))
    else
      1 + stringDistance(s1.substring(1), s2)
        .min(stringDistance(s1, s2.substring((1))))
        .min(stringDistance(s1.substring(1), s2.substring(1)))

  // TODO - Part 1 Step 2
  def getClosestWordInDictionary(misspelledWord: String): String =
    if misspelledWord.matches("[0-9]+") || misspelledWord.startsWith("_") then
      misspelledWord
    else dictionary(dictionary.keys.minBy(stringDistance(_, misspelledWord)))

end SpellCheckerImpl
