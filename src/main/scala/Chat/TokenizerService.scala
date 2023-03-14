package Chat

import Chat.Token.*
import Utils.SpellCheckerService

class TokenizerService(spellCheckerSvc: SpellCheckerService):

  /** Separate the user's input into tokens
    * @param input
    *   The user's input
    * @return
    *   A Tokenizer which allows iteration over the tokens of the input
    */
  // TODO - Part 1 Step 3
  def tokenize(input: String): Tokenized =
    // 0. remove punctuation  (. , ! ? *) and replace ' and long space by a simple space
    // 1. split the input by space
    // 2. normalize each word by using the dictionnary (spellCheckerSvc -> getClosestWordInDictionary)
    // 3. for each word, find his equivalent token and create a tuple (word, token)

    var words = input
      .replaceAll("[.,!?*]", "")
      .replaceAll("'", " ")
      .replaceAll(" {2,}", " ")
      .split(" ")
      .map(word => spellCheckerSvc.getClosestWordInDictionary(word))
      .map(word => (word, convertWordToToken(word)))

    return TokenizedImpl(words)

  def convertWordToToken(word: String): Token =
    word match
      case w if w.matches("[0-9]+") => Token.NUM
      case w if w.startsWith("_")   => Token.PSEUDO
      case "bonjour"                => Token.BONJOUR
      case "je"                     => Token.JE
      case "etre"                   => Token.ETRE
      case "vouloir"                => Token.VOULOIR
      case "asssoiffe"              => Token.ASSOIFFE
      case "affame"                 => Token.AFFAME
      case "biere"                  => Token.PRODUIT
      case "croissant"              => Token.PRODUIT
      case "et"                     => Token.ET
      case "ou"                     => Token.OU
      case "svp"                    => Token.SVP
      case "bad"                    => Token.BAD
      case _                        => Token.UNKNOWN

end TokenizerService
