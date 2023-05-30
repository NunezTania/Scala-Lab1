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
  def tokenize(input: String): Tokenized =
    // 0. remove punctuation  (. , ! ? *) and replace ' and long space by a simple space
    // 1. split the input by space
    // 2. normalize each word by using the dictionnary
    // 3. for each word, find his equivalent token and create a tuple (word, token)

    var words = input
      .replaceAll("[.,!?*]", "")
      .replaceAll("'", " ")
      .replaceAll(" {2,}", " ")
      .split(" ")
      .filter(word => word.nonEmpty)
      .map(word => spellCheckerSvc.getClosestWordInDictionary(word))
      .map(word => (word, convertWordToToken(word)))

    return TokenizedImpl(words)

  /** Convert a normalized word to a token
    * @param word
    *   The word to convert
    * @return
    *   The token corresponding to the word
    */
  def convertWordToToken(word: String): Token =
    word match
      case w if w.matches("[0-9]+") => Token.NUM
      case w if w.startsWith("_")   => Token.PSEUDO
      case "bonjour"                => Token.BONJOUR
      case "je"                     => Token.JE
      case "etre"                   => Token.ETRE
      case "vouloir"                => Token.VOULOIR
      case "assoiffe"               => Token.ASSOIFFE
      case "affame"                 => Token.AFFAME
      case "biere"                  => Token.PRODUIT
      case "croissant"              => Token.PRODUIT
      case "et"                     => Token.ET
      case "ou"                     => Token.OU
      case "svp"                    => Token.SVP
      case "farmer"                 => Token.MARQUE
      case "boxer"                  => Token.MARQUE
      case "wittekop"               => Token.MARQUE
      case "punkipa"                => Token.MARQUE
      case "jackhammer"             => Token.MARQUE
      case "tenebreuse"             => Token.MARQUE
      case "maison"                 => Token.MARQUE
      case "cailler"                => Token.MARQUE
      case "connaitre"              => Token.CONNAITRE
      case "combien"                => Token.COMBIEN
      case "commander"              => Token.COMMANDER
      case "appeler"                => Token.APPELER
      case "me"                     => Token.ME
      case "mon"                    => Token.MON
      case "quel"                   => Token.QUEL
      case "couter"                 => Token.COUTER
      case "sante"                  => Token.SANTE
      case "solde"                  => Token.SOLDE
      case "bad"                    => Token.BAD
      case "prix"                   => Token.PRIX
      case "le"                     => Token.LE
      case "de"                     => Token.DE
      case _                        => Token.UNKNOWN

end TokenizerService
