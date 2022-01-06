## READ_ME

Pour executer ce projet il faut se placer à l'origine du dosssier "semweb".

Ce projet a été réalisé en utilisant Spring et Jena.

<b>Attention!</b> La connection à l'adresse "http://localhost:8083/" déclenche l'update du triple store <b>"DataSem"</b> en lisant <b>"semweb\src\main\resources\static\data\20211116-daily-sensor-measures.csv"</b> ,non présent sur ce Git pour des raisons évidentes de bonnes pratiques, et en traitant la platefor Territoire et un site météorologique.

Pour se connecter sans provoquer la mise à jour de la base de données il faut accèder à "http://localhost:8083/accueil"