## Scala Laboratoire 4
### Bot-Tender : Future
#### Auteurs: Tania Nunez et Magali Egger
#### Introduction
Dans le cadre de ce laboratoire, il nous a été demandé d'ajouter des futures pour simuler la préparation de chaque élément d'une commande.
Elle prend un temps variable et peut échouer ou réussir. Les produits de même type et marque ne peuvent pas être préparés en même temps.

#### Choix d'implémentation
Pour mettre en place un tel comportement, nous avons ajouté une collection de type [TrieMap](https://scala-lang.org/api/3.2.2/scala/collection/concurrent/TrieMap.html) dans la classe ProductService. Elle contient, pour chaque produit spécifique, le dernier future créé, correspondant à la derniere commande d'un produit de ce type en cours. Nous avons ajouté une méthode prepareProducts, qui prends en paramètre une liste de produit et retourne un future de list de Int, indiquant combien de produit de chaque a pu être préparé. Pour chaque produit, cette méthode vérifie d'abord si il n'y a pas une commande déjà en cours (qu'il y a un future à la clé correspondante), si c'est le cas, le future renvoyé déppendra de ce future initial, sinon il sera créé indépendemment. Cette méthode a le comportement suivant : Pour chaque produit, une méthode récursive est appelée pour créer une chaîne de Future, dont on retourne le dernier. Plus précisémment, si on commande 3 bières, cette méthode crée un premier Future[Unit] qui va prendre un temps variable, sur lequel attendra un deuxième Future[Int]. Puis la méthode récursive est à nouveau appelée pour chaîner un autre Future[Unit] sur lequel attends un autre Future[Int], jusqu'à avoir 3 Future[Int]. Au final on obtiendra :

Fu1 -> Fi1 -> Fu2 -> Fi2 -> Fu3 -> Fi3

Où Fu est un Future[Unit] et Fi un Future[Int]. Chaque Future[Int] aura une valeur qui dépendra du succès ou de l'échec du Future[Unit] précédent. La méthode retourne le dernier future Fi3.

Cette méthode est appelée dans la classe AnalyzerService, dans laquelle laquelle le future est traité pour effectuer les diverses vérifications et traitements (voir si le user a un solde suffisant, débiter le compte) pour tranformer ce future en future de string, qui est le message a envoyer dans MessagesRoute.
La fonction reply a donc été adaptée pour renvoyer un tuple (String, Option[Future[String]]). La première string est le message immédiat (dans le cas d'une commande, la confirmation que la commande a été reçue, sinon le message de réponse). Le deuxième champs du tuple est le future message indiquant le résultat de la commande.
La fonction reply est toujours appelée dans MessagesRoute et appelle map sur le Future[String] pour envoyer le message lorsqu'il est complété.