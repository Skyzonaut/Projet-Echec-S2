# Projet-Echec-S2

# Présentation

Projet de jeu d'échec en Java pour la fin de semestre 2. Le projet à été réalisé en binôme :
- Yohan Folliot : 90%
- Melissa BenSaada : 10%

Le but est de travailler laa programmation orientée objet (POO) et d'offir un projet "bac-à-sable" dans lequel il est possible d'ajouter autant de fonctionnalité qu'on ne le souhaite. 

Ce projet était à réaliser en plus des heures de cours, avec un délai maximal de 3 mois.

## Contenu

Le jeu devait au minimum contenir les fonctionnalités suivantes :
- Un vérificateur de coup
- Une interface console permettant de voir l'échiquier et de remplir les coups souhaités
- La possibilité de sauvegarder l'état de l'échiquier

La version final de ce projet (personnel) contient les fonctionnalités suivantes :
- Validation des coups
- Propositions de coups
- Interface graphique (via JavaFx, lien dans les sources)
- Proposition de coups directement sur l'interface
- Gestion des échecs
- IA pouvant anticiper sur 1 ou 2 coups. Chaque pion se voit donner un coût, et l'IA cherchera le coup lui permettant de prendre le pion ennemi valant le plus, et uniquement si le sacrifice de sa pièce vaut la prise. Ainsi l'IA n'ira jamais prendre un pion avec une Reine si cela la met en danger direct.
- IA pouvant sauver son Roi, et cherchant à mettre en Echec et Mat
- Gestion des sauvegardes en JSON

## Sources
JavaFx : https://openjfx.io/
