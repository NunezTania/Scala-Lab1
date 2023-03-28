package Chat

enum Token:
  case // Terms
    BONJOUR,
    JE,
    SVP,
    ME,
    MON,
    ASSOIFFE,
    AFFAME,
    // Actions
    ETRE,
    VOULOIR,
    COMMANDER,
    APPELER,
    // Logic Operators
    ET,
    OU,
    // Products
    PRODUIT,
    //Marques
    MARQUE,
    // Util
    PSEUDO,
    NUM,
    SOLDE,

    // Nouveaux tokens labo 2
    QUEL,
    COMBIEN,
    CONNAITRE,
    COUTER,
    SANTE,
    // Nouveaux tokens labo 2

    EOL,
    UNKNOWN,
    BAD
end Token
