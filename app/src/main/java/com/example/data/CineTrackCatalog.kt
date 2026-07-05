package com.example.data

data class CatalogItem(
    val title: String,
    val genre: String,
    val mediaType: String, // "FILM", "SERIE", "ANIME"
    val rating: Int, // 1 to 5
    val overview: String
)

object CineTrackCatalog {
    val items = listOf(
        // FILMS
        CatalogItem(
            title = "57 seconde",
            genre = "Science-Fiction / Thriller",
            mediaType = "FILM",
            rating = 3,
            overview = "Franklin, un jeune blogueur technologique courageux, découvre par pur hasard un anneau temporel mystérieux d'origine inconnue qui a le pouvoir extraordinaire de le faire remonter le temps de précisément 57 secondes dans le passé. Voyant là une opportunité unique d'obtenir justice, il décide d'utiliser cet artefact révolutionnaire pour infiltrer, saboter et détruire de l'intérieur l'empire pharmaceutique ultra-puissant et corrompu responsable de la tragédie personnelle qui a brisé sa famille."
        ),
        CatalogItem(
            title = "A Serbian Film",
            genre = "Horreur / Thriller psychologique",
            mediaType = "FILM",
            rating = 3,
            overview = "Un acteur de films pour adultes sur le déclin accepte de participer à un projet artistique mystérieux, pour se retrouver piégé dans un cauchemar indicible."
        ),
        CatalogItem(
            title = "Armageddon",
            genre = "Action / Catastrophe / Sci-Fi",
            mediaType = "FILM",
            rating = 4,
            overview = "Lorsqu'un astéroïde de la taille gigantesque du Texas menace de s'écraser directement sur la Terre et d'anéantir toute trace de vie humaine, la NASA conçoit un plan de la dernière chance. L'agence spatiale fait appel à Harry Stamper, le meilleur foreur de pétrole au monde, et à son équipe de têtes brûlées excentriques. Leur mission suicide : s'entraîner en urgence absolue pour aller dans l'espace, se poser sur l'astéroïde, y forer un puits de 250 mètres de profondeur pour y déposer une charge nucléaire et le faire exploser de l'intérieur."
        ),
        CatalogItem(
            title = "Blink Twice",
            genre = "Thriller / Mystère",
            mediaType = "FILM",
            rating = 4,
            overview = "Une jeune serveuse de cocktail est invitée par un milliardaire de la tech sur son île privée mystérieuse, où les fêtes idylliques cachent une vérité terrifiante."
        ),
        CatalogItem(
            title = "Bone Tomahawk",
            genre = "Western / Horreur",
            mediaType = "FILM",
            rating = 4,
            overview = "Dans le Far West sauvage, après qu'un groupe de villageois a été sauvagement enlevé au milieu de la nuit, un shérif vieillissant mais déterminé s'allie avec un adjoint excentrique, un cow-boy blessé et un dandy arrogant. Ensemble, ils se lancent dans une traque désespérée et impitoyable à travers des territoires hostiles pour affronter une tribu de sauvages cannibales troglodytes oubliés de tous, dont les méthodes de torture dépassent l'entendement."
        ),
        CatalogItem(
            title = "Boss Level",
            genre = "Action / Science-Fiction",
            mediaType = "FILM",
            rating = 4,
            overview = "Roy Pulver, un ancien agent d'élite des forces spéciales, se retrouve inexplicablement piégé dans une boucle temporelle infinie et mortelle. Chaque jour, dès son réveil, il est traqué sans relâche par une horde d'assassins professionnels tous plus créatifs et sadiques les uns que les autres. Pour briser ce cycle sans fin et sauver son ex-femme ainsi que son jeune fils, il doit apprendre de chacune de ses morts violentes, décoder les secrets du projet scientifique secret sur lequel travaillait sa femme, et vaincre le chef de la conspiration."
        ),
        CatalogItem(
            title = "Cannibal Holocaust",
            genre = "Horreur / Found Footage",
            mediaType = "FILM",
            rating = 3,
            overview = "Une équipe de documentaristes disparaît dans la jungle amazonienne. Les bobines de film retrouvées révèlent les horreurs qu'ils ont commises et subies."
        ),
        CatalogItem(
            title = "Cave Ambush",
            genre = "Aventure / Action",
            mediaType = "FILM",
            rating = 3,
            overview = "Une mission d'exploration spéléologique tourne au cauchemar lorsque l'équipe se retrouve prise au piège dans un réseau de grottes inondées et hostiles."
        ),
        CatalogItem(
            title = "Caveat",
            genre = "Horreur / Thriller",
            mediaType = "FILM",
            rating = 4,
            overview = "Un homme souffrant de perte de mémoire partielle accepte de veiller sur une jeune femme psychologiquement instable dans une maison isolée sur une île."
        ),
        CatalogItem(
            title = "Coach Carter",
            genre = "Drame / Sport",
            mediaType = "FILM",
            rating = 4,
            overview = "L'histoire vraie de Ken Carter, entraîneur de basket-ball dans un lycée difficile, qui exigea de ses joueurs d'excellents résultats scolaires sous peine de boycotter les matchs."
        ),
        CatalogItem(
            title = "Cohérence",
            genre = "Science-Fiction / Thriller",
            mediaType = "FILM",
            rating = 4,
            overview = "Le passage d'une comète perturbe la réalité d'un groupe d'amis réunis pour un dîner, révélant des univers parallèles et des doubles menaçants."
        ),
        CatalogItem(
            title = "Cold Skin",
            genre = "Fantastique / Horreur",
            mediaType = "FILM",
            rating = 4, // 3.5 represented as 4
            overview = "Sur une île déserte du cercle polaire, un météorologue et un gardien de phare doivent s'unir chaque nuit pour repousser des assauts de créatures marines humanoïdes."
        ),
        CatalogItem(
            title = "Collateral",
            genre = "Thriller / Policier",
            mediaType = "FILM",
            rating = 4,
            overview = "Un chauffeur de taxi de Los Angeles est pris en otage par un tueur à gages méthodique qui l'oblige à le conduire de contrat en contrat tout au long de la nuit."
        ),
        CatalogItem(
            title = "Comme un prince",
            genre = "Comédie",
            mediaType = "FILM",
            rating = 3,
            overview = "Un boxeur talentueux mais colérique se retrouve condamné à des travaux d'intérêt général au Château de Chambord, où il va encadrer des jeunes en insertion."
        ),
        CatalogItem(
            title = "Creepshow",
            genre = "Horreur / Comédie noire",
            mediaType = "FILM",
            rating = 3,
            overview = "Une anthologie horrifique de cinq histoires terrifiantes inspirées des bandes dessinées cultes des années 50, réalisée par George A. Romero."
        ),
        CatalogItem(
            title = "Da Vinci Code",
            genre = "Thriller / Mystère",
            mediaType = "FILM",
            rating = 4,
            overview = "Le célèbre symbologue Robert Langdon est entraîné dans une enquête policière au Louvre qui le mène sur la piste d'un secret millénaire caché dans les œuvres de Léonard de Vinci."
        ),
        CatalogItem(
            title = "Danny Balint",
            genre = "Drame",
            mediaType = "FILM",
            rating = 2,
            overview = "Le parcours tragique d'un jeune néo-nazi juif à New York, déchiré entre sa haine violente et ses origines religieuses profondes."
        ),
        CatalogItem(
            title = "Destination finale",
            genre = "Horreur / Slasher",
            mediaType = "FILM",
            rating = 4,
            overview = "Après avoir échappé à un crash aérien grâce à une prémonition, un groupe de lycéens est traqué un par un par la Mort bien décidée à récupérer ses victimes."
        ),
        CatalogItem(
            title = "Detachment",
            genre = "Drame",
            mediaType = "FILM",
            rating = 4,
            overview = "Un enseignant suppléant s'efforce de rester distant émotionnellement dans un lycée public difficile, tout en se liant d'amitié avec ses élèves et collègues en détresse."
        ),
        CatalogItem(
            title = "District 9",
            genre = "Science-Fiction / Action",
            mediaType = "FILM",
            rating = 4,
            overview = "Des extraterrestres réfugiés sur Terre sont confinés dans un bidonville en Afrique du Sud. Un agent gouvernemental contaminé commence à se transformer en l'un d'eux."
        ),
        CatalogItem(
            title = "Drunk",
            genre = "Drame / Comédie",
            mediaType = "FILM",
            rating = 1,
            overview = "Quatre amis professeurs de lycée décident de mettre en pratique la théorie d'un psychiatre selon laquelle l'homme naîtrait avec un léger déficit d'alcool dans le sang."
        ),
        CatalogItem(
            title = "Equilibrium",
            genre = "Science-Fiction / Action",
            mediaType = "FILM",
            rating = 3, // 2.5 represented as 3
            overview = "Dans une société dystopique du futur où les émotions sont interdites et supprimées par une drogue quotidienne, un agent d'élite décide d'arrêter son traitement."
        ),
        CatalogItem(
            title = "Exit 8",
            genre = "Thriller / Mystère",
            mediaType = "FILM",
            rating = 3,
            overview = "Inspiré du célèbre jeu d'anomalies, un homme se retrouve piégé dans un couloir infini de métro et doit repérer les moindres bizarreries pour s'échapper."
        ),
        CatalogItem(
            title = "Eyes Wide Shut",
            genre = "Drame / Thriller",
            mediaType = "FILM",
            rating = 4,
            overview = "Un médecin new-yorkais s'embarque dans une odyssée nocturne obsessionnelle après que sa femme lui a avoué avoir eu des fantasmes extrêmes pour un autre homme."
        ),
        CatalogItem(
            title = "Gérald le conquérant",
            genre = "Comédie",
            mediaType = "FILM",
            rating = 1,
            overview = "Le portrait décalé et absurde d'un passionné d'histoire locale normande bien décidé à accomplir ses rêves de grandeur."
        ),
        CatalogItem(
            title = "Glass",
            genre = "Thriller / Fantastique",
            mediaType = "FILM",
            rating = 3,
            overview = "Le garde de sécurité David Dunn utilise ses capacités surnaturelles pour traquer la personnalité destructrice de Kevin Wendell Crumb, sous l'œil de l'énigmatique Elijah Price."
        ),
        CatalogItem(
            title = "Grave Encounters",
            genre = "Horreur / Found Footage",
            mediaType = "FILM",
            rating = 3,
            overview = "L'équipe d'une émission de télé-réalité sur le paranormal s'enferme volontairement pour la nuit dans un hôpital psychiatrique abandonné à la réputation terrifiante."
        ),
        CatalogItem(
            title = "Hell House LLC",
            genre = "Horreur / Found Footage",
            mediaType = "FILM",
            rating = 4,
            overview = "Un groupe de créateurs de maisons hantées d'Halloween s'installe dans un vieil hôtel abandonné. Le soir de l'ouverture, un drame inexpliqué cause de nombreux morts."
        ),
        CatalogItem(
            title = "Hérédité",
            genre = "Horreur / Drame",
            mediaType = "FILM",
            rating = 1,
            overview = "Après la mort de la matriarche, une famille commence à découvrir des secrets terrifiants sur leur lignée, déclenchant une spirale de cauchemars paranormaux."
        ),
        CatalogItem(
            title = "Hostel",
            genre = "Horreur / Slasher",
            mediaType = "FILM",
            rating = 3,
            overview = "Deux étudiants américains voyageant à travers l'Europe de l'Est se retrouvent piégés dans une auberge de jeunesse slovaque qui cache un réseau de torture sadique."
        ),
        CatalogItem(
            title = "Hostile Takeover",
            genre = "Action / Thriller",
            mediaType = "FILM",
            rating = 3,
            overview = "Un groupe terroriste hautement entraîné prend le contrôle d'un gratte-ciel d'affaires. Un agent de sécurité infiltré est le seul espoir de survie des otages."
        ),
        CatalogItem(
            title = "Inception",
            genre = "Science-Fiction / Action",
            mediaType = "FILM",
            rating = 5,
            overview = "Un voleur spécialisé dans l'extraction de secrets par les rêves se voit offrir une dernière chance de retrouver sa famille s'il parvient à réaliser une insertion d'idée."
        ),
        CatalogItem(
            title = "Matrix",
            genre = "Science-Fiction / Action",
            mediaType = "FILM",
            rating = 5,
            overview = "Un programmeur informatique découvre que notre réalité n'est qu'une simulation virtuelle gérée par des machines intelligentes ayant asservi l'humanité."
        ),
        CatalogItem(
            title = "Insomnia",
            genre = "Thriller / Policier",
            mediaType = "FILM",
            rating = 4,
            overview = "Un détective chevronné envoyé en Alaska pour élucider le meurtre d'une adolescente tue accidentellement son partenaire et se retrouve harcelé par le tueur."
        ),
        CatalogItem(
            title = "Invasion Los Angeles",
            genre = "Science-Fiction / Action",
            mediaType = "FILM",
            rating = 5,
            overview = "Un ouvrier découvre des lunettes de soleil spéciales qui lui permettent de voir que la société est secrètement contrôlée par des extraterrestres."
        ),
        CatalogItem(
            title = "Irréversible",
            genre = "Drame / Thriller / Horreur",
            mediaType = "FILM",
            rating = 4,
            overview = "Une nuit tragique à Paris où deux amis décident de faire justice eux-mêmes pour venger l'agression sauvage de la femme qu'ils aiment."
        ),
        CatalogItem(
            title = "Juré N°2",
            genre = "Drame / Thriller judiciaire",
            mediaType = "FILM",
            rating = 4,
            overview = "Pendant un procès pour meurtre, un juré réalise qu'il est peut-être lui-même à l'origine de l'accident mortel reproché à l'accusé."
        ),
        CatalogItem(
            title = "Keeper",
            genre = "Drame",
            mediaType = "FILM",
            rating = 3,
            overview = "Un gardien de prison se bat au quotidien pour concilier sa vie de famille et la rudesse de son métier dans un établissement surpeuplé."
        ),
        CatalogItem(
            title = "L'alarme fatale",
            genre = "Comédie / Parodie",
            mediaType = "FILM",
            rating = 3,
            overview = "Une parodie désopilante des films d'action policiers des années 90, menée par un duo d'inspecteurs complètement loufoques."
        ),
        CatalogItem(
            title = "L'associé du diable",
            genre = "Thriller / Fantastique",
            mediaType = "FILM",
            rating = 4,
            overview = "Un jeune avocat brillant et invaincu de Floride est recruté par un prestigieux cabinet new-yorkais dirigé par le mystérieux et charismatique John Milton."
        ),
        CatalogItem(
            title = "L'Homme qui a vu l'ours...",
            genre = "Comédie / Court-métrage",
            mediaType = "FILM",
            rating = 3,
            overview = "Une rumeur absurde et hilarante se répand dans un petit village de campagne au sujet d'une bête sauvage imaginaire."
        ),
        CatalogItem(
            title = "L'Homme qui rétrécit",
            genre = "Science-Fiction",
            mediaType = "FILM",
            rating = 3, // 2.5 represented as 3
            overview = "Après avoir été exposé à un brouillard radioactif, un homme commence à perdre de la taille chaque jour, transformant son quotidien en combat de survie."
        ),
        CatalogItem(
            title = "La neuvième porte",
            genre = "Thriller / Fantastique",
            mediaType = "FILM",
            rating = 3, // 2.5 represented as 3
            overview = "Un chercheur de livres rares est engagé pour authentifier un manuel de démonologie écrit en collaboration avec Lucifer lui-même."
        ),
        CatalogItem(
            title = "La plate-forme",
            genre = "Science-Fiction / Thriller",
            mediaType = "FILM",
            rating = 4,
            overview = "Dans une prison verticale futuriste, une dalle chargée de nourriture descend d'étage en étage, affamant les détenus situés au plus bas."
        ),
        CatalogItem(
            title = "La vague",
            genre = "Drame / Thriller",
            mediaType = "FILM",
            rating = 4,
            overview = "Pour expliquer le fonctionnement d'une dictature, un professeur de lycée lance une expérience sociale grandeur nature qui va rapidement échapper à son contrôle."
        ),
        CatalogItem(
            title = "Lake Mungo",
            genre = "Horreur / Faux docu",
            mediaType = "FILM",
            rating = 3,
            overview = "Après la noyade accidentelle de leur fille Alice, ses parents commencent à suspecter qu'elle hante toujours la maison familiale."
        ),
        CatalogItem(
            title = "Le nom de la rose",
            genre = "Drame / Policier historique",
            mediaType = "FILM",
            rating = 4,
            overview = "En 1327, dans une abbaye bénédictine isolée, un moine franciscain et son jeune novice enquêtent sur une série de morts suspectes liées à un manuscrit interdit."
        ),
        CatalogItem(
            title = "Le nouveau testament",
            genre = "Comédie / Fantastique",
            mediaType = "FILM",
            rating = 4,
            overview = "Dieu existe et vit à Bruxelles. Odieux avec sa femme et sa fille, cette dernière décide de pirater son ordinateur et de divulguer les dates de décès de toute l'humanité."
        ),
        CatalogItem(
            title = "Le prestige",
            genre = "Drame / Mystère / Thriller",
            mediaType = "FILM",
            rating = 5, // 4.5 -> 5
            overview = "Dans le Londres de la fin du XIXe siècle, deux illusionnistes surdoués se livrent une guerre acharnée pour créer le tour de magie ultime."
        ),
        CatalogItem(
            title = "Le secret des Marrowbone",
            genre = "Drame / Mystère / Horreur",
            mediaType = "FILM",
            rating = 5,
            overview = "Quatre frères et sœurs cachent la mort de leur mère pour ne pas être séparés, mais une présence obscure semble hanter leur immense demeure isolée."
        ),
        CatalogItem(
            title = "Légion",
            genre = "Action / Fantastique",
            mediaType = "FILM",
            rating = 3,
            overview = "L'archange Michel descend sur Terre pour protéger le dernier espoir de l'humanité contre l'apocalypse déclenchée par Dieu."
        ),
        CatalogItem(
            title = "Les promesses de l'ombre",
            genre = "Policier / Thriller",
            mediaType = "FILM",
            rating = 3,
            overview = "Une sage-femme londonienne enquête sur la mort d'une jeune fille russe et se retrouve confrontée à la terrible mafia rouge d'Europe de l'Est."
        ),
        CatalogItem(
            title = "Les yeux du diable",
            genre = "Documentaire",
            mediaType = "FILM",
            rating = 3,
            overview = "Une investigation profonde sur les réseaux ésotériques et les légendes urbaines occultes qui alimentent les peurs modernes."
        ),
        CatalogItem(
            title = "Libre et assoupi",
            genre = "Comédie",
            mediaType = "FILM",
            rating = 3,
            overview = "Sébastien n'a qu'une ambition dans la vie : ne rien faire. Un hymne à la paresse et au bonheur d'être libre et assoupi."
        ),
        CatalogItem(
            title = "Manuel de survie a l'apocalypse zombie",
            genre = "Comédie / Horreur",
            mediaType = "FILM",
            rating = 3,
            overview = "Trois scouts d'enfance s'associent à une serveuse intrépide pour sauver leur ville d'une invasion soudaine de morts-vivants."
        ),
        CatalogItem(
            title = "Midsommar",
            genre = "Horreur / Thriller",
            mediaType = "FILM",
            rating = 2,
            overview = "Un jeune couple américain se rend dans un village reculé de Suède pour participer à un festival de solstice d'été qui vire au culte païen violent."
        ),
        CatalogItem(
            title = "Mords moi sans hésitation",
            genre = "Comédie / Parodie",
            mediaType = "FILM",
            rating = 3,
            overview = "Une parodie déchaînée de la saga Twilight, se moquant joyeusement des vampires scintillants et des loups-garous romantiques."
        ),
        CatalogItem(
            title = "Never back down",
            genre = "Action / Drame / Arts Martiaux",
            mediaType = "FILM",
            rating = 4,
            overview = "Un adolescent rebelle s'initie au combat libre (MMA) dans sa nouvelle école pour affronter la brute locale qui l'a humilié."
        ),
        CatalogItem(
            title = "Ni dieux ni maîtres",
            genre = "Documentaire historique",
            mediaType = "FILM",
            rating = 2,
            overview = "Une fresque historique remarquable retraçant les origines et l'évolution du mouvement anarchiste à travers le monde."
        ),
        CatalogItem(
            title = "Nobody",
            genre = "Action / Thriller",
            mediaType = "FILM",
            rating = 5, // 4.5 -> 5
            overview = "Un père de famille en apparence ordinaire révèle son passé d'auditeur militaire ultra-violent après un cambriolage à son domicile."
        ),
        CatalogItem(
            title = "Obsession",
            genre = "Horreur",
            mediaType = "FILM",
            rating = 3,
            overview = "Une femme est traquée par une entité surnaturelle vindicative qui prend le contrôle de son esprit et de ses peurs les plus intimes."
        ),
        CatalogItem(
            title = "Oddity",
            genre = "Horreur / Thriller",
            mediaType = "FILM",
            rating = 4,
            overview = "Une médium aveugle cherche à élucider le meurtre mystérieux de sa sœur jumelle dans leur maison de campagne isolée à l'aide d'objets occultes."
        ),
        CatalogItem(
            title = "Old",
            genre = "Thriller / Fantastique",
            mediaType = "FILM",
            rating = 3,
            overview = "Une famille passe ses vacances sur une plage isolée et réalise avec effroi que le lieu accélère mystérieusement le processus de vieillissement."
        ),
        CatalogItem(
            title = "Passenger",
            genre = "Science-Fiction / Romance",
            mediaType = "FILM",
            rating = 1, // 0 -> 1
            overview = "Deux passagers d'un vaisseau spatial voyageant vers une lointaine colonie sont réveillés de leur sommeil artificiel 90 ans trop tôt."
        ),
        CatalogItem(
            title = "Planète des Singes",
            genre = "Science-Fiction / Action",
            mediaType = "FILM",
            rating = 4,
            overview = "Des astronautes s'écrasent sur une planète mystérieuse où des singes doués de parole règnent en maîtres absolus sur des humains réduits à l'état sauvage."
        ),
        CatalogItem(
            title = "Pop rédemption",
            genre = "Comédie",
            mediaType = "FILM",
            rating = 2,
            overview = "Les membres d'un groupe de Black Metal en cavale se font passer pour des animateurs de kermesse hippie pour échapper à la police."
        ),
        CatalogItem(
            title = "Prey",
            genre = "Action / Sci-Fi / Horreur",
            mediaType = "FILM",
            rating = 4,
            overview = "Trois siècles avant notre ère, une jeune guerrière Comanche affronte un redoutable prédateur extraterrestre doté d'un arsenal ultra-sophistiqué."
        ),
        CatalogItem(
            title = "Serenity",
            genre = "Science-Fiction / Thriller",
            mediaType = "FILM",
            rating = 5, // 4.5 -> 5
            overview = "Un capitaine de bateau de pêche menant une vie tranquille dans les Caraïbes est rattrapé par son passé lorsque son ex-femme le supplie de la sauver."
        ),
        CatalogItem(
            title = "Shot caller",
            genre = "Drame / Policier",
            mediaType = "FILM",
            rating = 4,
            overview = "Un homme d'affaires prospère condamné à la prison après un accident tragique est contraint de s'endurcir pour survivre au milieu des gangs."
        ),
        CatalogItem(
            title = "Sirat",
            genre = "Drame",
            mediaType = "FILM",
            rating = 3,
            overview = "Un drame familial poignant explorant les non-dits et le pardon entre plusieurs générations d'une même famille."
        ),
        CatalogItem(
            title = "Sisters",
            genre = "Horreur / Thriller",
            mediaType = "FILM",
            rating = 3,
            overview = "Une journaliste mène l'enquête sur un meurtre sauvage commis sous ses yeux, soupçonnant l'une des sœurs siamoises vivant en face."
        ),
        CatalogItem(
            title = "Sous écrous",
            genre = "Comédie / Action",
            mediaType = "FILM",
            rating = 2,
            overview = "Deux petits délinquants se retrouvent incarcérés et doivent rivaliser d'ingéniosité absurde pour supporter le quotidien de la prison."
        ),
        CatalogItem(
            title = "Sound of freedom",
            genre = "Drame / Thriller",
            mediaType = "FILM",
            rating = 4,
            overview = "Un ancien agent fédéral se lance dans une mission périlleuse pour sauver des centaines d'enfants victimes de trafic d'êtres humains en Colombie."
        ),
        CatalogItem(
            title = "Tenet",
            genre = "Science-Fiction / Action",
            mediaType = "FILM",
            rating = 4,
            overview = "Un agent secret est recruté pour manipuler l'écoulement du temps afin de prévenir une menace globale pire qu'une troisième guerre mondiale."
        ),
        CatalogItem(
            title = "Terrified",
            genre = "Horreur",
            mediaType = "FILM",
            rating = 4,
            overview = "Dans un quartier paisible de Buenos Aires, des phénomènes paranormaux violents et inexplicables poussent des spécialistes du paranormal à enquêter."
        ),
        CatalogItem(
            title = "The amateur",
            genre = "Action / Espionnage",
            mediaType = "FILM",
            rating = 4, // 3.5 -> 4
            overview = "Un cryptographe de la CIA dévasté par la mort de sa femme décide de faire chanter l'agence pour obtenir l'autorisation de traquer lui-même les terroristes."
        ),
        CatalogItem(
            title = "The Bay",
            genre = "Horreur / Science-Fiction",
            mediaType = "FILM",
            rating = 3,
            overview = "Un virus biologique mutant ravage une petite ville côtière du Maryland. Un montage de vidéos retrouvées révèle l'étendue du cauchemar."
        ),
        CatalogItem(
            title = "The call of the wild",
            genre = "Aventure / Drame",
            mediaType = "FILM",
            rating = 3,
            overview = "Le destin extraordinaire de Buck, un chien au grand cœur dont la vie est bouleversée lorsqu'il est arraché à son foyer pour devenir chien de traîneau dans le Yukon."
        ),
        CatalogItem(
            title = "The Fix",
            genre = "Thriller / Drame",
            mediaType = "FILM",
            rating = 3,
            overview = "Une plongée haletante dans l'envers du décor des médias et de la manipulation de l'opinion publique lors d'un scandale politique."
        ),
        CatalogItem(
            title = "The island",
            genre = "Science-Fiction / Action",
            mediaType = "FILM",
            rating = 4,
            overview = "Deux résidents d'une colonie utopique et aseptisée découvrent qu'ils ne sont en réalité que des clones destinés à servir de pièces de rechange à leurs originaux."
        ),
        CatalogItem(
            title = "The Man from earth",
            genre = "Science-Fiction / Drame",
            mediaType = "FILM",
            rating = 3, // 2.5 -> 3
            overview = "Lors de son pot de départ, un professeur d'université révèle à ses collègues stupéfaits qu'il est en réalité un homme préhistorique âgé de 14 000 ans."
        ),
        CatalogItem(
            title = "The taking of Deborah logan",
            genre = "Horreur / Found Footage",
            mediaType = "FILM",
            rating = 4,
            overview = "Une étudiante filme un documentaire sur la maladie d'Alzheimer d'une vieille dame, pour réaliser que ses crises cachent une possession démoniaque."
        ),
        CatalogItem(
            title = "The thin red line",
            genre = "Guerre / Drame",
            mediaType = "FILM",
            rating = 4,
            overview = "Pendant la bataille de Guadalcanal dans le Pacifique, les hommes de la compagnie C luttent autant pour leur survie physique que pour garder leur humanité."
        ),
        CatalogItem(
            title = "The Truman show",
            genre = "Drame / Comédie",
            mediaType = "FILM",
            rating = 5, // 4.5 -> 5
            overview = "Truman Burbank mène une vie paisible sans savoir que son quotidien est entièrement mis en scène et diffusé en direct 24h/24 comme une émission de télé-réalité mondiale."
        ),
        CatalogItem(
            title = "The visit",
            genre = "Horreur / Thriller",
            mediaType = "FILM",
            rating = 3,
            overview = "Deux adolescents sont envoyés passer une semaine de vacances chez leurs grands-parents maternels qu'ils n'ont jamais vus, découvrant rapidement leur comportement extrêmement inquiétant."
        ),
        CatalogItem(
            title = "Together",
            genre = "Horreur / Drame",
            mediaType = "FILM",
            rating = 2,
            overview = "Une famille isolée fait face à une mystérieuse épidémie nocturne qui altère la perception et pousse les infectés à la violence extrême."
        ),
        CatalogItem(
            title = "Undisputed",
            genre = "Action / Arts Martiaux",
            mediaType = "FILM",
            rating = 4,
            overview = "Le champion du monde de boxe poids lourds est incarcéré dans un pénitencier de haute sécurité où se déroulent des combats clandestins impitoyables."
        ),
        CatalogItem(
            title = "Une famille de batards",
            genre = "Comédie",
            mediaType = "FILM",
            rating = 1,
            overview = "Une comédie noire et déjantée suivant les mésaventures d'une fratrie dysfonctionnelle prête à toutes les bassesses pour capter un héritage fictif."
        ),
        CatalogItem(
            title = "V/H/S (2012)",
            genre = "Horreur / Anthologie",
            mediaType = "FILM",
            rating = 4,
            overview = "Un groupe de marginaux est engagé pour cambrioler une maison abandonnée et y récupérer une mystérieuse cassette vidéo, découvrant des enregistrements terrifiants."
        ),
        CatalogItem(
            title = "Vermines",
            genre = "Horreur / Thriller",
            mediaType = "FILM",
            rating = 2,
            overview = "Les résidents d'un immeuble de banlieue parisienne délabré se retrouvent confinés alors que des araignées venimeuses à la reproduction ultra-rapide envahissent les lieux."
        ),
        CatalogItem(
            title = "Will hunting",
            genre = "Drame",
            mediaType = "FILM",
            rating = 5,
            overview = "Un jeune génie de la banlieue de Boston travaillant comme concierge au MIT refuse d'exploiter ses dons en mathématiques jusqu'au jour où un psychologue l'aide."
        ),
        CatalogItem(
            title = "Yin yang Master",
            genre = "Fantastique / Action",
            mediaType = "FILM",
            rating = 4,
            overview = "Un maître du yin et du yang s'allie à un garde déchu pour repousser une armée de démons menaçant l'équilibre du monde des humains."
        ),
        CatalogItem(
            title = "Yoroï",
            genre = "Action / Fantastique",
            mediaType = "FILM",
            rating = 4,
            overview = "Un guerrier solitaire découvre une armure légendaire hantée par les esprits de ses ancêtres samouraïs, lui conférant des pouvoirs mystiques hors du commun."
        ),
        CatalogItem(
            title = "You should have left",
            genre = "Horreur / Thriller",
            mediaType = "FILM",
            rating = 3,
            overview = "Un père de famille s'installe avec sa femme et sa fille dans une maison de campagne moderne au Pays de Galles, où le temps et l'espace se déforment."
        ),

        // SÉRIES
        CatalogItem(
            title = "A la croisée des mondes",
            genre = "Fantastique / Aventure",
            mediaType = "SERIE",
            rating = 4,
            overview = "La jeune orpheline Lyra Belacqua part à la recherche de son ami enlevé et découvre une conspiration cosmique impliquant des enfants disparus et la Poussière."
        ),
        CatalogItem(
            title = "A knight of the seven kingdoms",
            genre = "Fantastique / Drame",
            mediaType = "SERIE",
            rating = 4,
            overview = "Les aventures palpitantes de Ser Duncan le Grand et de son jeune écuyer l'Œuf, se déroulant un siècle avant les événements de Game of Thrones."
        ),
        CatalogItem(
            title = "American Gods",
            genre = "Fantastique / Drame",
            mediaType = "SERIE",
            rating = 4,
            overview = "Un ancien prisonnier nommé Shadow Moon devient le garde du corps du mystérieux Mr. Wednesday, mêlé à une guerre entre les anciens et les nouveaux dieux."
        ),
        CatalogItem(
            title = "Barry",
            genre = "Comédie noire / Policier",
            mediaType = "SERIE",
            rating = 4,
            overview = "Un tueur à gages dépressif du Midwest se rend à Los Angeles pour un contrat et se découvre une passion salvatrice pour le théâtre amateur."
        ),
        CatalogItem(
            title = "Black list",
            genre = "Policier / Thriller",
            mediaType = "SERIE",
            rating = 4,
            overview = "Le criminel le plus recherché du monde se rend mystérieusement au FBI et propose de dénoncer ses anciens complices en travaillant avec une profileuse."
        ),
        CatalogItem(
            title = "Common side effects",
            genre = "Animation / Sci-Fi / Comédie",
            mediaType = "SERIE",
            rating = 5,
            overview = "Une série animée hilarante et subversive suivant deux chercheurs découvrant un remède universel qui possède des effets secondaires complètement absurdes."
        ),
        CatalogItem(
            title = "Halo",
            genre = "Science-Fiction / Action",
            mediaType = "SERIE",
            rating = 4, // 3.5 -> 4
            overview = "Au XXVIe siècle, l'humanité mène une guerre désespérée contre une alliance extraterrestre, menée par le super-soldat Master Chief."
        ),
        CatalogItem(
            title = "His dark materials",
            genre = "Fantastique / Aventure",
            mediaType = "SERIE",
            rating = 4,
            overview = "Une adaptation riche et fidèle de la trilogie de Philip Pullman, où deux enfants traversent des mondes parallèles magnifiques pour combattre une autorité théocratique."
        ),
        CatalogItem(
            title = "La palma",
            genre = "Drame / Thriller",
            mediaType = "SERIE",
            rating = 3,
            overview = "Un thriller dramatique tendu se déroulant sur l'île de La Palma, mêlant secrets de famille inavoués et menaces environnementales imminentes."
        ),
        CatalogItem(
            title = "Le prophète",
            genre = "Drame / Mystère",
            mediaType = "SERIE",
            rating = 4,
            overview = "Une enquête policière et spirituelle fascinante autour d'un homme charismatique affirmant délivrer des messages divins à une communauté isolée."
        ),
        CatalogItem(
            title = "Lost",
            genre = "Aventure / Drame / Mystère",
            mediaType = "SERIE",
            rating = 5,
            overview = "Après le crash de leur avion sur une île déserte du Pacifique, les survivants doivent s'organiser pour survivre tout en découvrant que l'île cache de sombres secrets."
        ),
        CatalogItem(
            title = "Monarch",
            genre = "Science-Fiction / Action",
            mediaType = "SERIE",
            rating = 4,
            overview = "Deux frères et sœurs explorent le passé secret de leur famille pour découvrir ses liens profonds avec l'organisation Monarch et les Titans légendaires."
        ),
        CatalogItem(
            title = "Pamela rose",
            genre = "Comédie / Policier",
            mediaType = "SERIE",
            rating = 3,
            overview = "Les célèbres agents du FBI décalés Richard Bullit et Douglas Riper reprennent du service pour résoudre une enquête criminelle complètement loufoque."
        ),
        CatalogItem(
            title = "Plaine Orientale",
            genre = "Policier / Drame",
            mediaType = "SERIE",
            rating = 4,
            overview = "Une immersion réaliste et tendue au cœur du grand banditisme en Corse, suivant les luttes d'influence entre familles rivales et forces de l'ordre."
        ),
        CatalogItem(
            title = "The great",
            genre = "Comédie / Historique",
            mediaType = "SERIE",
            rating = 4,
            overview = "Une satire anachronique et jubilatoire retraçant l'ascension de Catherine la Grande dans la Russie du XVIIIe siècle pour renverser l'empereur Pierre III."
        ),
        CatalogItem(
            title = "The librarians",
            genre = "Aventure / Fantastique",
            mediaType = "SERIE",
            rating = 3,
            overview = "Une équipe de bibliothécaires d'élite parcourt le monde pour sécuriser des reliques magiques ancestrales et protéger l'humanité des menaces."
        ),
        CatalogItem(
            title = "The witcher",
            genre = "Fantastique / Action",
            mediaType = "SERIE",
            rating = 4,
            overview = "Le sorceleur Geralt de Riv, un chasseur de monstres mutant, se bat pour trouver sa place dans un monde médiéval cruel où les humains se révèlent monstrueux."
        ),
        CatalogItem(
            title = "Tom clancy's Jack Ryan",
            genre = "Action / Espionnage",
            mediaType = "SERIE",
            rating = 4,
            overview = "L'analyste de la CIA Jack Ryan est propulsé sur le terrain pour la première fois pour traquer un terroriste émergent menaçant la sécurité mondiale."
        ),
        CatalogItem(
            title = "Tulsa King",
            genre = "Policier / Drame",
            mediaType = "SERIE",
            rating = 3,
            overview = "Un parrain de la mafia new-yorkaise tout juste libéré après 25 ans de prison est exilé à Tulsa, en Oklahoma, où il commence à bâtir un nouvel empire."
        ),
        CatalogItem(
            title = "West World",
            genre = "Science-Fiction / Thriller",
            mediaType = "SERIE",
            rating = 3,
            overview = "Dans un parc d'attractions futuriste peuplé d'androïdes réalistes, les visiteurs humains peuvent assouvir tous leurs désirs jusqu'au réveil des machines."
        ),

        // ANIMÉS
        CatalogItem(
            title = "Démon Slave",
            genre = "Action / Fantastique / Shōnen",
            mediaType = "ANIME",
            rating = 4,
            overview = "Dans un monde où des portes vers une dimension démoniaque s'ouvrent, des combattantes d'élite acquièrent des pouvoirs magiques en asservissant des démons."
        ),
        CatalogItem(
            title = "Full métal alchimiste",
            genre = "Action / Fantastique / Drame",
            mediaType = "ANIME",
            rating = 5,
            overview = "Deux frères alchimistes parcourent le monde à la recherche de la légendaire Pierre Philosophale pour réparer l'erreur tragique qui a brisé leurs corps."
        ),
        CatalogItem(
            title = "Nukitashi",
            genre = "Comédie / Érotique",
            mediaType = "ANIME",
            rating = 3,
            overview = "Une comédie romantique et impertinente se déroulant sur une île aux règles de séduction complètement folles et hors normes."
        )
    )
}
