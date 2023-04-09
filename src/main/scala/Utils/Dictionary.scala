package Utils

/** Contains the dictionary of the application, which is used to validate,
  * correct and normalize words entered by the user.
  */
object Dictionary:
  // This dictionary is a Map object that contains valid words as keys and their normalized equivalents as values (e.g.
  // we want to normalize the words "veux" and "aimerais" in one unique term: "vouloir").
  val dictionary: Map[String, String] = Map(
    "bonjour" -> "bonjour",
    "hello" -> "bonjour",
    "yo" -> "bonjour",
    "je" -> "je",
    "j" -> "je",
    "suis" -> "etre",
    "est" -> "etre",
    "veux" -> "vouloir",
    "aimerais" -> "vouloir",
    "assoiffe" -> "assoiffe",
    "assoiffee" -> "assoiffe",
    "affame" -> "affame",
    "affamee" -> "affame",
    "biere" -> "biere",
    "bieres" -> "biere",
    "croissant" -> "croissant",
    "croissants" -> "croissant",
    "et" -> "et",
    "ou" -> "ou",
    "svp" -> "svp",
    "stp" -> "svp",
    "maison" -> "maison",
    "cailler" -> "cailler",
    "farmer" -> "farmer",
    "boxer" -> "boxer",
    "wittekop" -> "wittekop",
    "punkipa" -> "punkipa",
    "jackhammer" -> "jackhammer",
    "ténébreuse" -> "tenebreuse",
    "connaître" -> "connaitre",
    "combien" -> "combien",
    "commander" -> "commander",
    "appeler" -> "appeler",
    "solde" -> "solde",
    "coûte" -> "couter",
    "mon" -> "mon",
    "me" -> "me",
    "m" -> "me",
    "prix" -> "prix",
    "le" -> "le",
    "de" -> "de",
    "quel" -> "quel"
  )
end Dictionary
